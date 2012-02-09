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
  (without-id "d1234567: what is this about") => "what is this about"
  (without-id "abc") => "abc")

;.;. Simplicity, carried to the extreme, becomes elegance. -- Jon Franklin
(facts "about answer"
  (answer "what is your name") => "parenthesis")
;.;. There's a certain satisfaction in a little bit of pain. -- Madonna
