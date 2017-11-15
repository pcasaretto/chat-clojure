(defproject clojure-chat "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure "1.9.0-alpha16"]
                 [org.clojure/core.async "0.3.443"]
                 ]
  :plugins [[lein-cljfmt "0.5.7"]]
  :main ^:skip-aot clojure-chat.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
