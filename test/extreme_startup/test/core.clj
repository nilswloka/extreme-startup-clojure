(ns extreme-startup.test.core
  (:use [extreme-startup.core])
  (:use [clojure.test]))

(deftest without-id-cuts-id
  (is (= without-id "dba94da0: what is 10 plus 32\n" "what is 10 plus 12\n")))