(ns cooktimer.css
  (:require [garden.compiler :refer [compile-css]]
            [clojure.walk :as walk]))

(def reset-box-model
  {:margin  0
   :padding 0
   :border  0
   :outline 0})

(def reset-font
  {:font {:weight "inherit"
          :style  "inherit"
          :family "inherit"
          :size   "100%"}
   :vertical-align "baseline"})

(def reset-body
  {:line-height 1
   :color       "black"
   :background  "white"})

(def reset-table
  {:border-collapse "separate"
   :border-spacing  0
   :vertical-align  "middle"})

(def reset-table-cell
  {:text-align     "left"
   :font-weight    "normal"
   :vertical-align "middle"})

(def global-reset
  [[:html :body :div :span :applet :object :iframe
    :h1 :h2 :h3 :h4 :h5 :h6 :p :blockquote :pre
    :a :abbr :acronym :address :big :cite :code
    :del :dfn :em :img :ins :kbd :q :s :samp
    :small :strike :strong :sub :sup :tt :var
    :dl :dt :dd :ol :ul :li
    :fieldset :form :label :legend
    :table :caption :tbody :tfoot :thead :tr :th :td
      (merge reset-box-model reset-font)]
   [:body reset-body]
   [:ol :ul {:list-style "none"}]
   [:table reset-table]
   [:caption :th :td reset-table-cell]
   [:a :img {:border "none"}]])

(def nested-reset
  [[:div :span :object :iframe :h1 :h2 :h3 :h4 :h5 :h6 :p
    :pre :a :abbr :acronym :address :code :del :dfn :em :img
    :dl :dt :dd :ol :ul :li :fieldset :form :label
    :legend :caption :tbody :tfoot :thead :tr
      (merge reset-box-model reset-font)]
   [:table reset-table]
   [:caption :th :td reset-table-cell]
   [:a :img {:border "none"}]])

(def reset-html5
  [[:article :aside :canvas :details :figcaption
    :figure :footer :header :hgroup :menu :nav
    :section :summary :main
      (merge reset-box-model {:display "block"})]
   [:audio :canvas :video
      {:display "inline-block"
       "*display" "inline"
       "*zoom" 1}]
   ["audio:not([controls]),[hidden]" {:display "none"}]])

(def google-typography
  [[:div :span :object :iframe :h1 :h2 :h3 :h4 :h5 :h6 :p
    :pre :a :abbr :acronym :address :code :del :dfn :em :img
    :dl :dt :dd :ol :ul :li :fieldset :form :label
    :legend :caption :tbody :tfoot :thead :tr :button
     {:font-family [["'Roboto'" "sans-serif"]]}]

   [".font-display-4" {:font {:size "112px"
                              :weight 300}}]
   [".font-display-3" {:font-size "56px"}]
   [".font-display-2" {:font-size "45px"}]
   [".font-display-1" {:font-size "34px"}]
   [".font-headline" {:font-size "24px"}]
   [".font-title" {:font {:size "20px"
                          :weight 500}}]
   [".font-subhead" {:font-size "15px"}]
   [".font-body-2" {:font {:size "13px"
                           :weight 500}}]
   [".font-body-1" {:font-size "13px"}]
   [".font-caption" {:font-size "12px"}]
   [".font-menu" {:font-size "13px"}]
   [".font-button" "button" {:font-size "14px"
                             :text-transform "uppercase"}]])

(def flex
  [[".flex-row" {:display "flex"}]
   [".flex-column" {:display "flex" :flex-direction "column"}]
   [".flex" {:flex "1"}]])

(def custom
  [["button"
    {:background "#fff"
     :border "1px solid #000"
     :cursor "pointer"
     :padding "20px"
     :outline 0
     :width "100px"}]
   [".recipe"
    {:border "1px solid #000"
     :margin "10px 0"}]
   [".timeline"
    {:padding "5px"}
    [".fill"
     {:background "#3CDD83"
      :flex "1"
      :width "0"}]]])

(def page-css
  (let [styles (concat global-reset
                       google-typography
                       flex
                       custom)]
    (compile-css styles)))
