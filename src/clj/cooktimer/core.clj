(ns cooktimer.core
  (:require [hiccup.page :as h]
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
     [:title "Cook Time"]]
    (into
      [:body]
      (concat body dev-js))))

(def page-body
  [[:div#my-app]])

(defroutes main-routes
  (GET "/" [] (layout page-body {}))
  (route/resources "/"))

(def app (handler/site main-routes))

(defonce server (run-jetty #'app {:port 3001 :join? false}))
