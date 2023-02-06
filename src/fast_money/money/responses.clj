(ns fast-money.money.responses
  (:require [clojure.data.json :as json]))

(defn user-not-found
  "Return a map with not existent user error"
  [id]
  {:status  500
   :headers {"Content-Type" "application/json"}
   :body    (json/write-str {:error (str "User ID not found " id)})})

(defn invalid-ammount
  "Return a map with invalid ammount error."
  [ammount]

  {:status  500
   :headers {"Content-Type" "application/json"}
   :body    (json/write-str {:error (str "Ammount invalid " ammount)})})

(def error-while-updateing
  {:status  500
   :headers {"Content-Type" "application/json"}
   :body    (json/write-str {:error "Error in transation. Relax, your money is safe."})})

(def not-enouth-money
  {:status  500
   :headers {"Content-Type" "application/json"}
   :body    (json/write-str {:error "You dont have money enouth to complete the transaction."})})

(def transation-success
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (json/write-str {:msg "The transtation was sucess"})})

(def cant-send-to-yourself
  {:status  401
   :headers {"Content-Type" "application/json"}
   :body    (json/write-str {:msg "You can't send money for yoursef."})})

