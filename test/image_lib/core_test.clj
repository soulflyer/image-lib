(ns image-lib.core-test
  (:require [clojure.test :refer :all]
            [expectations.clojure.test :refer :all]
            [image-lib.core :as sut]
            [image-lib.helper :as helper]
            [image-lib.utils.database :as database]
            [image-lib.utils.fixtures :as fixtures]
            [monger.core :as mg]))

(use-fixtures :once fixtures/database fixtures/seed-images fixtures/seed-keywords)

(deftest best-image
  []
  (expect (helper/image-path (sut/best-image (database/connection) database/images "Iain"))
    "1958/10/12-Test-Project/DIW_002.jpg"))
