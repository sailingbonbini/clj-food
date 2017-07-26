(set-env! :dependencies
          '[
            [boot/core "2.0.0-rc8"]
            [org.clojure/clojure "1.8.0"]
            [cpmcdaniel/boot-copy "1.0"]
            [http-kit "2.2.0"]
            [enlive "1.1.6"]]
          :source-paths #{"src/"})

(require '[cpmcdaniel.boot-copy :refer :all])

(task-options!
  pom {:project 'clj-food
       :version "0.0.2"}
  jar {:main 'cljfood.core}
  aot {:all true}
  copy {
       :output-dir "./target"
       :matching #{#"\.jar$"}}
  uber {})
 
(deftask build
  "Create a standalone jar."
  []
  (comp (aot) (pom) (uber) (jar) (copy)))