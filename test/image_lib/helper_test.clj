(ns image-lib.helper-test
  (:require [clojure.test :refer :all]
            [expectations.clojure.test :refer :all]
            [image-lib.helper :as sut]))


(deftest clean-number-string
  []
  (expect "800"
    (sut/clean-number-string "ISO800"))
  (expect "100"
    (sut/clean-number-string "1/100"))
  (expect "2.4"
    (sut/clean-number-string "f2.4")))


(deftest string-number-equals
  []
  (expect (sut/string-number-equals "100" 100)))


(deftest version-name
  []
  (expect
    "hello"
    (sut/version-name "1958/10/12/me/hello.jpg")))


(deftest project-name
  []
  (expect
    "1958/10/12/me"
    (sut/project-name "1958/10/12/me/hello.jpg")))


(deftest project-year
  []
  (expect
    "1958"
    (sut/project-year "1958/10/12-me")))


(deftest image-path
  []
  (expect
    "1958/10/12-me/hello.jpg"
    (sut/image-path {:Year "1958"
                     :Month "10"
                     :Project "12-me"
                     :Version "hello"})))


(deftest best
  []
  (expect (sut/best [{:_id 1 :Rating "2.0"}
                     {:_id 2 :Rating "3.0"}])
    {:_id 2 :Rating "3.0"}))
