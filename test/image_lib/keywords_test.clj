(ns image-lib.keywords-test
  (:require [clojure.test :refer :all]
            [expectations.clojure.test :refer :all]
            [image-lib.keywords :as sut]
            [image-lib.utils.database :as database]
            [image-lib.utils.database.seed :as seed]
            [image-lib.utils.fixtures :as fixtures]))


(use-fixtures :once
  fixtures/database fixtures/seed-keywords)


(deftest sub-keywords
  (expect
    (sort ["people" "Kathryn" "Iain" "Rachael" "me"])
    (sort (sut/find-sub-keywords (database/connection) database/keywords "people"))))


(deftest keyword-path
  (expect
    ["people" "me" "Iain"]
    (sut/keyword-path (database/connection) database/keywords "Iain")))


(deftest parents
  (expect
    "me"
    (sut/parent (database/connection) database/keywords "Iain")))


(deftest add-move-delete
  (sut/add-keyword (database/connection) database/keywords "cat" "Root")
  (sut/add-keyword (database/connection) database/keywords "animals" "Root")
  (expect
    (sort ["people" "Kathryn" "Iain" "Rachael" "me" "cat" "Root" "animals"])
    (sort (sut/find-sub-keywords (database/connection) database/keywords "Root")))
  (sut/move-keyword (database/connection) database/keywords "cat" "Root" "animals")
  (expect
    (sort ["animals" "cat"])
    (sort (sut/find-sub-keywords (database/connection) database/keywords "animals")))
  (sut/safe-delete-keyword (database/connection) database/keywords "animals")
  (expect
    "animals"
    (in (sut/find-sub-keywords (database/connection) database/keywords "Root")))
  (sut/safe-delete-keyword (database/connection) database/keywords "cat")
  (expect
    ["animals"]
    (sort (sut/find-sub-keywords (database/connection) database/keywords "animals"))))
