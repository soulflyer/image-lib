(ns image-lib.core-test
  (:require [image-lib.core        :as ic]
            [image-lib.images      :as im]
            [image-lib.helper      :as ih]

            [monger [core :as mg]]
            ;; TODO Need to switch to clojure-test expectations but that means using deftest first
            ;;[expectations.clojure.test :refer :all]
            [expectations :refer :all]            ))

;; (use-fixtures :once ??)

(def db (mg/get-db (mg/connect) "test"))

(expect {:_id "1"}
  (in (first
        (im/find-images db  "images" "Keywords" "Kathryn"))))
(expect {:_id "1"}
  (in (first
        (im/find-images-containing db "images" "Keywords" "Kat"))))
(expect {:_id "2"}
  (in
    (last
      (sort-by :Rating
        (im/find-all-images db "images" "keywords" "people")))))

(expect (ih/image-path (ic/best-image db "images" "Iain"))
  "1958/10/12-Test-Project/DIW_002.jpg")
