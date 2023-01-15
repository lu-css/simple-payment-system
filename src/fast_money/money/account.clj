(ns fast-money.money.account
  (:require [fast-money.db.users-db :as user]
            [clojure.data.json :as json]
            [fast-money.db.db :refer [db]]))

(defn get-saldo
  "Return a user saldo."
  [user-id]

  (let [id  {:id (Integer/parseInt user-id)}
        saldo (user/user-by-id db id)]
    {:status  200
     :headers {"Content-Type" "application/json"}
     :body    (json/write-str {:your-money (:money saldo)})}))
