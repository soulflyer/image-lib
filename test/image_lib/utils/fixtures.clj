(ns image-lib.utils.fixtures
  (:require [image-lib.utils.database :as database]
            [image-lib.utils.database.seed :as seed]))


(defn database
  [f]
  (database/connect)
  (f))


(defn seed-preferences
  [f]
  (seed/preferences)
  (f)
  (database/empty-tables! (database/connection) [database/preferences]))


(defn seed-keywords
  [f]
  (seed/keywords)
  (f)
  (database/empty-tables! (database/connection) [database/keywords]))
