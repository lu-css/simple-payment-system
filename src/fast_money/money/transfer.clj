(ns fast-money.money.transfer
  (:require [fast-money.db.users-db :as user]
            [clojure.data.json :as json]
            [fast-money.db.db :refer [db]]))

(defn- user-not-found
  "Return a map with not existent user error"
  [id]
  {:status  500
   :headers {"Content-Type" "application/json"}
   :body    (json/write-str {:error (str "User ID not found " id)})})

(defn- invalid-ammount
  "Return a map with invalid ammount error."
  [ammount]

  {:status  500
   :headers {"Content-Type" "application/json"}
   :body    (json/write-str {:error (str "Ammount invalid " ammount)})})

(defn valid-ammount?
  "Return if the value if a valid number"
  [ammount]

  (and
   (number? ammount)
   (> ammount 0)))

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

(defn- transfer
  "Transfers the money, using all validatons"
  [remetente destinatario ammount]

  (let [remetente-id (:id remetente)
        destinatario-id (:id destinatario)]

    (try
      (user/debit-money db {:id remetente-id :money ammount})

      (try
        (user/credit-money db {:id destinatario-id :money ammount})
        transation-success

        (catch Exception _e
          (do
            (user/update-ammount db {:id remetente-id :money (:money remetente)})
            (user/update-ammount db {:id destinatario-id :money (:money destinatario)})
            error-while-updateing)))

      (catch Exception _e
        (user/update-ammount db {:id remetente-id :money (:money remetente)})
        error-while-updateing))))

(defn- valid-transaction?
  "Return true if all transaction params is true,
  and send a response in case of fail."
  [remetente destinatario ammount]

  (cond
    (= remetente nil) (user-not-found (:id remetente))
    (= destinatario nil) (user-not-found (:id destinatario))
    (< (:money remetente) ammount) not-enouth-money
    :else true))

(defn transfer-money
  "Transfer money from a user to another."
  [req]

  (let [params (:params req)
        body (:body req)
        remetente-id (Integer/parseInt (:id params))
        destinatario-id (Integer/parseInt (:destination params))
        ammount (:ammount body)]

    (if-not (valid-ammount? ammount)
      (invalid-ammount ammount)

      (let [remetente (user/user-by-id db {:id remetente-id})
            destinatario (user/user-by-id db {:id destinatario-id})
            valid? (valid-transaction? remetente destinatario ammount)]

        (if (= valid? true)
          (transfer remetente destinatario ammount)
          valid?)))))
