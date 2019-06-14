(ns image-lib.keywords-test
  (:require [clojure.test :refer :all]
            [expectations.clojure.test :refer :all]
            [image-lib.keywords :as sut]
            [image-lib.utils.database :as database]
            [image-lib.utils.database.seed :as seed]
            [image-lib.utils.fixtures :as fixtures]))

(use-fixtures :once
  fixtures/database fixtures/seed-keywords)


(deftest keywords
  (expect "Rachael"
    (in (sut/find-sub-keywords (database/connection) database/keywords "people"))))
