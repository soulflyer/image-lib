(ns image-lib.file-helper
  (:require [image-lib.helper :refer [version-name]]
            [image-lib.images :refer [image-paths]]))

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
  [db image-collection root-path find-function]
  (remove
    (fn [im] (find-function (str root-path "/" im)))
    (image-paths db image-collection)))
