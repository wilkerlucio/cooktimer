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
  nil)

(defn recipe-component [[_ recipe] _]
  (reify
    om/IDidMount
    (did-mount [_]
      (go-loop []
        (om/transact! recipe #(assoc % :title (rand-int 1000)))
        (<! (timeout 100))
        (recur)))

    om/IRender
    (render [_]
      (dom/div #js {:className "timeline"}
        (:title recipe)))))

(defn render-recipes [recipes]
  (om/build-all recipe-component recipes))

(defn app-component [data _]
  (reify
    om/IRender
    (render [_]
      (apply dom/div #js {:className "container"}
        (render-recipes (:recipes data))))))

(defn init [state]
  (om/root app-component (atom state)
           {:target (.getElementById js/document "my-app")}))

(def recipe {:title "Pasta"
             :stops (create-stops {:at 120 :message "put on the water"}
                                  {:at 180 :message "go check it"}
                                  {:at 420 :message "take it off"})})

(defn generate-recipes [n]
  (let [col (interleave (range) (repeat recipe))]
    (apply sorted-map (take (* n 2) col))))

(init {:recipes (generate-recipes 10)})
