
-- :name update-ammount :! :n
-- :doc Update some user money.
update users
SET money = :money
WHERE id = :id

-- :name user-by-id :? :1
-- :doc Return a single User using his id.
select * from users
where id = :id
