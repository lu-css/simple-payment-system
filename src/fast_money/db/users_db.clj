(ns fast-money.db.users-db
  (:require [hugsql.core :as hugsql]))

(hugsql/def-db-fns
  "fast_money/db/users/users.sql")

(hugsql/def-sqlvec-fns
  "fast_money/db/users/users.sql")

