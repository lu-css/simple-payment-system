-- The Stored URL

-- :name create-urls-table
-- :command :execute
-- :result :raw
-- :doc Create url table
create table users (
  id              SERIAL primary key,
  name            varchar not null,
  money           decimal(5, 2) not null,
  created_at      timestamp not null default current_timestamp
)
