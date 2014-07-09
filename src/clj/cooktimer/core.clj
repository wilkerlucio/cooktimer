(ns cooktimer.core
  (:require [hiccup.page :as h]
            [garden.core :refer [css]]
            [ring.util.response :refer [response content-type]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.resource :refer [wrap-resource]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [compojure.handler :as handler]))

(def dev-js
  [[:script {:src "//cdnjs.cloudflare.com/ajax/libs/react/0.9.0/react.min.js"}]
   [:script {:src "out/goog/base.js"}]
   [:script {:src "main.js"}]
   [:script "goog.require('cooktimer.core')"]])

(defn layout [body {:keys [env]}]
  (h/html5
    [:head
     [:title "Cook Time"]
     [:link {:rel "stylesheet" :href "/style.css"}]
     [:link {:rel "stylesheet" :href "http://fonts.googleapis.com/css?family=Roboto"}]]
    (into
      [:body]
      (concat body dev-js))))

(def page-body
  [[:div#my-app]])

(def page-css
  (css
    ["*"
     {:font-family ["'Roboto'" "sans-serif"]}]
    ["button"
     {:background "#fff"
      :border "1px solid #000"
      :cursor "pointer"
      :padding "20px"}]
    [".flex-row" {:display "flex"}]
    [".flex-column" {:display "flex" :flex-direction "column"}]
    [".flex" {:flex "1"}]
    [".recipe"
     {:border "1px solid #000"
      :margin "10px 0"}]
    [".timeline"
     {:padding "5px"}
     [".fill"
      {:background "#3CDD83"
       :flex "1"
       :width "0"}]]))

(defroutes main-routes
  (GET "/" [] (layout page-body {}))
  (GET "/style.css" [] (-> (response page-css)
                           (content-type "text/css")))
  (route/resources "/"))

(def app (handler/site main-routes))

(defonce server (run-jetty #'app {:port 3001 :join? false}))
