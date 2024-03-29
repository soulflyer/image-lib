(ns image-lib.core
  (:gen-class)
  (:require [clojure.set        :refer [difference]]
            [image-lib.helper   :refer [best]]
            [image-lib.images   :refer [find-image
                                        find-images
                                        find-all-images]]
            [image-lib.keywords :as kw]
            [monger.collection  :as mc]
            [monger.operators   :refer :all]))


(defn add-keyword-to-photo
  "Adds <keyword> to the keywords field of <photo> in the images table."
  ([db image-collection keyword photoid]
   (let [img (find-image db image-collection photoid)]
     (mc/update db image-collection img {$addToSet {:Keywords keyword}})))
  ([db image-collection keyword year month project photo]
   (add-keyword-to-photo db image-collection keyword (str year month project photo))))


(defn remove-keyword-from-photo
  "Deletes <keyword> from the keyword field of <photo> in the image table."
  ([db image-collection keyword photoid]
   (let [img (find-image db image-collection photoid)]
     (mc/update db image-collection img {$pull {:Keywords keyword}})))
  ([db image-collection keyword year month project photo]
   (remove-keyword-from-photo db image-collection keyword (str year month project photo))))


(defn remove-keyword-from-photos
  "removes a given keyword from the keywords field of all images"
  [db image-collection keyword]
  (let [photos (find-images db image-collection :Keywords keyword)]
    (doall (map #(mc/update db image-collection % {$pull {:Keywords keyword}}) photos))))


(defn replace-keyword-in-photos
  "replace keyword in the :Keywords field of all images"
  [db image-collection old-keyword new-keyword]
  (let [photos (find-images db image-collection :Keywords old-keyword)]
    (doall
      (map #(mc/update db image-collection % {$addToSet {:Keywords new-keyword}}) photos))
    (remove-keyword-from-photos db image-collection old-keyword)))


(defn rename-keyword
  "Changes the keyword including any references in parents. If given the image-collection it will also change the keyword in the :Keyword field of every matching entry in the image-collection. Doesn't change the original images."
  ([db keyword-collection old-keyword new-keyword]
   (let [parents  (kw/find-parents db keyword-collection old-keyword)
         parent   (:_id (first parents))
         children (:sub (mc/find-map-by-id db keyword-collection old-keyword))]
     (kw/add-keyword db keyword-collection new-keyword parent)
     (doall (map #(kw/move-keyword db keyword-collection % old-keyword new-keyword) children))
     (kw/delete-keyword db keyword-collection old-keyword)))
  ([db keyword-collection image-collection old-keyword new-keyword]
   (rename-keyword db keyword-collection old-keyword new-keyword)
   (replace-keyword-in-photos db image-collection old-keyword new-keyword)))


(defn merge-keyword
  ([db keyword-collection image-collection dispose-keyword keep-keyword]
   (let [dispose-keyword-parents (kw/find-parents db keyword-collection dispose-keyword)
         dispose-keyword-parent  (first dispose-keyword-parents)]
     (replace-keyword-in-photos db image-collection dispose-keyword keep-keyword)
     (kw/delete-keyword db keyword-collection dispose-keyword))))


(defn used-keywords
  "returns a set of all keywords found in the given database of images"
  [db image-collection]
  ;; TODO find out if reduce realizes the whole seq or not
  (reduce #(set (concat %1 %2))
    (map :Keywords (mc/find-maps db image-collection {} [:Keywords]))))

;; TODO define a fn that returns all the used keywords including parents so
;; unused-keywords can return only the genuinely unused kws, not all the
;; intermediate ones that don't have any direct uses. OR:
;; TODO change current version of unused-keywords to do this.


(defn unused-keywords
  "returns a set of all keywords found in the keyword-collection but not present in any images"
  [db image-collection keyword-collection]
  (difference
    (set (kw/all-keywords db keyword-collection))
    (used-keywords db image-collection)))


(defn missing-keywords
  "Returns a set of all keywords found in images but not in the keyword collection"
  [db image-collection keyword-collection]
  (difference
    (used-keywords db image-collection)
    (set (kw/all-keywords db keyword-collection))))


(defn add-missing-keywords
  "Add any keywords present in the images but not in the keyword collection"
  ([db image-collection keyword-collection root-keyword]
   (let [_ (kw/add-keyword db keyword-collection root-keyword "Root")
         missing (missing-keywords db image-collection keyword-collection)]
     (println "hello from add-missing-keywords")
     (dorun (map #(kw/add-keyword db keyword-collection % root-keyword) missing))))
  ([db image-collection keyword-collection]
   (add-missing-keywords db image-collection keyword-collection "orphaned keywords")))


;;TODO make best-image do what best-sub-image does and get rid of best-sub-image.
(defn best-image
  "Return the first of the highest rated images."
  [db image-collection given-keyword]
  (best (find-images db image-collection :Keywords given-keyword)))


(defn best-sub-image
  "Return the first of the highest rated images, searches sub keywords too"
  [db image-collection keyword-collection keyword]
  (best (find-all-images db image-collection keyword-collection keyword)))


(defn orphaned-keywords
  [database kw-collection]
  (difference (set (kw/all-keywords database kw-collection))
    (set (kw/all-sub-keywords database kw-collection))))


(defn add-orphaned-keywords
  [db kc]
  (let [_       (kw/add-keyword db kc "orphaned keywords" "Root")
        orphans (difference (orphaned-keywords db kc) #{"Root"})]
    (dorun (map #(kw/add-keyword db kc % "orphaned keywords") orphans))))
