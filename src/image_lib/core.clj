(ns image-lib.core
  (:require [monger
             [collection :as mc]
             [core :as mg]
             [operators :refer :all]]
            [seesaw.core :refer :all]
            [seesaw.tree :refer :all] )
  (:gen-class))

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

(defn image-paths
  [db image-collection]
  (map image-path (mc/find-maps db image-collection {})))

(defn basename
  "Cuts the extension off the end of a string"
  [filename]
  (let [index-dot (.lastIndexOf filename ".")
        index-slash (+ 1 (.lastIndexOf filename "/"))]
    (if (< 0 index-dot)
      (subs filename index-slash index-dot)
      filename)))

(defn project-name
  [filename]
  (let [index-slash (.lastIndexOf filename "/")]
    (if (< 0 index-slash)
      (subs filename 0 index-slash)
      filename)))

(defn file-exists?
  [path]
  (let [file (java.io.File. path)]
    (.exists file)))

(defn related-file-exists?
  [path]
  (let [file (java.io.File. path)
        dir  (.getParentFile file)
        files (.list dir)]
    (if (some #{(basename path)} (seq (map basename files))) true false)))

(defn loosely-related-file-exists?
  "given a pathname to a file, checks if any variant of the file exists
  (loosely-related-file exists? /home/me/picture/abc.jpg
  will return true if any file exists in /home/me/pictures  that starts with abc
  ie: abc.jpg abc.png, abc-version2.jpg etc."
  [path]
  (let [file (java.io.File. path)
        dir  (.getParentFile file)
        files (.list dir)]
    (< 0 (count (filter #(re-find (re-pattern %) path) (map basename files))))))

(defn missing-files
  [database image-collection root-path find-function]
  (remove
   (fn [im] (find-function (str root-path "/" im)))
   (image-paths database image-collection)))

(defn find-projects
  "returns a list of all the projects in yyyy/mm/project-name form"
  [database image-collection]
  (let [connection (mg/connect)
        db (mg/get-db connection database)]
    (sort (set (map project-name (image-paths db image-collection))))))

(defn get-best-image
  [database images-collection given-keyword]
  (thumbnail-file
   (image-path
    (last
     (sort-by :Rating (find-images database images-collection :Keywords given-keyword))))))
