(ns fast-money.money.transfer
  (:require [fast-money.db.users-db :as user]
            [fast-money.db.db :refer [db]]
            [fast-money.money.responses :as responses]))

(def number-only-regex #"^[0-9]+$")

(defn valid-ammount?
  "Return if the value if a valid number"
  [ammount]

  (and
   (number? ammount)
   (> ammount 0)))

(defn- valid-transaction?
  "Return true if all transaction params is true,
  and send a response in case of fail."
  [remetente destinatario ammount]

  (cond
    (= remetente destinatario) responses/cant-send-to-yourself
    (nil? remetente) (responses/user-not-found "'from'")
    (nil? destinatario) (responses/user-not-found "'to'")
    (< (:money remetente) ammount) responses/not-enouth-money
    :else true))

(defn valid-param?
  "Return true if is a valid param format.
  Otherwies return the corresponding error message."
  [param]

  (if-not (or
           (nil? (re-matches number-only-regex (:id param)))
           (nil? (re-matches number-only-regex (:destination param))))
    true
    responses/invalid-params))

(defn- transfer
  "Transfers the money, using all validatons"
  [remetente destinatario ammount]

  (let [remetente-id (:id remetente)
        destinatario-id (:id destinatario)]

    (try
      (user/debit-money db {:id remetente-id :money ammount})

      (try
        (user/credit-money db {:id destinatario-id :money ammount})
        responses/transation-success

        (catch Exception _e
          (do
            (user/update-ammount db {:id remetente-id :money (:money remetente)})
            (user/update-ammount db {:id destinatario-id :money (:money destinatario)})
            responses/error-while-updateing)))

      (catch Exception _e
        (user/update-ammount db {:id remetente-id :money (:money remetente)})
        responses/error-while-updateing))))

(defn make-transaction
  [remetente-id destinatario-id ammount]

  (let [remetente (user/user-by-id db {:id remetente-id})
        destinatario (user/user-by-id db {:id destinatario-id})
        valid? (valid-transaction? remetente destinatario ammount)]

    (if (true? valid?)
      (transfer remetente destinatario ammount)
      valid?)))

(defn transfer-money
  "Transfer money from a user to another."
  [req]

  (let [params (:params req)
        body (:body req)
        ammount (:ammount body)]

    (cond
      (false? (valid-ammount? ammount)) responses/invalid-ammount
      (not= (valid-param? params) true) (valid-param? params)
      :else (make-transaction (Integer/parseInt (:id params)) (Integer/parseInt (:destination params)) ammount))))
