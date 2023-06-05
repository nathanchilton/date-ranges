(ns date-ranges-donkey.core-test
  (:require [clojure.test :refer :all]
            [date-ranges-donkey.core :refer :all]))

(def expected-result-happy-path {:status 200 :body "2022-01-01_2022-01-03,2022-01-10_2022-01-10"})

(deftest happy-path
  (testing "happy-path (Derek's original user story)"
    (is (= (generate-date-ranges {:body "2022-01-01,2022-01-02,2022-01-03,2022-01-10"  :headers {"content-type" "application/text"}})
           expected-result-happy-path))))

(deftest happy-path-out-of-order
  (testing "happy-path with dates out of order"
    (is (= (generate-date-ranges {:body "2022-01-02,2022-01-03,2022-01-10,2022-01-01"  :headers {"content-type" "application/text"}})
           expected-result-happy-path))))

(deftest happy-path-out-of-order-duplicates
  (testing "happy-path with dates out of order and duplicates"
    (is (= (generate-date-ranges {:body "2022-01-03,2022-01-02,2022-01-03,2022-01-10,2022-01-03,2022-01-03,2022-01-01,2022-01-03,"  :headers {"content-type" "application/text"}})
           expected-result-happy-path))))

(deftest empty-input
  (testing "what if the input is empty"
    (is (= (generate-date-ranges {:body ""  :headers {"content-type" "application/text"}})
           {:status 400 :body "No dates were found in the request."}))))

(deftest invalid-date-values
  (testing "invalid date values (leap day exists in 2020, but not 2022)"
    (is (= (generate-date-ranges {:body "2022-01-01,2022-02-29,2020-02-29"  :headers {"content-type" "application/text"}})
           {:status 400 :body "These dates contained invalid values:\n[\"2022-02-29\"]"}))))

(deftest invalid-date-format-short-year
  (testing "invalid date format: two-digit year"
    (is (= (generate-date-ranges {:body "2022-01-01,2022-02-29,20-02-29"  :headers {"content-type" "application/text"}})
           {:status 400 :body "These dates did not match the expected format:\n[\"20-02-29\"]"}))))
