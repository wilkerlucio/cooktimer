(ns cooktimer.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [chan <! >! timeout]]
            cooktimer.repl))

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

(defn current-time [] (-> (js/Date.) .getTime))

(defn elapsed-from
  ([start-time offset]
    (-> (current-time)
        (- start-time)
        (+ offset))))

(defn start [timer]
  (om/transact! timer #(assoc % :state :running))
  (let [start-time (current-time)
        offset (:elapsed @timer)]
    (go-loop []
      (om/transact! timer #(assoc % :elapsed (elapsed-from start-time offset)))
      (<! (timeout 100))
      (if (= (:state @timer) :running) (recur)))))

(defn stop [timer]
  (om/transact! timer #(assoc % :state :paused)))

(defn recipe-component [[_ timer] _]
  (reify
    om/IRender
    (render [_]
      (let [total (total-time timer)
            elapsed (:elapsed timer)
            progress (* (/ elapsed total) 100)]
        (dom/div #js {:className "recipe flex-row"}
          (dom/div #js {:className "flex flex-column"}
            (dom/div nil (:title timer))
            (dom/div #js {:className "flex flex-column timeline"}
              (dom/div #js {:className "fill"
                            :style #js {:width (str progress "%")}})))
          (case (:state timer)
            :paused
              (dom/button #js {:onClick #(start timer)} "Start")
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

(def recipe {:title "Pasta"
             :stops (create-stops {:at 120000 :message "put on the water"}
                                  {:at 180000 :message "go check it"}
                                  {:at 420000 :message "take it off"})})

(defn generate-recipes [n]
  (let [col (interleave (range) (repeat (timer recipe)))]
    (apply sorted-map (take (* n 2) col))))

(init {:recipes (generate-recipes 10)})
