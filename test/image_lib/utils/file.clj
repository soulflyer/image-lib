(ns image-lib.utils.file
  (:require [clojure.java.io :as io]))


(defn create

  ([]
   (create "/tmp/clojure-test.txt"))

  ([filename]
   (let [wrtr (io/writer filename)]
     (.write wrtr "you can delete me\n")
     (.close wrtr))))
