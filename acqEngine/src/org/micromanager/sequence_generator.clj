; FILE:         sequence_generator.clj
; PROJECT:      Micro-Manager
; SUBSYSTEM:    mmstudio acquisition engine
; ----------------------------------------------------------------------------
; AUTHOR:       Arthur Edelstein, arthuredelstein@gmail.com, 2010-2011
;               Developed from the acq eng by Nenad Amodaj and Nico Stuurman
; COPYRIGHT:    University of California, San Francisco, 2006-2011
; LICENSE:      This file is distributed under the BSD license.
;               License text is included with the source distribution.
;               This file is distributed in the hope that it will be useful,
;               but WITHOUT ANY WARRANTY; without even the implied warranty
;               of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
;               IN NO EVENT SHALL THE COPYRIGHT OWNER OR
;               CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
;               INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES.

(ns org.micromanager.sequence-generator
  (:use [org.micromanager.mm :only [select-values-match? core mmc]]))

(def MAX-Z-TRIGGER-DIST 5.0)

(defstruct acq-settings :frames :positions :channels :slices :slices-first
  :time-first :keep-shutter-open-slices :keep-shuftter-open-channels
  :use-autofocus :autofocus-skip :relative-slices :exposure :interval-ms :custom-intervals-ms)

(defn all-equal?
  ([coll]
    (or (empty? coll)
        (apply = coll)))
  ([val coll]
    (or (empty? coll)
        (apply = val coll))))

(defn pairs
  "Generates a lazy list of pairs in sequence, ending with the last
   value paired with nil."
  [x]
  (partition 2 1 (lazy-cat x (list nil))))

(defn pairs-back
  "Generates a lazy list of pairs in sequence, beginning the (nil first-val)."
  [x]
  (partition 2 1 (lazy-cat (list nil) x)))

(defn if-assoc
  "Associate k,v in a map if pred is true, otherwise
   return the map unchanged."
  [pred m k v]
  (if pred (assoc m k v) m))

