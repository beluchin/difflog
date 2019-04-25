(ns difflog.domain.num)

(declare drop-last-arg)
(defn diff-no-larger-than [x]
  (comp #(< % 0.1) #(Math/abs %) (drop-last-arg -)))

(defn- drop-last-arg [fn]
  #(apply fn (drop-last %&)))
