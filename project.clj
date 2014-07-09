(defproject cooktimer "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2268"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]
                 [om "0.6.4"]
                 [hiccup "1.0.5"]
                 [compojure "1.1.8"]
                 [ring/ring-jetty-adapter "1.1.0"]
                 [garden "1.1.8"]]

  :plugins [[lein-cljsbuild "1.0.3"]
            [jarohen/simple-brepl "0.1.1"]]

  :cljsbuild {
    :builds {
      :dev {
        :source-paths ["src/cljs"]
        :compiler {
          :output-dir "resources/public/out"
          :output-to "resources/public/main.js"
          :optimizations :none
          :source-map true}}}})
