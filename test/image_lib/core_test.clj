(ns image-lib.core-test
  (:require [image-lib.core        :as ic]
            [image-lib.images      :as im]
            [image-lib.search      :as is]
            [image-lib.helper      :as ih]
            [image-lib.projects    :as ip]
            [image-lib.preferences :as pr]
            [monger
             [collection :as mc]
             [core :as mg]]
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
(expect "1958/10/12-Test-Project/DIW_002.jpg"
  (ih/image-path
    (last
      (sort-by
        :Rating
        (im/find-images db "images" "Keywords" "Iain")))))
(expect "DIW_123"
        (ih/version-name "/2015/02/09-Project/DIW_123.jpg"))
(expect "clojure-test"
        (ih/version-name "/tmp/clojure-test.jpg"))
(expect "/2015/02/09-Project"
  (ih/project-name "/2015/02/09-Project/DIW_123.jpg"))
(expect 3 (count (ip/all-projects db "images")))
(expect (ih/best [{:_id 1 :Rating "2.0"}
                  {:_id 2 :Rating "3.0"}])
  {:_id 2 :Rating "3.0"})
(expect (ih/image-path (ic/best-image db "images" "Iain"))
  "1958/10/12-Test-Project/DIW_002.jpg")
