(ns date-ranges-donkey.core

  (:require [com.appsflyer.donkey.core :refer [create-donkey create-server]]
            [com.appsflyer.donkey.server :refer [start]]
            [cheshire.core :refer :all]))

(defn byte-array-to-string [byte-array]
  (apply str (map char byte-array)))

(require '[clojure.string :as str])

(require '[java-time.api :as jt]
         ;; for REPL experimentation
         'java-time.repl)

(defn parse
  "Parses a JSON-formatted string and returns a Clojure data structure."
  [s]
  (cheshire.core/parse-string s true))

(defn gen
  "Generates a JSON-formatted string from a Clojure data structure."
  [o]
  (cheshire.core/generate-string o {:pretty true}))

(defn check-date-format
  "Compare a string to a regex to see if it matches the expected date format."
  [date-string] (re-matches #"\d{4}-\d\d?-\d\d?" date-string))

(def checked-dates {:valid [] :invalid []})

(defn valid-or-invalid
  "Divide a list of date strings into collections of valid and invalid dates (depending upon whether they match a regular expression)."
  [yyyy-mm-dd]
  (if (nil? (check-date-format yyyy-mm-dd))
    :invalid
    :valid))

(defn ymd-to-date
  "Build a date object using the specified year, month, and day (of month)."
  [y m d]
  (jt/local-date y  m  d))

(defn validate-date-values
  "Divide a list of dates into collections of valid and invalid, depending upon whether or not they are actually valid dates."
  [date-list]

  (reduce
   (fn [results yyyy-mm-dd]
     (try
       (let [date-object   (apply ymd-to-date (map #(Integer/parseInt %) (str/split yyyy-mm-dd #"-")))]
         (update results :valid conj date-object))

       (catch Exception e
         (update results :invalid conj yyyy-mm-dd))))
   checked-dates
   date-list))

(defn validate-request-format
  [date-string-collection]
  (reduce
   (fn [results yyyy-mm-dd]
     (let [type (valid-or-invalid yyyy-mm-dd)]
       (update results type conj yyyy-mm-dd)))
   checked-dates
   (remove empty? (map str/trim date-string-collection))))

(defn build-range-vector
  "Evaluate a list of date objects and create a list of ranges of consecutive dates."
  [date-objects]

  (let [sorted-date-objects (sort date-objects)]
    (reduce
     (fn [ranges date-object]

       (let [last-range (last ranges)
             last-end (:end (last ranges))
             days   (jt/time-between last-end date-object :days)]

         (cond
          ;; If this date is only one day after the :end date, 
          ;; update the :end date of last element in this vector to be this date
           (= days 1)
           (conj (pop ranges) (assoc last-range :end date-object))
          ;;  If there is more than one day between this date and the :end, 
          ;;  add a new date-range element to the end of the vector
           (> days 1)
           (conj ranges {:begin date-object :end date-object})
          ;;  else, do nothing with this date and return an unmodified ranges object
           :else ranges)))
     [{:begin (first sorted-date-objects)
       :end (first sorted-date-objects)}]
     (rest sorted-date-objects))))

(defn generate-date-ranges [request]

  (let  [request-format (get (:headers request) "content-type")
         request-body (byte-array-to-string (:body request))
         date-string-collection (if (str/includes? request-format "application/json") (parse request-body) (str/split request-body #","))
         validated-date-strings (validate-request-format date-string-collection)]

    (println (str "request-format: " request-format))
    (println (str "Request:\n" validated-date-strings))

    (if (empty? (:invalid validated-date-strings))
      (if (empty? (:valid validated-date-strings))
        {:status 400
         :body "No dates were found in the request."}

        ;;  We have date values and all of them match the expected format
        (let [validated-date-objects (validate-date-values (:valid validated-date-strings))]
          (if (empty? (:invalid validated-date-objects))

            ;;  At this point, we know that there are valid date objects
            ;;  Let's build the list of ranges

            (let [range-vector (build-range-vector (:valid validated-date-objects))
                  ;; Now, format the range objects for either JSON or text format, depending upon the request type
                  range-string (if (str/includes? request-format "application/json")
                                 (str "{\"ranges\": " (gen (map (fn [range-object] {:begin (str (:begin range-object)) :end (str (:end range-object))}) range-vector)) "}" )
                                 (str/join "," (map (fn [range-object] (str (:begin range-object) "_" (:end range-object))) range-vector)))]

              ;; Return the list of ranges
              {:status 200
               :body range-string})

            ;; If the collection of invalid date objects was not empty:
            {:status 400
             :body (str "These dates contained invalid values:\n" (:invalid validated-date-objects))})))

      ;;  If the collection of invalid date formats was not empty (some date strings did not match the regex):
      {:status 400
       :body   (str "These dates did not match the expected format:\n" (:invalid validated-date-strings))})))

(defn -main
  []
  (let [port 8080]
    (->
     (create-donkey)
     (create-server
      {:port   port
       :routes [{;;  Support the same behavior for two different routes
                 {:path         "/date-ranges"}
                 {:path         "/dateListToRangeList"}
                 :methods      [:post]
                 :handler-mode :blocking
                 :consumes     ["text/plain" "application/json"]
                 :produces     ["text/plain" "application/json"]
                 :handler      generate-date-ranges}]})
     start)
    (println (str "Server listening on port " port))))
