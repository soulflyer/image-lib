(ns image-lib.preferences
  (:require [monger.collection :as mc]
            [monger.operators :refer [$set]]))

(defn preference
  "return the value of the preference from the db"
  [db preferences-collection pref]
  (:path (first (mc/find-maps db preferences-collection {:_id pref}))))

(defn preferences
  "return all the preferences"
  [db preferences-collection]
  (mc/find-maps db preferences-collection))

(defn preference!
  "set the value of preference in the db"
  [db preferences-collection pref value]
  (mc/update db preferences-collection {:_id pref} {$set {:path value}} {:upsert true}))
