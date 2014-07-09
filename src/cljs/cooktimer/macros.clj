(ns cooktimer.macros)

(defmacro go-forever [& body]
  `(cljs.core.async.macros/go (while true ~@body)))
