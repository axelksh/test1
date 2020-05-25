(ns test1.events
  (:require
    [ajax.core :as ajax]
    [re-frame.core :as re-frame]
    [test1.db :as db]))

(def tickers
  {:btcusdt "BTCUSDT"
   :ethbtc "ETHBTC"
   :ltcbtc "LTCBTC"})

(def binance-url "https://api.binance.com/api/v3")

(def track-ticker-interval 10000)

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ [_ ticker]]
   (re-frame/dispatch [:request-ticker ticker])
   db/default-db))


;; Creates specific handler for the appropriate ticker
(defn ticker-handler
  [ticker]
  (fn [resp]
    (re-frame/dispatch [:process-ticker resp ticker])))


;;; Response handler for appropriate ticker
(re-frame/reg-event-db
  :process-ticker
  (fn [db [_ resp ticker]]
    (let [new-price (-> (get-in db [:tickers ticker :price])
                        (subvec 1)
                        (conj (get-in resp ["price"])))]
      (update-in db [:tickers ticker]  merge {:price        new-price
                                              :last-update  (.now js/Date)}))))


;; General event for requesting tickers
(re-frame/reg-event-db
  :request-ticker
  (fn [db [_ ticker]]
    (ajax/GET
      (str binance-url "/ticker/price?symbol=" (get-in tickers [ticker]))
      {:handler (ticker-handler ticker)
       :error-handler #(re-frame/dispatch [:request-error %1])})
    db))


;; Error response handler
(re-frame/reg-event-db
  :request-error
  (fn [db [_ resp]]
    (println "Error while tracking ticker." resp)
    db))


;;; Starts tracking ticker requesting api every 10 seconds
;;; Should replace with some socket?...
(re-frame/reg-event-db
  ::track-ticker
  (fn [db [_ ticker]] (js/setInterval
                        #(re-frame/dispatch [:request-ticker ticker]) track-ticker-interval)
                          db))