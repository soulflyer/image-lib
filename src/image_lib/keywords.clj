(ns image-lib.keywords
  (:require [monger.collection :as mc]
            [monger.operators :refer :all]))


(defn find-keyword
  [database kw-collection keyword-name]
  (first (mc/find-maps database kw-collection {:_id keyword-name})))


(defn root?
  "Checks that keyword collection contains a Root entry. This is usually hidden, but must exist."
  [database kw-collection]
  (not (nil? (find-keyword database "keywords" "Root"))))


(defn find-sub-keywords
  "given a keyword entry returns a list of all the sub keywords"
  [database keyword-collection given-keyword]
  (let [keyword-entry (first (mc/find-maps database keyword-collection {:_id given-keyword}))]
    (if (empty? keyword-entry)
      (println (str "Keyword not found:- " given-keyword))
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


(defn move-keyword
  "Move a keyword from one parent to another"
  [db keyword-collection kw old-parent new-parent]
  (mc/update db keyword-collection {:_id new-parent} {$addToSet {:sub kw}})
  (mc/update db keyword-collection {:_id old-parent} {$pull {:sub kw}}))


(defn add-sample
  "Adds a sample image to a keyword"
  [db keyword-collection kw sample]
  (mc/update db keyword-collection {:_id kw} {$set {:sample sample}}))


(defn find-parents
  "given a keyword, returns a list of the parents"
  [db keyword-collection kw]
  (mc/find-maps db keyword-collection {:sub kw}))


(defn parent
  "given a keyword returns the _id of the parent"
  [db keyword-collection kw]
  (:_id (first (find-parents db keyword-collection kw))))


(defn keyword-path
  "Given a keyword, returns it's full path as a vector"
  [db keyword-collection kw]
  (if (root? db keyword-collection)
    (vec
      (reverse
        (loop [kw kw
               acc []]
          (if (= kw "Root")
            acc
            (recur (parent db keyword-collection kw)
              (conj acc kw))))))
    (throw (Exception. "Keyword collection has no Root entry"))))

(defn disconnect-keyword
  "Removes keyword from parent keyword but doesn't delete it"
  [db keyword-collection keyword parent]
  (mc/update db keyword-collection {:_id parent} {$pull {:sub keyword}}))


(defn delete-keyword
  "Remove a keyword"
  ([db keyword-collection kw parent]
   (mc/remove-by-id db keyword-collection kw)
   (mc/update db keyword-collection {:_id parent} {$pull {:sub kw}}))
  ([db keyword-collection kw]
   (let [parents (map :_id (find-parents db keyword-collection kw))]
     (doall (map #(delete-keyword db keyword-collection kw %) parents))
     (str kw))))


(defn safe-delete-keyword
  "Delete a keyword, but only if it has no sub keywords"
  ([db keyword-collection kw]
   (let [keyword (mc/find-map-by-id db keyword-collection kw)]
     (if (= 0 (count (:sub keyword)))
       (delete-keyword db keyword-collection kw))))
  ([db keyword-collection kw parent]
   (let [keyword (mc/find-map-by-id db keyword-collection kw)]
     (if (= 0 (count (:sub keyword)))
       (delete-keyword db keyword-collection kw parent)))))


(defn all-keywords
  "returns all the keyword ids"
  [db keyword-collection]
  (map :_id (mc/find-maps db keyword-collection)))


(defn all-sub-keywords
  [database kw-collection ]
  (reduce
    (fn [a b] (concat a (:sub (find-keyword database kw-collection b))))
    #{}
    (all-keywords database kw-collection)))
