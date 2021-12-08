(ns basic-http-server.env
  (:require
    [clojure.tools.logging :as log]
    [basic-http-server.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[basic-http-server started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[basic-http-server has shut down successfully]=-"))
   :middleware wrap-dev})
