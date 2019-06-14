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


(deftest move-keyword
  (expect
    ["me"]
    (sut/find-sub-keywords (database/connection) database/keywords "me"))
  (sut/move-keyword (database/connection) database/keywords "Iain" "people" "me")
  (expect
    (sort ["Iain" "me"])
    (sort (sut/find-sub-keywords (database/connection) database/keywords "me"))))


(deftest keyword-path
  (expect
    ["people" "me" "Iain"]
    (sut/keyword-path (database/connection) database/keywords "Iain")))

(deftest parents
  (expect
    "people"
    (sut/parent (database/connection) database/keywords "Iain")))
