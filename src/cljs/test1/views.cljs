(ns test1.views
  (:require
    [test1.subs :as subs]
    [re-frame.core :as re-frame]
    ["react-vis" :as vis]))


(def axis-style {:line {:stroke "#333"
                        :strokeLinecap "square"}
                 :ticks {:stroke "#999"}
                 :text {:stroke "none"
                        :fill "#333"}})


(defn line-chart [data]
  [:> vis/XYPlot
   {:width 800
    :height 225
    :margin {:left 50 :right 50}}
   [:> vis/XAxis
    {:tickTotal 10
     :tickSizeInner 0
     :tickSizeOuter 3
     :style axis-style}]
   [:> vis/YAxis
    {:tickSizeInner 0
     :tickSizeOuter 3
     :style axis-style}]
   [:> vis/LineSeries
    {:data data
     :color "#30d5c8"
     :strokeWidth 5
     :style {:fill "none"
             :strokeLinejoin "round"
             :strokeLinecap "round"}}]])


(defn chart-data
  [[y1 y2 y3 y4 y5 y6 y7 y8 y9 y10]]
  [{:x 1 :y y1}
   {:x 2 :y y2}
   {:x 3 :y y3}
   {:x 4 :y y4}
   {:x 5 :y y5}
   {:x 6 :y y6}
   {:x 7 :y y7}
   {:x 8 :y y8}
   {:x 9 :y y9}
   {:x 10 :y y10}])


(defn chart
  []
  (let [price (-> @(re-frame/subscribe [::subs/tickers])
                   (get-in [:btcusdt :price]))]
    [:div
     {:style
      {:display "inline-block"}}
     [line-chart (chart-data price)]]))


(defn parse-date
  [timestamp]
  (-> (js/Date. timestamp)
      (.toDateString)))


(defn parse-time
  [timestamp]
  (let [date  (js/Date. timestamp)
        hours (.getHours date)
        min   (.getMinutes date)
        sec   (.getSeconds date)]
    (str hours ":" min ":" sec)))


(defn info
  []
  (let [ticker (-> @(re-frame/subscribe [::subs/tickers])
                    (get-in [:btcusdt]))
        name          (:name ticker)
        date          (parse-date (:last-update ticker))
        time          (parse-time (:last-update ticker))
        current-price (last (:price ticker))]
    [:div
     {:style
      {:display "inline-block"
       :vertical-align "top"}}
     [:p "Currency: "         name]
     [:p "Current price: "    current-price]
     [:p "Date: "             date]
     [:p "Last update time: " time]]))


(defn ui
  []
  [:div
   [:h1 "Currency exchange"]
   [chart]
   [info]])