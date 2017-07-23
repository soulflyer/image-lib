(ns image-lib.helper
  (:require [clojure.string :as str]))

(defn clean-number-string
  "returns a number when given a string. Leading and trailing text and anything before a / character is removed"
  [x]
  (if (clojure.core/and x (not (= x "")))
    ;; This regex pulls out a substing containing only digits and dots.
    (re-find #"[\d\.]+"
             (str/replace (str x)
                      ;;This regex removes a leading 1/ or f/
                      #"^[fF1]/"
                      ""))
    ""))

(defmulti string-number-equals
  "a version of = that can compare numbers, strings or one of each"
  (fn [x y] (cond
             (clojure.core/or (nil? x) (nil? y)) :empty
             (clojure.core/and (instance? String x) (instance? String y)) :2strings
             :else :other)))
(defmethod string-number-equals :other [x y]
  (= (bigdec (clean-number-string x)) (bigdec (clean-number-string y))))
(defmethod string-number-equals :2strings [x y]
  (= x y))
(defmethod string-number-equals :empty [x y]
  (cond (clojure.core/and (nil? x) (nil? y)) true
        (clojure.core/or (= "" x) (= "" y)) true
        :else false))

(defn version-name
  "Cuts the extension off the end of a string"
  [filename]
  (let [index-dot (if (= -1 (.lastIndexOf filename "."))
                    (count filename)
                    (.lastIndexOf filename "."))
        index-slash (max 0 (+ 1 (.lastIndexOf filename "/")))]
    (if (< 0 index-dot)
      (subs filename index-slash index-dot)
      filename)))

(defn project-name
  "cuts the last part of the pathname off to leave yyyy/mm/project-name"
  [filename]
  (let [index-slash (.lastIndexOf filename "/")]
    (if (< 0 index-slash)
      (subs filename 0 index-slash)
      filename)))

(defn project-year
  "returns the year from a project name in the form yyyy/mm/project"
  [proj]
  (let [index-slash (.indexOf proj "/")]
    (if (< 0 index-slash)
      (subs proj 0 index-slash)
      proj)))
