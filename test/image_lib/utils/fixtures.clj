(ns image-lib.utils.fixtures
  (:require [image-lib.utils.database :as database]
            [image-lib.utils.database.seed :as seed]
            [image-lib.utils.file :as file]))


(defn database
  [f]
  (database/connect)
  (f))


(defn seed-preferences
  [f]
  (database/empty-tables! (database/connection) [database/preferences])
  (seed/preferences)
  (f))


(defn seed-keywords
  [f]
  (database/empty-tables! (database/connection) [database/keywords])
  (seed/keywords)
  (f))


(defn create-file
  [f]
  (file/create "/tmp/image-lib-file-test.txt")
  (f))
