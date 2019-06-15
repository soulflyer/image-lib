(ns image-lib.file-test
  (:require [clojure.test :refer :all]
            [expectations.clojure.test :refer :all]
            [image-lib.file :as sut]
            [image-lib.utils.fixtures :as fixtures]))


(use-fixtures :once
  fixtures/create-file)


(deftest file
  []
  (expect
    (sut/file-exists? "/tmp/image-lib-file-test.txt"))
  (expect
    (sut/related-file-exists? "/tmp/image-lib-file-test.png"))
  (expect
    (sut/loosely-related-file-exists? "/tmp/image-lib-file-test_version_1.png")))

;; TODO add tests for missing-files and overwrite
