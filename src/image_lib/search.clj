(ns image-lib.search
  (:refer-clojure :exclude [or and])
  (:require
   [image-lib.helper :refer [image-path
                             clean-number-string
                             string-number-equals]]))

(defn eq [image-seq meta-key & meta-value]
  ;; If the last param is nil or missing, just return the image-seq
  (if (nil? (first meta-value))
    image-seq
    (filter #(string-number-equals (meta-key %) meta-value) image-seq)))

(defmulti contains
  "returns true if haystack contains needle. This is case insensitive and matches substrings if haystack is a string"
  (fn [haystack needle]
    (class haystack)))
(defmethod contains java.lang.String
  [haystack needle]
  (if (re-find (re-pattern (str "(?i)" needle)) haystack)
    true
    false))
(defmethod contains clojure.lang.Sequential
  [haystack needle]
  (contains? (set haystack) needle))
(defmethod contains nil
  [haystack needle]
  false)

(defn in
  "returns a sequence containing all entries of image-seq where meta-key contains meta-value"
  [image-seq meta-key & meta-value]
  (if (nil? (first meta-value))
    image-seq
    (filter #(contains (meta-key %) (first meta-value))
                 image-seq)))

(defn gt [image-seq meta-key meta-value]
  (filter #(let [db-value (meta-key %)]
             (if db-value
               (>  (bigdec (clean-number-string (meta-key %)))
                   (bigdec (clean-number-string meta-value)))
               false))
          image-seq))

(defn lt [image-seq meta-key meta-value]
  (filter #(let [db-value (meta-key %)]
             (if db-value
               (<  (bigdec (clean-number-string db-value))
                   (bigdec (clean-number-string meta-value)))
               false))
          image-seq))

(defn ge [image-seq meta-key meta-value]
  (filter #(let [db-value (meta-key %)]
             (if db-value
               (>= (bigdec (clean-number-string db-value))
                   (bigdec (clean-number-string meta-value)))
               false))
          image-seq))

(defn le [image-seq meta-key meta-value]
  (filter #(if (meta-key %)
             (<= (bigdec (clean-number-string (meta-key %)))
                 (bigdec (clean-number-string meta-value)))
             false)
          image-seq))

;; (defn open
;;   ([pics]
;;    (open pics medium))
;;   ([pics size]
;;    (sh "xargs" external-viewer
;;        :in (str/join " " (map #(str size "/" %)
;;                               (map image-path pics))))))

(defn paths
  "given a collection of pics, return just the paths"
  [pics]
  (map image-path pics))

(defn write
  "Append the collection 'things' to file 'file-name' one per line"
  [things file-name]
  (map #(spit file-name (str % "\n") :append true) things))

(defn overwrite
  "Write the collection to a file, clearing it first"
  [things file-name]
  (spit file-name "" :append false)
  (write things file-name))

(defmacro or [coll & forms]
  (if (seq forms)
    `(union (-> ~coll ~(first forms)) (or ~coll ~@(rest forms)))
    #{}))

(defmacro and [& forms]
  `(-> ~@forms)
)

;; (defmacro images [& forms]
;;   `(-> all-images
;;        ~@forms))
