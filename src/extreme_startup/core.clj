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
   :default :default))

(defmulti answer question-type)

(defmethod answer :name [question]
  "parenthesis")

(defmethod answer :default [question]
  question)

(defn without-id [question]
  (if-let [groups-without-id (re-find #"^.*: (.*)$" question)]
    (last groups-without-id)
    question))

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
