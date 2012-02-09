(ns extreme-startup.server
  (:require [ring.adapter.jetty :as jetty])
  (:use [extreme-startup.core] :reload-all))

(defonce server (jetty/run-jetty #'application {:port 8081, :join? false}))

