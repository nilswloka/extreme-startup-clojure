(ns extreme-startup.core
  (:use ring.middleware.stacktrace
        ring.middleware.keyword-params
        ring.middleware.params
        ring.middleware.reload)
  (:use compojure.core)
  (:require [extreme-startup.logging :as logging])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]))

(defn question-type [question]
  (cond
   (.contains question "what is your name") :name
   (.contains question "plus") :plus
   (.contains question "largest") :max
   (and (.contains question "multiplied") (.contains question "plus")) :mult-plus
   (.contains question "multiplied") :times
   (.contains question "both a square and a cube") :square-cube
   (.contains question "primes") :primes
   (.contains question "minus") :minus
   (.contains question "Fibonacci") :fibs
   (.contains question "power") :power
   :default :default))

(defn extract-numbers [q]
  (let [number-strings (re-seq #"\d+" q)
        numbers (map #(Integer/parseInt %) number-strings)]
    numbers))

(defmulti answer question-type)

(defmethod answer :name [question]
  "B-klammerbeutel")

(defmethod answer :plus [question]
  (let [numbers (extract-numbers question)]
    (str (apply + numbers))))

(defmethod answer :minus [question]
  (let [numbers (extract-numbers question)]
    (str (apply - numbers))))

(defmethod answer :max [question]
  (let [numbers (extract-numbers question)]
    (str (apply max numbers))))

(defmethod answer :power [question]
  (let [numbers (extract-numbers question)
        a (first numbers)
        b (second numbers)]
    (str (BigInteger/valueOf (Math/pow a b)))))

(defmethod answer :times [question]
  (let [numbers (extract-numbers question)]
    (str (apply * numbers))))

(defn fib-step [[a b]]
  [b (+ a b)])
 
(defn fib-seq []
  (map first (iterate fib-step [0 1])))

(defn sieve [n]
  (let [n (int n)]
    "Returns a list of all primes from 2 to n"
    (let [root (int (Math/round (Math/floor (Math/sqrt n))))]
      (loop [i (int 3)
             a (int-array n)
             result (list 2)]
        (if (>= i n)
          (reverse result)
          (recur (+ i (int 2))
                 (if (< i root)
                   (loop [arr a
                          inc (+ i i)
                          j (* i i)]
                     (if (>= j n)
                       arr
                       (recur (do (aset arr j (int 1)) arr)
                              inc
                              (+ j inc))))
                   a)
                 (if (zero? (aget a i))
                   (conj result i)
                   result)))))))

(defmethod answer :primes [question]
  (let [numbers (extract-numbers question)
        max-number (apply max numbers)
        the-primes (sieve max-number)
        set-of-primes (into #{} the-primes)
        primes-answer (filter set-of-primes numbers)
        with-commas (reverse (rest (reverse (interleave primes-answer (repeat ", ")))))]
    (apply str with-commas)))

(defmethod answer :fibs [question]
  (let [numbers (extract-numbers question)
        n (first numbers)
        fibs (take (inc n) (fib-seq))]
    (str (first (reverse fibs)))))

(defn sq-and-cube [number]
  (and (= 0 (rem number (Math/sqrt number))) (= 0 (rem number (Math/pow number (/ 1 3))))))

(defmethod answer :square-cube [question]
  (let [numbers (extract-numbers question)]
    (str ((filter sq-and-cube numbers)))))

(defmethod answer :default [question]
  (cond (.contains question "Dr No") "Sean Connery"
        (.contains question "banana") "yellow"
        (.contains question "Eiffel") "Paris"
        (.contains question "Spain") "Peseta"
        (.contains question "Great Britain") "David Cameron"
        :default question))

(defn without-id [question]
  (subs question 10))

(defn handler [q]
  (let [question-with-id q
        question (without-id question-with-id)
        answer (answer question)]
    (logging/add-entry! (str q ": " answer))
    {:status 200,
     :headers {"Content-Type" "text/plain"},
     :body answer}))

(defn log-view []
  (let [list-of-log-entries (map (fn [entry] (str "<li>" entry "</li>")) (logging/all-entries))
        view (str "<ul>" (apply str list-of-log-entries) "</ul>")]
    {:status 200,
     :headers {"Content-Type" "text/html",
               "Refresh" "3; http://localhost:8081/log"},
     :body view}))

(defroutes all-routes
  (GET "/" {{q :q} :params} (handler q))
  (GET "/log" [] (log-view)))

(def application
  (handler/site all-routes))
