(ns difflog.domain
  (:require [clojure.string :as string]))

(declare line-delimiter tokenized-word-diffs contains-diff? trans-preds
         difflogline-internal)

(defn difflogline [lhs rhs rules]
  (difflogline-internal lhs rhs (trans-preds rules)))

(defn difflog
  ([lhs rhs] (difflog lhs rhs {}))
  ([lhs rhs rules]
   (map #(difflogline-internal %1 %2 (trans-preds rules)) lhs rhs)))

(def ^:const line-delimiter (System/lineSeparator))
(def ^:const token-delimiter "[\\s\\[\\]]")
(def ^:const split-pattern (re-pattern (format "(?<=\\S)(?=%s)|(?<=%s)(?=\\S)"
                                               token-delimiter
                                               token-delimiter)))

(defn- to-num-or-na [xs]
  (try
    (vec (map #(Double/valueOf %) xs))
    (catch NumberFormatException _
      :na)))

(def ^{:doc (clojure.string/join "\n"
             ["transformer: [l r] -> [newl newr] | :na (not-applicable)"
              "  predicate-builder: x -> l r idx -> true | false"])
       :const true
       :private true}
  rule-def
  {:col {:transformer identity
         :predicate-builder (fn [x] (if (set? x)
                                      #(contains? x %3)
                                      #(= x %3)))}
   :num {:transformer to-num-or-na
         :predicate-builder identity}
   :word {:transformer identity
          :predicate-builder (fn [k v] (fn [l r _] (and (= k l) (= v r))))}})

(defn- string-trans-pred [k v]
  (let [rd (rule-def :word)
        transformer (:transformer rd)
        predicate (:predicate-builder rd)]
    [transformer (predicate k v)]))

(defn- trans-pred [[k v :as rule]]
  (if (string? k)
    (string-trans-pred k v)
    (let [trans-pred (rule-def k)
          transformer (:transformer trans-pred)
          predicate-builder (:predicate-builder trans-pred)]
      [transformer (predicate-builder v)])))

(defn- trans-preds [rules]
  (set (map trans-pred rules)))

(defn- ignore-diff? [lhs rhs idx [transformer predicate]]
  (let [transformed (transformer [lhs rhs])]
    (if (= :na transformed)
      false
      (apply #(predicate %1 %2 idx) transformed))))

(defn- diff [lhs rhs idx trans-preds]
  {:lhs lhs
   :rhs rhs
   :ignored (not-every? false? (map (partial ignore-diff? lhs rhs idx) trans-preds))})

(defn- diff-or-token
  "takes in two tokens. applies rules only if the tokens are different"
  [lhs rhs idx trans-preds]
  (if (= lhs rhs) lhs (diff lhs rhs idx trans-preds)))

(defn- tokenized-word-diffs [l r trans-preds]
  (let [lwords (.split split-pattern l)
        rwords (.split split-pattern r)
        range-from-1 (drop 1 (range))]
    (map #(diff-or-token %1 %2 %3 trans-preds) lwords rwords range-from-1)))

(defn- difflogline-internal [lhs rhs trans-preds]
  (let [twd (tokenized-word-diffs lhs rhs trans-preds)]
    (map #(if (string? (first %)) (string/join %) (first %))
         (partition-by #(string? %) twd))))

(defn- contains-diff? [line]
  (some vector? line))

(defn- conj-if-diff [coll line]
  (if (contains-diff? line)
    (conj coll line)
    coll))


(comment
  (seq (.split split-pattern "hello        world"))
  (line-word-diffs "hello world" "goodbye world" {})
  (partition-by #(string? %) [["a" "b"] " " "c"])
  (map #(if (string? (first %)) (string/join %) (first %))
       (partition-by #(string? %)
                     [["a" "b"] "           " "c"]))
  
  (str "a" "b")
  (#{:a} :a)
  (#{:a} :b)
  (contains? #{:a} :a)
  (contains? #{:a} :b)
  
  (into {} [[:a 1] [:b 2]])
  (seq? [])
  (seq? {})
  (sequential? #{:a})
  (sequential? [])
  (sequential? '())
  (set? #{:a})
  ({:a 1} :b 0)
  
  (to-num-or-error "hello" "world")

  (Double/valueOf "123hello")
  
  (#(identity % %&) 1 2)
  (map identity {:a 1}) ; ([:a 1])
  (some true? [false])
  
  (seq {:a 1, :b 2})
  (seq (.split "        hello  \t  world              " "\\"))
  (seq (.split " hello " "\\s+"))
  (seq (.split "hello=123 mundo[456]" "[\\s=\\[\\]]+"))
   
  (map + [1 2] [100 200])

  (difflog "hello world" "goodbye world")
  (conj [1 2] 3)
  (vector? [])
  (empty? (seq))
)
