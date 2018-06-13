(ns image-lib.write
  (:require [image-lib.images  :refer [find-image]]
            [monger.collection :as mc]
            [monger.operators  :refer :all]))

(defn write-to-photo
  "Write an iptc field to a photo in the images-table"
  [db image-collection photoid field value]
  (let [img (find-image db image-collection photoid)]
    (mc/update db image-collection img {$set {field value}})))

(defn write-title
  "Writes a string to the :Object-Name field of a photo entry in the db"
  [db image-collection photoid value]
  (write-to-photo db image-collection photoid :Object-Name value))

(defn write-caption
  "Writes a string to the :Caption field of a photo entry in the db"
  [db image-collection photoid value]
  (write-to-photo db image-collection photoid :Caption-Abstract value))
