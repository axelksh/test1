(ns test1.core
  (:require
   [test1.events :as events]
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [test1.views :as views]))

(defn mount-ui
  []
  (rdom/render [views/ui] (.getElementById js/document "app")))

(defn init
  []
  (re-frame/dispatch-sync [::events/initialize-db :btcusdt])
  (re-frame/dispatch-sync [::events/track-ticker :btcusdt])
  (mount-ui))
