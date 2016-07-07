(ns image-lib.core
  (:require [monger
             [collection :as mc]
             [core :as mg]
             [operators :refer :all]]
            [clojure.set :refer [difference]]
            [clojure.string :refer [replace]]
            [clojure.set :refer [difference]]
            [monger
             [collection :as mc]
             [core :as mg]
             [operators :refer :all]]
            [seesaw
             [core :refer :all]
             [tree :refer :all]
             [keymap :refer :all]])
  (:gen-class))

(def database                   "photos")
(def keyword-collection       "keywords")
(def preference-collection "preferences")
(def image-collection           "images")
(def connection (mg/connect))
(def db (mg/get-db connection database))

(defn get-keyword
  [keyword-name]
  (first (mc/find-maps db keyword-collection {:_id keyword-name})))


(defn find-sub-keywords
  "given a keyword entry returns a list of all the sub keywords"
  ([database keyword-collection given-keyword]
   (let [keyword-entry (first (mc/find-maps database keyword-collection {:_id given-keyword}))]
     (if (empty? keyword-entry)
       (println (str "Keyword not found: " given-keyword))
       (if (= 0 (count (:sub keyword-entry)))
         (conj '() given-keyword)
         (flatten
          (conj
           (map #(find-sub-keywords database keyword-collection %) (:sub keyword-entry))
           given-keyword))))))
  ([given-keyword]
   (find-sub-keywords db keyword-collection given-keyword)))

(defn add-keyword
  "Add a new keyword"
  ([db keyword-collection new-keyword parent]
   (if-not (mc/find-by-id db keyword-collection new-keyword)
     (mc/save db keyword-collection
              (hash-map :_id new-keyword
                        :sub [])))
   (mc/update db keyword-collection {:_id parent} {$addToSet {:sub new-keyword}}))
  ([new-keyword parent]
   (add-keyword db keyword-collection new-keyword parent)))

(defn move-keyword
  "Move a keyword from one parent to another"
  ([db keyword-collection kw old-parent new-parent]
   (mc/update db keyword-collection {:_id new-parent} {$addToSet {:sub kw}})
   (mc/update db keyword-collection {:_id old-parent} {$pull {:sub kw}}))
  ([kw old-parent new-parent]
   (move-keyword db keyword-collection kw old-parent new-parent)))

(defn find-parents
  "given a keyword, returns a list of the parents"
  ([db keyword-collection kw]
   (mc/find-maps db keyword-collection {:sub kw}))
  ([kw]
   (find-parents db keyword-collection kw)))

(defn first-parent
  "get the parent keyword map"
  [keyword]
  (first (find-parents db keyword-collection keyword)))

(defn find-images
  "Searches database collection for entries where the given field matches the given value"
  ([database image-collection field value]
   (mc/find-maps database image-collection {field value}))
  ([field value]
   (find-images db image-collection field value)))

(defn disconnect-keyword
  "Removes keyword from parent keyword but doesn't delete it"
  ([db keyword-collection keyword parent]
   (mc/update db keyword-collection {:_id parent} {$pull {:sub keyword}}))
  ([keyword parent]
   (disconnect-keyword db keyword-collection keyword parent)))

(defn delete-keyword
  "Remove a keyword"
  ([db keyword-collection kw parent]
   (mc/remove-by-id db keyword-collection kw)
   (mc/update db keyword-collection {:_id parent} {$pull {:sub kw}}))
  ([db keyword-collection kw]
   (let [parents (map :_id (find-parents db keyword-collection kw))]
     (doall (map #(delete-keyword db keyword-collection kw %) parents))))
  ([kw]
   (delete-keyword db keyword-collection kw))
  ([kw parent]
   ((delete-keyword db keyword-collection kw parent))))

(defn safe-delete-keyword
  "Delete a keyword, but only if it has no sub keywords"
  ([db keyword-collection kw]
   (let [keyword (mc/find-map-by-id db keyword-collection kw)]
     (if (= 0 (count (:sub keyword)))
       (delete-keyword db keyword-collection kw))))
  ([db keyword-collection kw parent]
   (let [keyword (mc/find-map-by-id db keyword-collection kw)]
     (if (= 0 (count (:sub keyword)))
       (delete-keyword db keyword-collection kw parent))))
  ([kw]
   (safe-delete-keyword db keyword-collection kw))
  ([kw parent]
   ((safe-delete-keyword db keyword-collection kw parent))))

(defn remove-keyword-from-photos
  "removes a given keyword from the keywords field of all images"
  ([db image-collection keyword]
   (let [photos (find-images db image-collection :Keywords keyword)]
     (doall (map #(mc/update db image-collection % {$pull {:Keywords keyword}}) photos))))
  ([keyword]
   (remove-keyword-from-photos db image-collection keyword)))

(defn replace-keyword-in-photos
  "replace keyword in the :Keywords field of all images"
  ([db image-collection old-keyword new-keyword]
   (let [photos (find-images db image-collection :Keywords old-keyword)]
     (doall
      (map #(mc/update db image-collection % {$addToSet {:Keywords new-keyword}}) photos))
     (remove-keyword-from-photos db image-collection old-keyword)))
  ([old-keyword new-keyword]
   (replace-keyword-in-photos db image-collection old-keyword new-keyword)))

(defn rename-keyword
  "Changes the keyword including any references in parents. If given the image-collection it will also change the keyword in the :Keyword field of every matching entry in the image-collection. Doesn't change the original images. Note that the short form also changes the image collectoion."
  ([db keyword-collection old-keyword new-keyword]
   (let [parents (find-parents db keyword-collection old-keyword)
         parent  (:_id (first parents))
         children (:sub (mc/find-map-by-id db keyword-collection old-keyword))]
     (add-keyword db keyword-collection new-keyword parent)
     (doall (map #(move-keyword db keyword-collection % old-keyword new-keyword) children))
     (delete-keyword db keyword-collection old-keyword)))
  ([db keyword-collection image-collection old-keyword new-keyword]
   (rename-keyword db keyword-collection old-keyword new-keyword)
   (replace-keyword-in-photos db image-collection old-keyword new-keyword))
  ([old-keyword new-keyword]
   (rename-keyword db keyword-collection image-collection old-keyword new-keyword)))

(defn merge-keyword
  ([db keyword-collection image-collection dispose-keyword keep-keyword]
   (let [dispose-keyword-parents (find-parents db keyword-collection dispose-keyword)
         dispose-keyword-parent  (first dispose-keyword-parents)]
     (replace-keyword-in-photos db image-collection dispose-keyword keep-keyword)
     (delete-keyword db keyword-collection dispose-keyword)))
  ([dispose-keyword keep-keyword]
   (merge-keyword db keyword-collection image-collection dispose-keyword keep-keyword)))

(defn find-images-containing
  "Searches database collection for entries where the given field contains the given value"
  ([database image-collection field value]
   (mc/find-maps database image-collection {field {$regex value}}))
  ([field value]
   (find-images-containing db image-collection field value)))

(defn find-all-images
  "Given a keyword searches the database for images containing it or any of its sub keywords"
  ([db image-collection keyword-collection given-keyword]
   (let [keywords (find-sub-keywords db keyword-collection given-keyword)]
     (flatten (map #(find-images db image-collection "Keywords" %) keywords))))
  ([given-keyword]
   (find-all-images db image-collection keyword-collection given-keyword)))

(defn image-path
  "return a string containing the year/month/project/version path of an image"
  [image-map]
  (str (:Year image-map) "/"
       (:Month image-map) "/"
       (:Project image-map) "/"
       (:Version image-map) ".jpg"))

(defn image-paths
  "Returns the path of every image in the database"
  ([db image-collection]
   (map image-path (mc/find-maps db image-collection {})))
  ([]
   (image-paths db image-collection)))

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
  "cuts the last part of the pathname off to leave yyyy/mm/project-name"
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
  "Searches the directory given by root-path and returns a list of any images not found there but present in the image db. find-function is a function that when given a file path returns true or false. Try image-lib.core/file-exists? "
  ([db image-collection root-path find-function]
   (remove
    (fn [im] (find-function (str root-path "/" im)))
    (image-paths db image-collection)))
  ([root-path find-function]
   (missing-files db image-collection root-path find-function)))

(defn all-projects
  "returns a list of all the projects in yyyy/mm/project-name form"
  ([db image-collection]
   (sort (set (map project-name (image-paths db image-collection)))))
  ([]
   (all-projects db image-collection)))

(defn used-keywords
  "returns a set of all keywords found in the given database of images"
  ([db image-collection]
   (reduce #(set (concat %1 %2))
           (map :Keywords (mc/find-maps db image-collection {} [:Keywords]))))
  ([]
   (used-keywords db image-collection)))

(defn all-ids
  "returns all the keyword ids"
  ([db keyword-collection]
   (map :_id (mc/find-maps db keyword-collection)))
  ([]
   (all-ids db keyword-collection)))

(defn all-keywords
  []
  (all-ids db keyword-collection))

(defn unused-keywords
  "returns a set of all keywords found in the keyword-collection but not present in any images"
  ([db image-collection keyword-collection]
   (difference
    (set (all-ids db keyword-collection))
    (used-keywords db image-collection)))
  ([]
   (unused-keywords db image-collection keyword-collection)))

(defn missing-keywords
  "Returns a set of all keywords found in images but not in the keyword collection"
  ([db image-collection keyword-collection]
   (difference
    (used-keywords db image-collection)
    (set (all-ids db keyword-collection))))
  ([]
   (missing-keywords db image-collection keyword-collection)))

(defn add-missing-keywords
  "Add any keywords present in the images but not in the keyword collection"
  ([db image-collection keyword-collection root-keyword]
   (let [_ (add-keyword db keyword-collection root-keyword "Root")
         missing (missing-keywords db image-collection keyword-collection)]
     (map #(add-keyword db keyword-collection % root-keyword) missing)))
  ([db image-collection keyword-collection]
   (add-missing-keywords db image-collection keyword-collection "orphaned keywords"))
  ([]
   (add-missing-keywords db image-collection keyword-collection)))

(defn best
  [images]
  (last
   (sort-by :Rating images)))

(defn best-image
  "Return the first of the highest rated images."
  ([db image-collection given-keyword]
   (best (find-images db image-collection :Keywords given-keyword)))
  ([given-keyword]
   (best-image db image-collection given-keyword)))

(defn best-sub-image
  "Return the first of the highest rated images, searches sub keywords too"
  ([db image-collection keyword-collection keyword]
   (best (find-all-images db image-collection keyword-collection keyword)))
  ([keyword]
   (best-sub-image db image-collection keyword-collection keyword)))

(defn preference
  "return the value of the preference from the db"
  ([db preferences-collection pref]
   (:path (first (mc/find-maps db preferences-collection {:_id pref}))))
  ([pref]
   (preference db preference-collection pref)))

(defn preferences
  "return all the preferences"
  ([db preferences-collection]
   (mc/find-maps db preferences-collection))
  ([]
   (preferences db preference-collection)))

(defn preference!
  "set the value of preference in the db"
  ([db preferences-collection pref value]
   (mc/update db preferences-collection {:_id pref} {$set {:path value}} {:upsert true}))
  ([pref value]
   (preference! db preference-collection pref value)))
