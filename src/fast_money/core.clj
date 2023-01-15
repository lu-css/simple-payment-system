(ns fast-money.core
  (:require [compojure.core :refer [GET POST defroutes context]]
            [org.httpkit.server :as server]
            [fast-money.money.transfer :as trans]
            [fast-money.money.account :as acct]
            [ring.middleware.json :as mj]
            [ring.middleware.defaults :refer [api-defaults, wrap-defaults]])
  (:gen-class))

(def port 8080)

(defroutes app-routes
  (context "/:id" [id]
    (GET "/saldo" [] (acct/get-saldo id))
    (POST "/to/:destination" [] (mj/wrap-json-body trans/transfer-money {:keywords? true}))))

(defn -main
  [& _args]
  (server/run-server  (wrap-defaults #'app-routes api-defaults)  {:port port})
  (println (str "Server started at port " port)))
