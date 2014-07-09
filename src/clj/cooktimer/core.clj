(ns cooktimer.core
  (:require [hiccup.page :as h]
            [cooktimer.css :refer [page-css]]
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
     [:title "Cook Timer"]
     [:link {:rel "stylesheet" :href "/style.css"}]
     [:link {:rel "stylesheet" :href "http://fonts.googleapis.com/css?family=Roboto:400,500,300"}]]
    (cons
      [:body]
      (concat body dev-js))))

(def page-body
  [[:div.font-display-4 "Cook Timer"]
   [:div#my-app]])

(defroutes main-routes
  (GET "/" [] (layout page-body {}))
  (GET "/style.css" [] (-> (response page-css)
                           (content-type "text/css")))
  (route/resources "/"))

(def app (handler/site main-routes))

(defonce server (run-jetty #'app {:port 3001 :join? false}))
