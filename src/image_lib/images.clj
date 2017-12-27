(ns image-lib.images
  (:require [monger.collection :as mc]
            [monger.operators  :refer :all]
            [image-lib.helper :refer [image-path]]))

(defn find-images
  "Searches database collection for entries where the given field matches the given value"
  [database image-collection field value]
  (mc/find-maps database image-collection {field value}))

(defn find-image
  "returns an image given its id"
  ([db collection id]
   (mc/find-one-as-map db collection {:_id id}))
  ([db collection year month project pic]
   (find-image db collection (str year month project pic))))

(defn find-images-containing
  "Searches database collection for entries where the given field contains the given value"
  [database image-collection field value]
  (mc/find-maps database image-collection {field {$regex value}}))

(defn image-paths
  "Returns the path of every image in the database"
  [db image-collection]
  (map image-path (mc/find-maps db image-collection {})))