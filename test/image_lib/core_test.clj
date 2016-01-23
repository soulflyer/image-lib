(ns image-lib.core-test
  (:require [image-lib.core :refer :all]
            [monger
             [collection :as mc]
             [core :as mg]])
  (:use expectations))

(def db (mg/get-db (mg/connect) "test"))
(expect "Rachael"
        (in (find-sub-keywords db "keywords" "people")))
(expect {:foo 1} (in {:foo 1 :cat 4}))
(expect {:_id "1"}
        (in (first
             (find-images db  "images" "Keywords" "Kathryn"))))
(expect {:_id "1"}
        (in (first
             (find-images-containing db "images" "Keywords" "Kat"))))
(expect {:_id "2"}
        (in
         (last
          (sort-by :Rating
                   (find-all-images db "images" "keywords" "people")))))
(expect "1958/10/12-Test-Project/DIW_002.jpg"
        (image-path
         (last
          (sort-by
           :Rating
           (find-images db "images" "Keywords" "Iain")))))
(expect "DIW_123"
        (version-name "/2015/02/09-Project/DIW_123.jpg"))
(expect "clojure-test"
        (version-name "/tmp/clojure-test.jpg"))
(expect "/2015/02/09-Project"
        (project-name "/2015/02/09-Project/DIW_123.jpg"))
(expect true
        (file-exists? "/tmp/clojure-test.txt"))
(expect true
        (related-file-exists? "/tmp/clojure-test.png"))
(expect true
        (loosely-related-file-exists? "/tmp/clojure-test_version_1.png"))
(expect 3 (count (all-projects db "images")))
(expect (best [{:_id 1 :Rating "2.0"}
               {:_id 2 :Rating "3.0"}])
        {:_id 2 :Rating "3.0"})
(expect (image-path (best-image db "images" "Iain"))
        "1958/10/12-Test-Project/DIW_002.jpg")
(expect (image-path (best-image db "images" "keywords" "people"))
        "1981/10/18-Test-Project/DIW_002.jpg")
(expect (preference db "preferences" "masters-directory")
        "/Users/iain/Pictures")
