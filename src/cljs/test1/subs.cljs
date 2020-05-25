(ns test1.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
  ::tickers
  (fn [db _]
    (:tickers db)))
