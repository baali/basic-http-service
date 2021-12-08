(ns user
  "Userspace functions you can run by default in your local REPL."
  (:require
   [basic-http-server.config :refer [env]]
    [clojure.pprint]
    [clojure.spec.alpha :as s]
    [expound.alpha :as expound]
    [mount.core :as mount]
    [basic-http-server.core :refer [start-app]]
    [clojure.tools.namespace.repl]
    [basic-http-server.db.core]
    [conman.core :as conman]
    [luminus-migrations.core :as migrations]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(add-tap (bound-fn* clojure.pprint/pprint))

(clojure.tools.namespace.repl/set-refresh-dirs "dev" "src" "src-shared" "test")

(defn start
  "Starts application.
  You'll usually want to run this on startup."
  []
  (mount/start-without #'basic-http-server.core/repl-server))

(defn stop
  "Stops application."
  []
  (mount/stop-except #'basic-http-server.core/repl-server))

(defn restart
  "Restarts application."
  []
  (stop)
  (start))

(defn reset []
  (time
   (do
     (stop)
     (clojure.tools.namespace.repl/refresh :after `start))))

(defn restart-db
  "Restarts database."
  []
  (mount/stop #'basic-http-server.db.core/*db*)
  (mount/start #'basic-http-server.db.core/*db*)
  (binding [*ns* (the-ns 'basic-http-server.db.core)]
    (conman/bind-connection basic-http-server.db.core/*db* "sql/queries.sql")))

(defn reset-db
  "Resets database."
  []
  (migrations/migrate ["reset"] (select-keys env [:database-url])))

(defn migrate
  "Migrates database up for all outstanding migrations."
  []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback
  "Rollback latest database migration."
  []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))

(defn create-migration
  "Create a new up and down migration file with a generated timestamp and `name`."
  [name]
  (migrations/create name (select-keys env [:database-url])))


