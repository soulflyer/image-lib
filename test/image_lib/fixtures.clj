(ns image-lib.fixtures
  (:require [image-lib.utils.database :as database]
            [image-lib.utils.database.seed :as seed]))


(defn database
  [f]
  (database/connect)
  (database/empty-tables!)
  (seed/preferences)
  (f)
  (database/empty-tables!))
