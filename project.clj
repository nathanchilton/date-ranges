(defproject date_ranges_donkey "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [com.appsflyer/donkey "0.5.2"]
                 [clojure.java-time "1.1.0"]
                 [cheshire "5.11.0"]
                 [org.clojure/data.json "1.0.0"]
                 ]
  :plugins [[cider/cider-nrepl "0.28.5"]]
  :repl-options {:init-ns date-ranges-donkey.core})
