(ns clojure-chat.core
  (:gen-class)
  (:require
   [clojure.core.async :as async]
   [clojure.java.io :as io])
  (:import (java.net ServerSocket)))

(defn copy
  [in out]
  (async/go-loop []
    (let [msg (async/<! in)]
      (if (nil? msg)
        (do
          (async/>! out "Other party disconnected")
          (async/close! out))
        (do
          (async/>! out msg)
          (recur))))))

(defn sock-reader
  [sock]
  (let [in (async/chan)
        reader (io/reader sock)]
    (async/go-loop []
      (let [msg (.readLine reader)]
        (if (nil? msg)
          (do
            (async/close! in)
            (.close sock))
          (do
            (async/>! in msg)
            (recur)))))
    in))

(defn sock-writer
  [sock]
  (let [out (async/chan)
        writer (io/writer sock)]
    (async/go-loop []
      (let [msg (async/<! out)]
        (if (nil? msg)
          (do
            (.close sock))
          (do
            (.write writer (str msg "\n"))
            (.flush writer)
            (recur)))))
    out))

(defn -main
  []
  (println "Starting server ...")
  (let [server (ServerSocket. 32665)]
    (loop []
      (let [sock1 (.accept server)
            sock2 (.accept server)]
        (println (format "Listening on socket %s %s ..." sock1 sock2))
        (async/go
          (copy
           (sock-reader sock1)
           (sock-writer sock2)))
        (async/go
          (copy
           (sock-reader sock2)
           (sock-writer sock1)))
        (recur)))
    (.join (Thread/currentThread))))
