(ns {{ns}}.components.server
  (:require [taoensso.timbre :as timbre]
            [org.httpkit.server :refer [run-server]]
            [cronj.core :as cronj]
            [selmer.parser :as parser]
            [mount.core :refer [defstate]]
            [{{ns}}.components.config :refer [config]]
            [{{ns}}.components.handler :refer [handler]]
            [{{ns}}.session :as session]))

(defn destroy
  "destroy will be called when your application
   shuts down, put any clean up code here"
  []
  (timbre/info "{{name}} is shutting down...")
  (cronj/shutdown! session/cleanup-job)
  (timbre/info "shutdown complete!"))

(defn init
  "init will be called once when
   app is deployed as a servlet on
   an app server such as Tomcat
   put any initialization code here"
  [config]
  (when (= (:env config) :dev) (parser/cache-off!))
  ;;start the expired session cleanup job
  (cronj/start! session/cleanup-job)
  (timbre/info "\n-=[ {{name}} started successfully"
               (when (= (:env config) :dev) "using the development profile") "]=-"))

(defstate server :start (run-server handler {:port (get-in config [:config :port] 3000)})
          :stop (server))
