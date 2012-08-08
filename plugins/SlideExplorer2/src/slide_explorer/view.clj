(ns slide-explorer.view
  (:import (javax.swing JFrame JPanel JLabel JTextArea)
           (java.awt AlphaComposite Color Graphics Graphics2D Rectangle RenderingHints Window)
           (java.awt.event ComponentAdapter KeyEvent KeyAdapter
                            WindowAdapter)
           (ij.process ByteProcessor ImageProcessor))
  (:require [clojure.pprint :as pprint]
            [slide-explorer.disk :as disk]
            [slide-explorer.reactive :as reactive]
            [slide-explorer.cache :as cache]
            [clojure.core.memoize :as memo])
  (:use [org.micromanager.mm :only (edt)]
        [slide-explorer.paint :only (enable-anti-aliasing repaint repaint-on-change)]
        [slide-explorer.tiles :only (center-tile floor-int)]
        [slide-explorer.image :only (crop insert-half-tile overlay)]
        [slide-explorer.user-controls :only (make-view-controllable
                                              setup-fullscreen)]))


(def MIN-ZOOM 1/256)

(def MAX-ZOOM 1)

; Order of operations:
;  Stitch (not done)
;  Crop (not done)
;  Flatten fields (not done)
;  Max intensity projection (z) (not done)
;  Rescale (working)
;  Color Overlay (working)

;; TESTING UTILITIES

(defn reference-viewer
  "Creates a small window that shows the value of a reference
   and upates as that value changes."
  [reference key]
  (let [frame (JFrame. key)
        label (JLabel.)
        update-fn (fn [_ new-state]
                    (edt (.setText label
                                   (str "<html><pre>" 
                                        (with-out-str (pprint/pprint new-state))
                                        "</pre></html>"))))]
    (.add (.getContentPane frame) label)
    (reactive/handle-update reference update-fn)
    (update-fn nil @reference)
    (doto frame
      (.addWindowListener
        (proxy [WindowAdapter] []
          (windowClosing [e]
                         (remove-watch reference key))))
      .show))
  reference)

