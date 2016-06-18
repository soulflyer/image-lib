(ns image-lib.core
  (:require [monger
             [collection :as mc]
             [core :as mg]
             [operators :refer :all]])
  (:gen-class))

(defn find-sub-keywords
  "given a keyword entry returns a list of all the sub keywords"
  [database keyword-collection given-keyword]
  (let [keyword-entry (first (mc/find-maps database keyword-collection {:_id given-keyword}))]
    (if (empty? keyword-entry)
      (println (str "Keyword not found: " given-keyword))
      (if (= 0 (count (:sub keyword-entry)))
        (conj '() given-keyword)
        (flatten (conj
                  (map #(find-sub-keywords database keyword-collection %) (:sub keyword-entry))
                  given-keyword))))))

(defn add-keyword
  "Add a new keyword"
  [db keyword-collection new-keyword parent]
  (mc/save db keyword-collection
           (hash-map :_id new-keyword
                     :sub []))
  (mc/update db keyword-collection {:_id parent} {$addToSet {:sub new-keyword}}))

(defn move-keyword
  "Move a keyword from one parent to another"
  [db keyword-collection kw old-parent new-parent]
  (mc/update db keyword-collection {:_id new-parent} {$addToSet {:sub kw}})
  (mc/update db keyword-collection {:_id old-parent} {$pull {:sub kw}}))

(defn delete-keyword
  "Remove a keyword"
  [db keyword-collection kw parent]
  (mc/remove-by-id db keyword-collection kw)
  (mc/update db keyword-collection {:_id parent} {$pull {:sub kw}}))

(defn find-parents
  "given a keyword, returns a list of the parents"
  [db keyword-collection kw]
  (mc/find-maps db keyword-collection {:sub kw}))

(defn find-images
  "Searches database collection for entries where the given field matches the given value"
  [database image-collection field value]
  (mc/find-maps database image-collection {field value}))


(defn find-images-containing
  "Searches database collection for entries where the given field contains the given value"
  [database image-collection field value]
  (mc/find-maps database image-collection {field {$regex value}}))

(defn find-all-images
  "Given a keyword searches the database for images containing it or any of its sub keywords"
  [db image-collection keyword-collection given-keyword]
  (let [keywords (find-sub-keywords db keyword-collection given-keyword)]
    (flatten (map #(find-images db image-collection "Keywords" %) keywords))))

(defn image-path
  "return a string containing the year/month/project/version path of an image"
  [image-map]
  (str (:Year image-map) "/"
       (:Month image-map) "/"
       (:Project image-map) "/"
       (:Version image-map) ".jpg"))

(defn image-paths
  [db image-collection]
  (map image-path (mc/find-maps db image-collection {})))

(defn version-name
  "Cuts the extension off the end of a string"
  [filename]
  (let [index-dot (if (= -1 (.lastIndexOf filename "."))
                    (count filename)
                    (.lastIndexOf filename "."))
        index-slash (max 0 (+ 1 (.lastIndexOf filename "/")))]
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
    (if (some #{(version-name path)} (seq (map version-name files))) true false)))

(defn loosely-related-file-exists?
  "given a pathname to a file, checks if any variant of the file exists
  (loosely-related-file exists? /home/me/picture/abc_version_2.jpg
  will return true if any file exists in /home/me/pictures that matches the
  start of the last section of the pathname
  ie: abc.jpg abc.NEF, abc-version_2.NEF etc."
  [path]
  (let [file (java.io.File. path)
        vname (version-name path)
        dir  (.getParentFile file)
        files (.list dir)]
    (< 0 (count (filter #(re-find (re-pattern %) vname) (map version-name files))))))

(defn missing-files
  [db image-collection root-path find-function]
  (remove
   (fn [im] (find-function (str root-path "/" im)))
   (image-paths db image-collection)))

(defn all-projects
  "returns a list of all the projects in yyyy/mm/project-name form"
  [db image-collection]
  (sort (set (map project-name (image-paths db image-collection)))))

(defn all-keywords
  "returns a set of all keywords found in the given database of images"
  [db image-collection]
  (reduce #(set (concat %1 %2))
          (map :Keywords (mc/find-maps db image-collection {} [:Keywords]))))

(defn best
  [images]
  (last
   (sort-by :Rating images)))

(defn best-image
  "Return the first of the highest rated images.
  If the keyword-collection  parameter is present, searches for the sub keywords too."
  ([db image-collection keyword-collection given-keyword]
   (best (find-all-images db image-collection keyword-collection given-keyword)))
  ([db image-collection given-keyword]
   (best (find-images db image-collection :Keywords given-keyword))))

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
