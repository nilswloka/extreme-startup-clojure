(ns extreme-startup.core
  (:require [ring.adapter.jetty :as jetty])
  (:use ring.middleware.stacktrace
        ring.middleware.keyword-params
        ring.middleware.params
        ring.middleware.reload))

(defn without-id [question]
  (subs question 10))

(defn handler [request]
  (do (println (get (:params request) "q"))
      {:status 200,
       :headers {"Content-Type" "text/plain"},
       :body "parenthesis!"}))

(def application
  (-> handler
      wrap-params
      wrap-stacktrace))

(defonce server (jetty/run-jetty #'application {:port 8081, :join? false}))