(defmacro timer [expr]
  `(let [ret# ~expr] ; (time ~expr)]
    ; (print '~expr)
    ; (println " -->" (pr-str ret#))
     ret#))

;; TILE <--> PIXELS

(defn tile-to-pixels [[nx ny] [tile-width tile-height] tile-zoom]
  [(int (* tile-zoom nx tile-width))
   (int (* tile-zoom ny tile-height))])

(defn tile-in-pixel-rectangle?
  [[nx ny] rectangle [tile-width tile-height]]
  (let [nl (floor-int (/ (.x rectangle) tile-width))
        nr (floor-int (/ (+ -1 (.getWidth rectangle) (.x rectangle)) tile-width))
        nt (floor-int (/ (.y rectangle) tile-height))
        nb (floor-int (/ (+ -1 (.getHeight rectangle) (.y rectangle)) tile-height))]
    (and (<= nl nx nr)
         (<= nt ny nb))))   

(defn tiles-in-pixel-rectangle
  "Returns a list of tile indices found in a given pixel rectangle."
  [rectangle [tile-width tile-height]]
  (let [nl (floor-int (/ (.x rectangle) tile-width))
        nr (floor-int (/ (+ -1 (.getWidth rectangle) (.x rectangle)) tile-width))
        nt (floor-int (/ (.y rectangle) tile-height))
        nb (floor-int (/ (+ -1 (.getHeight rectangle) (.y rectangle)) tile-height))]
    (for [nx (range nl (inc nr))
          ny (range nt (inc nb))]
      [nx ny])))

(defn pixel-rectangle
  	  "Converts the screen state coordinates to visible camera pixel coordinates."
  	  [{:keys [x y width height zoom]}]
  	  (Rectangle. (- x (/ width 2 zoom))
                 	    (- y (/ height 2 zoom))
                 	    (/ width zoom)
                 	    (/ height zoom)))

(defn screen-rectangle
  [{:keys [x y width height zoom]}]
  (Rectangle. (- (* x zoom) (/ width 2))
              (- (* y zoom) (/ height 2))
              width
              height))

;; TILING

(defn child-index [n]
  (floor-int (/ n 2)))

(defn child-indices [indices]
  (-> indices
     (update-in [:nx] child-index)
     (update-in [:ny] child-index)
     (update-in [:zoom] / 2)))

(defn propagate-tile [tile-map-atom child parent]
  (let [child-tile (.get (disk/load-tile tile-map-atom child))
        parent-tile (.get (disk/load-tile tile-map-atom parent))
        new-child-tile (insert-half-tile
                         parent-tile
                         [(even? (:nx parent))
                          (even? (:ny parent))]
                         child-tile)]
    (disk/add-tile tile-map-atom child new-child-tile)))

(defn add-to-memory-tiles [tile-map-atom indices tile]
  (let [full-indices (assoc indices :zoom 1)]
    (disk/add-tile tile-map-atom full-indices tile)
    (loop [child (child-indices full-indices)
           parent full-indices]
      (when (<= MIN-ZOOM (:zoom child))
        (propagate-tile tile-map-atom child parent)
        (recur (child-indices child) child)))))

;; OVERLAY

(def overlay-memo (memo/memo-lru overlay 100))

(defn multi-color-tile [memory-tiles-atom tile-indices channels-map]
  (let [channel-names (keys channels-map)
        procs (for [chan channel-names]
                (let [tile-index (assoc tile-indices :nc chan)]
                  ;(swap! memory-tiles-atom cache/hit-item tile-index)
                  (get @memory-tiles-atom tile-index)))
        lut-maps (map channels-map channel-names)]
    (overlay-memo procs lut-maps)))

;; PAINTING

(defn draw-image [^Graphics2D g image x y]
  (.drawImage g image x y nil))

(defn paint-tiles [^Graphics2D g overlay-tiles-atom screen-state [tile-width tile-height]]
  (let [pixel-rect (.getClipBounds g)]
    (doseq [[nx ny] (tiles-in-pixel-rectangle pixel-rect
                                              [tile-width tile-height])]
      (let [tile-index {:nc :overlay
                         :zoom (screen-state :zoom)
                         :nx nx :ny ny :nt 0
                         :nz (screen-state :z)}]
      (when-let [image (get
                         @overlay-tiles-atom
                         tile-index)]
        (swap! overlay-tiles-atom cache/hit-item tile-index)
        (let [[x y] (tile-to-pixels [nx ny] [tile-width tile-height] 1)]
          (draw-image g image x y)))))))
	

(defn paint-screen [graphics screen-state overlay-tiles-atom]
  (let [original-transform (.getTransform graphics)
        zoom (:zoom screen-state)
        x-center (/ (screen-state :width) 2)
        y-center (/ (screen-state :height) 2)
        [tile-width tile-height] [512 512]]
    (doto graphics
      (.setClip 0 0 (:width screen-state) (:height screen-state))
      (.translate (- x-center (int (* (:x screen-state) zoom)))
                  (- y-center (int (* (:y screen-state) zoom))))
      (paint-tiles overlay-tiles-atom screen-state [tile-width tile-height])
      enable-anti-aliasing)))

;; Loading visible tiles

(defn visible-loader
  "Loads tiles needed for drawing."
  [screen-state-atom memory-tile-atom overlay-tiles-atom acquired-images]
  (let [visible-tile-positions (tiles-in-pixel-rectangle
                                 (screen-rectangle @screen-state-atom)
                                 [512 512])]
    (doseq [[nx ny] visible-tile-positions
            channel (keys (:channels @screen-state-atom))]
     (let [tile {:nx nx
                  :ny ny
                  :zoom (@screen-state-atom :zoom)
                  :nc channel
                  :nz (@screen-state-atom :z)
                  :nt 0}]
        (disk/load-tile memory-tile-atom tile)
       (let [overlay-tile-coords (assoc tile :nc :overlay)]
        (swap! overlay-tiles-atom
               cache/add-item 
               overlay-tile-coords
               (multi-color-tile memory-tile-atom tile
                                 (:channels @screen-state-atom))))))))

(defn load-visible-only
  "Runs visible-loader whenever screen-state-atom changes."
  [screen-state-atom memory-tile-atom
   overlay-tiles-atom acquired-images]
  (let [react-fn (fn [_ _] (visible-loader screen-state-atom memory-tile-atom
                                           overlay-tiles-atom acquired-images))
        agent (agent {})]
    (def agent1 agent)
    (reactive/handle-update
      memory-tile-atom
      (fn [old new] (when-not (identical? (with-meta old nil) (with-meta new nil)))
                      (react-fn old new))
      agent)
    (reactive/handle-update
      screen-state-atom
      react-fn
      agent)
    (reactive/handle-update
      acquired-images
      react-fn
      agent)))
  
;; MAIN WINDOW AND PANEL

(defn control-panel []
  (doto
    (proxy [JPanel] []
      (paintComponent [^Graphics graphics]
        (proxy-super paintComponent graphics)))
    (.setBackground Color/BLACK)))  

(defn main-panel [screen-state overlay-tiles-atom]
  (doto
    (proxy [JPanel] []
      (paintComponent [^Graphics graphics]
        (proxy-super paintComponent graphics)
        (paint-screen graphics @screen-state overlay-tiles-atom)))
    (.setBackground Color/BLACK)))
    
(defn main-frame []
  (doto (JFrame. "Slide Explorer II")
    .show
    (.setBounds 10 10 500 500)))

(defn show [memory-tiles acquired-images]
  (let [screen-state (atom (sorted-map :x 0 :y 0 :z 0 :zoom 1
                                       :width 100 :height 10
                                       :keys (sorted-set)
                                       :channels (sorted-map))
                                       :update 0)
        ;overlay-tiles (atom {})
        overlay-tiles (atom (cache/empty-lru-map 100))
        panel (main-panel screen-state overlay-tiles)
        frame (main-frame)]
    (def mt memory-tiles)
    (def ss screen-state)
    (def f frame)
    (def pnl panel)
    (def ot overlay-tiles)
    (def ai acquired-images)
    (.add (.getContentPane frame) panel)
    (setup-fullscreen frame)
    (load-visible-only screen-state memory-tiles
                       overlay-tiles acquired-images)
    (repaint-on-change panel screen-state)
    (repaint-on-change panel memory-tiles)
    (repaint-on-change panel overlay-tiles)
    (make-view-controllable panel screen-state)
    ;(handle-open frame)
    screen-state))