(defn make-property-sequences
  "Make a sequence of properties."
  [channel-properties]
  (let [ks (apply sorted-set
                  (apply concat (map keys channel-properties)))]
    (into (sorted-map)
      (for [[d p] ks]
        [[d p] (map #(get % [d p]) channel-properties)]))))

(defn channels-sequenceable
  "Checks if a give property sequence can actually be triggered,
   (and if a set of channels all have the same exposure setting)."
  [property-sequences channels]
  (and
    (not (some false?
           (for [[[d p] s] property-sequences]
             (or (all-equal? s)
                 (and (core isPropertySequenceable d p)
                      (<= (count s) (core getPropertySequenceMaxLength d p)))))))
    (all-equal? (map :exposure channels))))

(defn select-triggerable-sequences
  "Select only those sequences that can and must be triggered."
  [property-sequences]
  (into (sorted-map)
    (filter #(let [[[d p] vs] %]
               (and (core isPropertySequenceable d p)
                    (not (all-equal? vs))))
            property-sequences)))

(defn make-dimensions
  "Create a structure describing the order and extent of each dimension in
   a multi-dimensional acquisition given the acquisition settings generated
   from the Acquisition Dialog input."
  [settings]
  (let [{:keys [slices channels frames positions
                slices-first time-first]} settings
        a [[slices :slice :slice-index] [channels :channel :channel-index]]
        a (if slices-first a (reverse a))
        b [[frames :frame :frame-index] [positions :position :position-index]]
        b (if time-first b (reverse b))]
    (concat a b)))
        
(defn nest-loop
  "Create a loop for a given dimension, as generated by make-dimensions."
  [events dim-vals dim dim-index-kw]
  (if (and dim-vals (pos? (count dim-vals)))
    (for [i (range (count dim-vals)) event events]
      (assoc event
        dim-index-kw i
        dim (if (= dim-index-kw :frame-index) i (get dim-vals i))))
    (map #(assoc % dim-index-kw 0) events)))

(defn create-loops
  "Generate loops of events from a set of dimensions parameters as
   generated by make-dimensinos."
  [dimensions]
  (reduce #(apply (partial nest-loop %1) %2) [{}] dimensions))

(defn make-main-loops
  "Make the sequence of multi-d acq. events using the acquisition settings."
  [settings]
  (create-loops (make-dimensions settings)))

(defn build-event
  "Attach appropriate :exposure and :relative-z (true/false) setting
   to an event, given the acquisition settings map."
  [settings event]
  (assoc event
    :exposure (if (:channel event)
                (get-in event [:channel :exposure])
                (:default-exposure settings))
    :relative-z (:relative-slices settings)))

(defn process-skip-z-stack
  "Remove events corresponding to a channel/slice that should be skipped."
  [events slices]
  (if (pos? (count slices))
    (let [middle-slice (get slices (unchecked-divide-int (count slices) 2))]
      (filter 
        #(or 
           (= middle-slice (% :slice))
           (nil? (% :channel))
           (get-in % [:channel :use-z-stack]))
        events))
    events))

(defn different [e1 e2 selector]
  "Determines where values in two event maps
   (located by selector) are different."
  (not= (get-in e1 selector)
        (get-in e2 selector)))

(defn manage-shutter
  "Attach a [:close-shutter true/false] to each event, corresponding
   to Keep Shutter Open for slices and channels."
  [events keep-shutter-open-channels keep-shutter-open-slices]
  (for [[e1 e2] (pairs events)]
    (let [diff #(different e1 e2 %)]
      (assoc e1 :close-shutter
             (if e2 (or
                      (and
                        (not keep-shutter-open-channels)
                        (diff [:channel]))
                      (and
                        (not keep-shutter-open-slices)
                        (diff [:slice]))
                      (and (diff [:frame-index])
                           (not
                             ;; special case where we rapidly cycle through channels:
                             (and (not (diff [:slice]))
                                  (not (diff [:position-index]))
                                  keep-shutter-open-channels
                                  (let [wait (e2 :wait-time-ms)]
                                    (or (nil? wait) (zero? wait))))))
                      (diff [:position-index])
                      (:autofocus e2)
                      (diff [:channel :properties ["Core" "Shutter"]]))
               true)))))

(defn process-channel-skip-frames
  "Remove events corresponding to a channel/frame that should be skipped."
  [events]
  (filter
    #(or
       (nil? (% :channel))
       (-> % :channel :skip-frames zero?)
       (zero? (mod (% :frame-index) (-> % :channel :skip-frames inc))))
    events))

(defn process-new-position
  "Attach a [:new-position true] entry to events where appropriate."
  [events]
  (for [[e1 e2] (pairs-back events)]
    (assoc e2 :new-position
      (different e1 e2 [:position-index]))))

(defn process-use-autofocus
  "Modify events sequence so that event maps will include an [:autofocus true]
   entry when appropriate."
  [events use-autofocus autofocus-skip]
  (for [[e1 e2] (pairs-back events)]
    (assoc e2 :autofocus
      (and use-autofocus
        (or (not e1)
            (and (zero? (mod (e2 :frame-index) (inc autofocus-skip)))
                 (or (different e1 e2 [:position-index])
                     (different e1 e2 [:frame-index]))))))))

(defn process-wait-time
  "Modify events sequence so that events will contain a nonzero
   value at key :wait-time-ms."
  [events interval]
  (cons
    (assoc (first events) :wait-time-ms (if (vector? interval) (first interval) 0))
    ;if supplied first custom time point is delay before acquisition start
    (for [[e1 e2] (pairs events) :when e2]
      (if-assoc (different e1 e2 [:frame-index])
                e2 :wait-time-ms (if (vector? interval)
                                   (nth interval (:frame-index e2))
                                   interval)))))

(defn event-triggerable
  "Returns true if an event can be added to a burst."
  [burst event]
  (let [n (count burst)
        e1 (peek burst)
        e2 event
        channels (map :channel (conj burst event))
        props (map :properties channels)]
    (and
      (channels-sequenceable (make-property-sequences props) channels)
      (or (= (e1 :slice) (e2 :slice))
          (when-let [z-drive (. mmc getFocusDevice)]
            (and
              (.isStageSequenceable mmc z-drive)
              (< n (.getStageSequenceMaxLength mmc z-drive))
              (<= (Math/abs (- (e1 :slice) (e2 :slice))) MAX-Z-TRIGGER-DIST)
              (<= (e1 :slice-index) (e2 :slice-index))))))))
  
(defn burst-valid
  "Returns true if a pair of events can be included in the 
   same burst."
  [e1 e2]
  (and
    (let [wait-time (:wait-time-ms e2)]
      (or (nil? wait-time) (>= (:exposure e2) wait-time)))
    (select-values-match? e1 e2 [:exposure :position])
    (not (:autofocus e2))
    (not (:runnables e2))))

(defn make-triggers
  "Make a series of trigger sequences from a set of properties
   and/or slices."
  [events]
  (let [props (map #(-> % :channel :properties) events)]
    (merge
      {:properties (-> props make-property-sequences select-triggerable-sequences)}
      (let [slices (map :slice events)]
        (when (and (not (empty? slices))
                   (not (all-equal? slices)))
          {:slices (when (-> events first :slice) slices)})))))

(defn accumulate-burst-event
  "Accumulate a series of events into a burst as long
   as possible. Returns a vector containing the burst 
   event sequence followed by a sequence of events that
   couldn't be included in the burst." 
  [events]
  (loop [remaining-events (next events)
         burst [(first events)]]
    (let [e1 (last burst)
          e2 (first remaining-events)]
      (if (and e1
               (burst-valid e1 e2)
               (event-triggerable burst e2))
        (recur (next remaining-events)
               (conj burst e2))
        [burst remaining-events]))))
      
(defn make-bursts
  "Lazily convert a sequence of events into bursts, when possible."
  [events]
  (lazy-seq
    (let [[burst later] (accumulate-burst-event events)]
      (when burst
        (cons
          (if (< 1 (count burst))
            (assoc (first burst)
                   :task :burst
                   :burst-data burst
                   :burst-length (count burst)
                   :trigger-sequence (make-triggers burst))
            (assoc (first burst) :task :snap))
          (when later
            (make-bursts later)))))))

(defn add-next-task-tags
  "Attach a :next-frame-index entry to each event map."
  [events]
  (for [p (pairs events)]
    (do ;(println p)
        (assoc (first p) :next-frame-index (get (second p) :frame-index)))))

(defn selectively-update-tag
  "Modifies events matched by the sub-map event-template
   by applying update-fn to the value in event at key."
  [events event-template key update-fn]
  (let [ks (keys event-template)]
    (for [event events]
      (if (select-values-match? event event-template ks)
        (update-in event [key] update-fn)
        event))))

(defn selectively-append-runnable
  "Attach runnables to those events matching a give template."
  [events event-template runnable]
  (selectively-update-tag events event-template :runnables
                          #(conj (vec %) runnable)))

(defn attach-runnables
  "Attaches runnables in runnable-list to events."
  [events runnable-list]
  (if (pos? (count runnable-list))
    (recur (apply selectively-append-runnable events (first runnable-list))
           (next runnable-list))
    events))

(defn generate-default-acq-sequence
  "The default generator of multi-d acq. sequences. Generates a sequence of events,
   and modifies this sequence according to acquisition settings provided by user."
  [settings runnables]
  (let [{:keys [slices keep-shutter-open-channels keep-shutter-open-slices
                use-autofocus autofocus-skip interval-ms custom-intervals-ms relative-slices
                runnable-list]} settings]
    (-> (make-main-loops settings)
        (#(map (partial build-event settings) %))
        (process-skip-z-stack slices)
        (process-channel-skip-frames)
        (process-use-autofocus use-autofocus autofocus-skip)
        (process-new-position)
        (process-wait-time (if (first custom-intervals-ms) custom-intervals-ms interval-ms))
        (attach-runnables runnables)
        (manage-shutter keep-shutter-open-channels keep-shutter-open-slices)
        (make-bursts)
        (add-next-task-tags)
        )))

(defn make-channel-metadata
  "Make the metadata property map for each channel."
  [channel]
  (when-let [props (:properties channel)]
    (into {}
      (for [[[d p] v] props]
        [(str d "-" p) v]))))

(defn generate-simple-burst-sequence [numFrames use-autofocus
                                      channels slices default-exposure
                                      property-triggers position-index]
  (println "simple")
  (let [numChannels (max 1 (count channels))
        numSlices (max 1 (count slices))
        numFrames (max 1 numFrames)
        exposure (if-not (empty? channels)
                   (:exposure (first channels))
                   default-exposure)
        raw-events
        (for [f (range numFrames)
              s (range numSlices)
              c (range numChannels)]
          (let [first-plane (and (zero? f) (zero? c))
                last-plane (and (= numFrames (inc f))
                                (= numSlices (inc s))
                                (= numChannels (inc c)))]
            {:next-frame-index (inc f)
             :wait-time-ms 0.0
             :exposure exposure
             :position-index position-index
             :position position-index
             :autofocus (if first-plane use-autofocus false)
             :channel-index c
             :channel (get channels c)
             :slice-index s
             :slice (get slices s)
             :frame-index f
             :metadata (make-channel-metadata (get channels c))}))
        partitioned-events (if (< 1 numSlices)
                             (partition-by :frame-index raw-events)
                             (lazy-seq (list raw-events)))]
    (map (fn [events]
           (assoc (first events)
                  :task :burst
                  :burst-data events
                  :burst-length (count events)
                  :trigger-sequence (merge (make-triggers events) property-triggers)))
         partitioned-events)))
  

(defn generate-multiposition-bursts [positions num-frames use-autofocus
                                     channels slices default-exposure triggers]
  (process-new-position
    (flatten
      (for [pos-index (range (count positions))]
        (map #(assoc % :position-index pos-index
                     :position (nth positions pos-index))
             (generate-simple-burst-sequence
               num-frames use-autofocus channels slices default-exposure triggers pos-index))))))

(defn generate-acq-sequence [settings runnables]
  (let [{:keys [numFrames time-first positions slices channels
                use-autofocus default-exposure interval-ms
                autofocus-skip custom-intervals-ms slices-first]} settings
        property-sequences (make-property-sequences (map :properties channels))
        have-multiple-frames (< 1 numFrames)
        have-multiple-positions (< 1 (count positions))
        have-multiple-slices (< 1 (count slices))
        have-multiple-channels (< 1 (count channels))
        channel-properties-sequenceable (channels-sequenceable property-sequences channels)
        slices-sequenceable (when-let [z-drive (.getFocusDevice mmc)]
                              (.isStageSequenceable mmc z-drive))
        no-channel-skips-frames (all-equal? 0 (map :skip-frames channels))
        all-channels-do-z-stack (all-equal? true (map :use-z-stack channels))]
    (println slices-first)
    (if
      (and
        (or
          have-multiple-frames
          have-multiple-slices
          have-multiple-channels)
        (or
          time-first ; time points at each position
          (not have-multiple-positions))
        (not (and
               have-multiple-slices
               (not slices-sequenceable)))
        (not (and
               have-multiple-channels
               (not channel-properties-sequenceable)))
        (not (and
               have-multiple-channels
               (not no-channel-skips-frames)))
        (not (and
               have-multiple-slices
               have-multiple-channels
               (not all-channels-do-z-stack)))
        (not (and
               have-multiple-slices
               have-multiple-channels
               slices-first))
        (or
          (not use-autofocus)
          (>= autofocus-skip (dec numFrames)))
        (zero? (count runnables))
        (not (first custom-intervals-ms))
        (> default-exposure interval-ms))
      (let [triggers {:properties (select-triggerable-sequences property-sequences)}]
        (if have-multiple-positions
          (generate-multiposition-bursts
            positions numFrames use-autofocus channels slices
            default-exposure triggers)
          (generate-simple-burst-sequence
            numFrames use-autofocus channels slices
            default-exposure triggers 0)))
      (generate-default-acq-sequence settings runnables))))

