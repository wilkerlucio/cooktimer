(ns cooktimer.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]
                   [cooktimer.macros :refer [go-forever]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [chan <! >! timeout]]
            cooktimer.repl))

(enable-console-print!)

(defn comparator-by [key]
  #(apply compare (map key %&)))

(defn create-stops [& stops]
  (into (sorted-set-by (comparator-by :at)) stops))

(defn add-stop [recipe stop]
  (update-in recipe [:stops] conj stop))

(defn total-time [recipe]
  (-> recipe :stops last :at))

(defn timer [recipe]
  (assoc recipe
    :elapsed 0
    :state :paused
    :pending (:stops recipe)))

(defn current-time []
  (-> (js/Date.) .getTime))

(defn elapsed-from
  ([start-time offset]
    (-> (current-time)
        (- start-time)
        (+ offset))))

(defn start-timer [timer]
  (om/transact! timer #(assoc % :state :running))
  (let [start-time (current-time)
        offset (:elapsed @timer)]
    (go-loop []
      (om/transact! timer #(assoc % :elapsed (elapsed-from start-time offset)))
      (<! (timeout 100))
      (if (= (:state @timer) :running) (recur)))))

(defn stop [timer]
  (om/transact! timer #(assoc % :state :paused)))

;; bonus notice, when using macbook retina, SVG drawing is much smoother than plain HTML blocks
(defn render-progress [elapsed total]
  (let [progress (* (/ elapsed total) 100)]
    (dom/svg #js {:className "flex" :style #js {:height "30px"}}
      (dom/rect #js {:width (str progress "%")
                     :height "100%"
                     :fill "green"}))))

(defn recipe-component [[_ timer] _]
  (reify
    om/IRender
    (render [_]
      (let [total (total-time timer)
            elapsed (:elapsed timer)]
        (dom/div #js {:className "recipe flex-row"}
          (dom/div #js {:className "flex flex-column"}
            (dom/div nil (:title timer))
            (dom/div #js {:className "flex flex-column timeline"}
              (render-progress elapsed total)))
          (case (:state timer)
            :paused
              (dom/button #js {:onClick #(start-timer timer)} "Start")
            :running
              (dom/button #js {:onClick #(stop timer)} "Stop")
            (dom/button nil (str "Invalid " (:state timer)))))))))

(defn app-component [data _]
  (om/component
    (apply dom/div #js {:className "container"}
      (om/build-all recipe-component (:recipes data)))))

(defn init [state]
  (om/root app-component (atom state)
           {:target (.getElementById js/document "my-app")}))

(defn minutes [n]
  (* n 60 1000))

(def frozen-pizza
  {:title "Frozen Pizza"
   :stops (create-stops {:at (minutes 10) :message "put the pizza in"}
                        {:at (minutes 22) :message "take pizza out"})})

(def rice
  {:title "Rice"
   :stops (create-stops {:at (minutes 10) :message "go in"}
                        {:at (minutes 35) :message "go out"})})

(def miojo
  {:title "Miojo"
   :stops (create-stops {:at (minutes 2) :message "water boiling"}
                        {:at (minutes 5) :message "go out"})})

(defn generate-recipes [n]
  (let [col (interleave (range) (repeat (timer frozen-pizza)))]
    (apply sorted-map (take (* n 2) col))))

(defn init-recipes [& recipes]
  (let [col (zipmap (range) (map timer recipes))]
    (into (sorted-map) col)))

(init {:recipes (init-recipes frozen-pizza
                              rice
                              miojo)})


