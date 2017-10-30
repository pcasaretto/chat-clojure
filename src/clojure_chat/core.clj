(ns clojure-chat.core
  (:gen-class)
  (:require
    [clojure.core.async :as async]
    [clojure.java.io :as io])
  (:import (java.net ServerSocket)))

(defn echo-service
  [in out]
  (async/go-loop []
    (let [msg (async/<! in)]
      (async/>! out msg)
      (recur))))

(defn line-reader
  [sock]
  (let [in (async/chan)
        reader (io/reader sock)]
    (async/go-loop []
      (let [msg (.readLine reader)]
        (async/>! in msg))
      (recur))
    in))

(defn echo-writer
  [sock]
  (let [out (async/chan)
        writer (io/writer sock)]
    (async/go-loop []
      (let [msg (async/<! out)]
        (.write writer (str "Echoing: " msg)))
      (.flush writer)
      (recur))
    out))

(defn -main
  []
  (println "Starting server ...")
  (let [server (ServerSocket. 32665)]
      (loop []
        (let [sock (.accept server)]
        (println (format "Listening on socket %s ..." sock))
        (async/go
          (echo-service
            (line-reader sock)
            (echo-writer sock)))
        (recur )))
    (.join (Thread/currentThread))))
