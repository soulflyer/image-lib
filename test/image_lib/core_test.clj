(ns image-lib.core-test
  (:require [image-lib.core :refer :all]
            [monger
             [collection :as mc]
             [core :as mg]])
  (:use expectations))

(def db (mg/get-db (mg/connect) "test"))
(expect (= 1 1))

(expect (let [ar [1 2 3]]
          (first ar)) 1 )

(expect (best-image db "images" "keywords" "people")
        "1981/10/18-Test-Project/DIW_002.jpg")
