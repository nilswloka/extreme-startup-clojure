(ns extreme-startup.test.core
  (:use [extreme-startup.core] :reload)
  (:use [midje.sweet])
  (:use [clojure.test]))

(fact "handler uses answer"
  (handler ...question...) =>
  {:status 200, :headers {"Content-Type" "text/plain"}, :body ...answer...}
  (provided
    (without-id ...question...) => ...question-without-id...
    (answer ...question-without-id...) => ...answer...))

(fact "without-id removes everything up to the space following the colon from a question"
  (without-id "d1234567: what is this about") => "what is this about")

;.;. The journey is the reward. -- traditional
(facts "about answer"
  (answer "what is your name") => "B-klammerbeutel"
  (answer "what is 7 plus 1") => "8"
  (answer "which of the following number is the largest: 74, 60, 701") => "701"
  (answer "what is 1 multiplied by 9") => "9")
