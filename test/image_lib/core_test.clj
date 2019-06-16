(ns image-lib.core-test
  (:require [clojure.test :refer :all]
            [expectations.clojure.test :refer :all]
            [image-lib.core :as sut]
            [image-lib.helper :as helper]
            [image-lib.images :as images]
            [image-lib.keywords :as keywords]
            [image-lib.utils.database :as db]
            [image-lib.utils.fixtures :as fixtures]
            [monger.core :as mg]))


(use-fixtures :each fixtures/database fixtures/seed-images fixtures/seed-keywords)


(deftest add-and-remove-keyword
  []
  (let [image-id "19581012-Test-ProjectDIW_002"]
    (expect
      ["Iain"]
      (:Keywords (images/find-image (db/connection) db/images image-id)))
    (sut/add-keyword-to-photo (db/connection) db/images "me" image-id)
    (expect
      ["Iain" "me"]
      (:Keywords (images/find-image (db/connection) db/images image-id)))
    (sut/remove-keyword-from-photo (db/connection) db/images "me" image-id)
    (expect
      ["Iain"]
      (:Keywords (images/find-image (db/connection) db/images image-id)))))


(deftest rename-keyword
  []
  (let [image-id "19581012-Test-ProjectDIW_002"]
    (expect
      ["Iain"]
      (:Keywords (images/find-image (db/connection) db/images image-id)))
    (sut/rename-keyword (db/connection) db/keywords db/images "Iain" "Iain Wood")
    (expect
      "Iain Wood"
      (in (keywords/all-keywords (db/connection) db/keywords)))
    (expect
      (not (some #{"Iain"} (keywords/all-keywords (db/connection) db/keywords))))
    (expect
      ["Iain Wood"]
      (:Keywords (images/find-image (db/connection) db/images image-id)))))


(deftest merge-keyword
  []
  (let [image-id "19830115-Test-ProjectDIW_001"]
    (expect
      "color"
      (in (:Keywords (images/find-image (db/connection) db/images image-id))))
    (expect
      #{"Rachael" "Kathryn" "Iain" "I'm a missing keyword" "color" "colour"}
      (sut/used-keywords (db/connection) db/images))
    (sut/merge-keyword (db/connection) db/keywords db/images "color" "colour")
    (expect
      #{"Rachael" "Kathryn" "Iain" "I'm a missing keyword" "colour"}
      (sut/used-keywords (db/connection) db/images))
    (expect
      "colour"
      (in (:Keywords (images/find-image (db/connection) db/images image-id))))))


(deftest used-keywords
  []
  (expect
    #{"Rachael" "Kathryn" "Iain" "I'm a missing keyword" "color" "colour"}
    (sut/used-keywords (db/connection) db/images)))


(deftest unused-keywords
  []
  (expect
    #{"Root" "people" "me" "I'm an orphaned keyword"}
    (sut/unused-keywords (db/connection) db/images db/keywords)))


(deftest add-missing-keywords
  []
  (expect
    #{"I'm a missing keyword"}
    (sut/missing-keywords (db/connection) db/images db/keywords))
  (sut/add-missing-keywords (db/connection) db/images db/keywords)
  (expect
    "orphaned keywords"
    (in (keywords/all-keywords (db/connection) db/keywords)))
  (expect
    "I'm a missing keyword"
    (in (keywords/find-sub-keywords (db/connection) db/keywords "orphaned keywords"))))


(deftest add-orphaned-keywords
  []
  (expect
    "I'm an orphaned keyword"
    (in (sut/orphaned-keywords (db/connection) db/keywords)))
  (sut/add-orphaned-keywords (db/connection) db/keywords)
  (expect
    "I'm an orphaned keyword"
    (in (keywords/find-sub-keywords (db/connection) db/keywords "orphaned keywords"))))


(deftest best-image
  []
  (expect
    "1958/10/12-Test-Project/DIW_002.jpg"
    (helper/image-path (sut/best-image (db/connection) db/images "Iain"))))


(deftest best-sub-image
  []
  (expect
    "1981/10/18-Test-Project/DIW_002.jpg"
    (helper/image-path (sut/best-sub-image (db/connection) db/images db/keywords "people"))))
