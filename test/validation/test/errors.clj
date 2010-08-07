(ns validation.test.errors
  (:use clojure.test validation.errors validation.test))

(refer-private 'validation.errors)

(def *user*
     {:name "Bob"
      :email "bob@example.com"
      :password "secret"
      :password-confirmation "secret"})

(def *continent*
     {:name "Europe"
      :iso-3166-alpha-2 "eu"
      :location {:latitude 0 :longitude 0}})

(deftest test-add-error-message-on
  (let [message "is not a valid email address."]
    (let [user (add-error-message-on *user* :email message)]
      (is (= (error-messages-on user :email) [message])))
    (is (= (-> *user*
               (add-error-message-on :email message)
               (add-error-message-on :email message))
           (add-error-message-on *user* :email message)))
    (is (= (error-messages
            (-> *user*
                (add-error-message-on :email "can't be blank.")
                (add-error-message-on :email "must be a valid email address.")))
           {:email ["can't be blank." "must be a valid email address."]}))))

(deftest test-add-error-message-on-nested
  (let [continent (add-error-message-on *continent* [:location :latitude] "must be between -90.0 and 90.0.")]
    (is (= (error-message-on continent [:location :latitude])
           "must be between -90.0 and 90.0."))
    (is (= (error-messages-on continent [:location :latitude])
           ["must be between -90.0 and 90.0."]))))

(deftest test-error-messages
  (is (= (error-messages *user*) nil))
  (let [messages {:name ["can't be blank"]}
        record (with-meta {:name ""} {:errors messages})]
    (is (= (error-messages record) messages))))

(deftest test-error-messages-on
  (is (= (error-messages-on *user* :email) nil))
  (let [record (with-meta {:name ""} {:errors {:name ["can't be blank"]}})]
    (is (= (error-messages-on record :name) ["can't be blank"]))))

(deftest test-error-messages-on-nested
  (let [continent (with-meta *continent* {:errors {:location {:latitude ["must be between -90.0 and 90.0."]}}})]
    (is (= (error-messages-on continent [:location :latitude])
           ["must be between -90.0 and 90.0."]))))

(deftest test-error-message-on
  (is (= (error-message-on *user* :email) nil))
  (let [record (with-meta {:name ""} {:errors {:name ["can't be blank"]}})]
    (is (= (error-message-on record :name) "can't be blank"))))

(deftest test-error-message-on-nested
  (let [continent (with-meta *continent* {:errors {:location {:latitude ["must be between -90.0 and 90.0."]}}})]
    (is (= (error-message-on continent [:location :latitude])
           "must be between -90.0 and 90.0."))))

(deftest test-format-error-message
  (is (nil? (format-error-message nil nil)))
  (is (= (format-error-message :email "is not a valid email address.")
         "Email is not a valid email address.")))

(deftest test-format-error-messages
  (is (empty? (format-error-messages nil)))
  (is (empty? (format-error-messages {})))
  (is (= (format-error-messages {:email ["is not a valid email address."] :nick ["can't be blank."]})
         ["Email is not a valid email address." "Nick can't be blank."])))

(deftest test-valid?
  (is (valid? {}))
  (is (valid? (with-meta {} {:errors {}})))
  (is (not (valid? (with-meta {} {:errors {:name ["can't be blank."]}})))))

