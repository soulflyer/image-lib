(ns image-lib.images-test
  (:require [clojure.test :refer :all]
            [expectations.clojure.test :refer :all]
            [image-lib.helper :as helper]
            [image-lib.images :as sut]
            [image-lib.utils.database :as db]
            [image-lib.utils.fixtures :as fixtures]))

(use-fixtures :once
  fixtures/database fixtures/seed-images fixtures/seed-keywords)


(deftest images
  []
  (expect
    ["1958/10/12-Test-Project/DIW_001.jpg"
     "1958/10/12-Test-Project/DIW_002.jpg"]
    (helper/image-paths (sut/images (db/connection) db/images "1958" "10" "12-Test-Project"))))


(deftest find-images
  []
  (expect
    {:_id "1"}
    (in (first (sut/find-images (db/connection) db/images "Keywords" "Kathryn")))))


(deftest find-images-containing
  []
  (expect {:_id "1"}
    (in (first
          (sut/find-images-containing (db/connection) db/images "Keywords" "Kat")))))


(deftest find-all-images
  []
  (expect {:_id "2"}
    (in
      (last
        (sort-by :Rating
          (sut/find-all-images (db/connection) db/images db/keywords "people"))))))
