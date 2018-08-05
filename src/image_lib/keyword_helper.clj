(ns image-lib.keyword-helper
  "All these fns are in image-lib.keywords Should probably remove this ns"
  (:require [monger.collection :as mc]
            [monger.operators :refer :all]))

(defn find-keyword
  [database kw-collection keyword-name]
  (first (mc/find-maps database kw-collection {:_id keyword-name})))

(defn find-sub-keywords
  "given a keyword entry returns a list of all the sub keywords"
  [database keyword-collection given-keyword]
  (let [keyword-entry (first (mc/find-maps database keyword-collection {:_id given-keyword}))]
    (if (empty? keyword-entry)
      (println (str "Keyword not found: " given-keyword))
      (if (= 0 (count (:sub keyword-entry)))
        (conj '() given-keyword)
        (flatten
          (conj
            (map #(find-sub-keywords database keyword-collection %) (:sub keyword-entry))
            given-keyword))))))

(defn add-keyword
  "Add a new keyword"
  [db keyword-collection new-keyword parent]
  (if-not (mc/find-by-id db keyword-collection new-keyword)
    (mc/save db keyword-collection
             (hash-map :_id new-keyword
                       :sub [])))
  (mc/update db keyword-collection {:_id parent} {$addToSet {:sub new-keyword}}))
