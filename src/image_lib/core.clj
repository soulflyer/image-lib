(ns image-lib.core
  (:require [monger
             [collection :as mc]
             [core :as mg]
             [operators :refer :all]]))

(defn find-images
  "Searches database collection for entries where the given field matches the given value"
  [database image-collection field value]
  (let [connection (mg/connect)
        db (mg/get-db connection database)]
    (mc/find-maps db image-collection {field value})))


(defn find-images-containing
  "Searches database collection for entries where the given field contains the given value"
  [database image-collection field value]
  (let [connection (mg/connect)
        db (mg/get-db connection database)]
    (mc/find-maps db image-collection {field {$regex value}})))


(defn find-sub-keywords
  "given a keyword entry returns a list of all the sub keywords"
  [database keyword-collection given-keyword]
  (let [connection (mg/connect)
        db (mg/get-db connection database)
        keyword-entry (first (mc/find-maps db keyword-collection {:_id given-keyword}))]
    (if (empty? keyword-entry)
      (println (str "Keyword not found: " given-keyword ))
      (if (= 0 (count (:sub keyword-entry)))
        (conj '() given-keyword)
        (flatten (conj
                  (map #(find-sub-keywords database keyword-collection %) (:sub keyword-entry))
                  given-keyword))))))


(defn image-path
  "return a string containing the year/month/project/version path of an image"
  [image-map]
  (str (:Year image-map) "/"
       (:Month image-map) "/"
       (:Project image-map) "/"
       (:Version image-map) ".jpg"))


;; (defn find-best-image
;;   "return an image with the highest rating for the given keyword"
;;   [database keyword-collection given-keyword]
;;   )
