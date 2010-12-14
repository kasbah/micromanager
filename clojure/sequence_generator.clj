(ns sequence-generator)

(defstruct channel :name :exposure :z-offset :use-z-stack :skip-frames)

(defstruct stage-position :stage-device :axes)

(defstruct acq-settings :frames :positions :channels :slices :slices-first
  :time-first :keep-shutter-open-slices :keep-shutter-open-channels
  :use-autofocus :autofocus-skip :relative-slices :exposure :interval-ms)

(defn pairs [x]
  (partition 2 1 (concat x [nil])))

(defn make-dimensions [settings]
  (let [{:keys [slices channels frames positions
                slices-first time-first]} settings
        a [[slices :slice :slice-index] [channels :channel :channel-index]]
        a (if slices-first a (reverse a))
        b [[frames :frame :frame-index] [positions :position :position-index]]
        b (if time-first b (reverse b))]
    (concat a b)))
        
(defn nest-loop [events dim-vals dim dim-index-kw]
  (if (and dim-vals (pos? (count dim-vals)))
    (for [i (range (count dim-vals)) event events]
      (assoc event
        dim-index-kw i
        dim (nth dim-vals i)))
    (map #(assoc % dim-index-kw 0) events)))

(defn create-loops [dimensions]
  (reduce #(apply (partial nest-loop %1) %2) [{:task :snap}] dimensions))

(defn make-main-loops [settings]
  (create-loops (make-dimensions settings)))

(defn process-skip-z-stack [events slices]
  (if (pos? (count slices))
    (let [middle-slice (nth slices (int (/ (count slices) 2)))]
      (filter
        #(or
           (nil? (% :channel))
           (-> % :channel :use-z-stack)
           (= middle-slice (% :slice)))
        events)))
    events)

(defn manage-shutter [events keep-shutter-open-channels keep-shutter-open-slices]
  (for [[e1 e2] (pairs events)]
    (assoc e1 :close-shutter
      (if e2 (or
               (and
                 (not keep-shutter-open-channels)
                 (not= (e1 :channel) (e2 :channel)))
               (and
                 (not keep-shutter-open-slices)
                 (not= (e1 :slice) (e2 :slice)))
               (not= (e1 :frame) (e2 :frame))
               (not= (e1 :position) (e2 :position)))
        true))))

(defn process-channel-skip-frames [events]
  (filter
    #(or
       (nil? (% :channel))
       (-> % :channel :skip-frames zero?)
       (not= 0 (mod (% :frame) (-> % :channel :skip-frames inc))))
    events))

(defn process-use-autofocus [events use-autofocus autofocus-skip]
  (for [event events]
    (assoc event :autofocus
      (and use-autofocus
        (or
          (nil? (event :frame))
          (zero? (mod (event :frame) (inc autofocus-skip))))))))

(defn process-wait-time [events interval-ms]
  (cons
    (assoc (first events) :wait-time-ms 0)
    (for [[e1 e2] (pairs events) :when e2]
      (if (not= (:frame e1) (:frame e2))
        (assoc e2 :wait-time-ms interval-ms)
        e2))))

(defn generate-acq-sequence [settings]
  (let [{:keys [slices keep-shutter-open-channels keep-shutter-open-slices
         use-autofocus autofocus-skip interval-ms]} settings]
    (-> (make-main-loops settings)
      (process-skip-z-stack slices)
      (manage-shutter keep-shutter-open-channels keep-shutter-open-slices)
      (process-channel-skip-frames)
      (process-use-autofocus use-autofocus autofocus-skip)
      (process-wait-time interval-ms)
      )))

; Testing:

(def my-channels
  [(struct channel "Cy3" 100 0 true 0)
   (struct channel "Cy5"  50 0 false 0)
   (struct channel "DAPI" 50 0 true 0)])

(def default-settings
  (struct-map acq-settings
    :frames (range 100) :positions [{:name "a" :x 1 :y 2} {:name "b" :x 4 :y 5}]
    :channels my-channels :slices (range 5)
    :slices-first true :time-first true
    :keep-shutter-open-slices false :keep-shutter-open-channels true
    :use-autofocus true :autofocus-skip 3 :relative-slices true :exposure 100
    :interval-ms 1000))


(def test-settings
  (struct-map acq-settings
    :frames (range 10) :positions [{:name "a" :x 1 :y 2} {:name "b" :x 4 :y 5}]
    :channels my-channels :slices (range 5)
    :slices-first true :time-first true
    :keep-shutter-open-slices false :keep-shutter-open-channels true
    :use-autofocus true :autofocus-skip 3 :relative-slices true :exposure 100
    :interval-ms 100))

(def null-settings
  (struct-map acq-settings
    :frames (range 96)
    :positions (range 1536)
    :channels [(struct channel "Cy3" 100 0 true 0)
               (struct channel "Cy5"  50 0 true 0)]
    :slices (range 5)
    :slices-first true :time-first false
    :keep-shutter-open-slices false :keep-shutter-open-channels true
    :use-autofocus true :autofocus-skip 3 :relative-slices true :exposure 100
    :interval-ms 100))

;(def result (generate-acq-sequence null-settings))

;(count result)