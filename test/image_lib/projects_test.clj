(ns image-lib.projects-test
  (:require [clojure.test :refer :all]
            [expectations.clojure.test :refer :all]
            [image-lib.projects :as sut]
            [image-lib.utils.database :as db]
            [image-lib.utils.fixtures :as fixtures]))

(use-fixtures :once
  fixtures/database fixtures/seed-images)


(deftest all-projects
  []
  (expect
    ["1958/10/12-Test-Project"
     "1981/10/18-Test-Project"
     "1983/01/15-Test-Project"]
    (sut/all-projects (db/connection) db/images)))


(deftest project-paths
  []
  (expect
    ["1958/10/12-Test-Project/DIW_001.jpg"
     "1958/10/12-Test-Project/DIW_002.jpg"]
    (sut/project-paths (db/connection) db/images "1958" "10" "12-Test-Project")))
