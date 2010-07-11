(ns validation.test.core
  (:use clojure.test validation.core validation.errors validation.test))

(refer-private 'validation.core)

(deftest test-validate-acceptance
  (testing "accepted attribute"
    (are [value]
      (is (valid? (validate-acceptance {:tos value} :tos)))
      "1"))
  (testing "not accepted attribute"
    (are [value]
      (let [record (validate-acceptance {:tos value} :tos)]
        (is (not (valid? record)))
        (is (= (error-messages-on record :tos) ["must be accepted."])))
      nil "" "0")))

(deftest test-validate-confirmation
  (testing "valid attribute"
    (is (valid? (validate-confirmation {:password "secret" :password-confirmation "secret"} :password))))
  (testing "invalid attribute"
    (are [value]
      (let [record (validate-confirmation {:password "secret" :password-confirmation value} :password)]
        (is (not (valid? record)))
        (is (= (error-messages-on record :password) ["doesn’t match confirmation."])))
      nil "" "invalid")))

(deftest test-validate-email
  (testing "valid email address"
    (are [value]
      (is (valid? (validate-email {:email value} :email)))
      "info@domain.com" "first.last@sub.domain.com"))
  (testing "invalid email address"
    (are [value]
      (let [record (validate-email {:email value} :email)]
        (is (not (valid? record)))
        (is (= (error-messages-on record :email) ["is not a valid email address."])))
      nil "" "root" "root@localhost")))

(deftest test-validate-exact-length
  (testing "valid attribute"
    (are [value]
      (is (valid? (validate-exact-length {:country value} :country 2)))
      "de" "es"))
  (testing "invalid attribute"
    (are [value]
      (let [record (validate-exact-length {:country value} :country 2)]
        (is (not (valid? record)))
        (is (= (error-messages-on record :country) ["has the wrong length (should be 2 characters)."])))
      nil "" "deu" "esp")))

(deftest test-confirmation-attribute
  (is (= (confirmation-attribute "password") :password-confirmation))
  (is (= (confirmation-attribute :password) :password-confirmation)))

(deftest test-validate-exclusion
  (testing "valid attribute"
    (are [value]
      (is (valid? (validate-exclusion {:nick value} :nick ["admin" "root"])))
      nil "" "test"))
  (testing "invalid attribute"
    (are [value]
      (let [record (validate-exclusion {:nick value} :nick ["admin" "root"])]
        (is (not (valid? record)))
        (is (= (error-messages-on record :nick) ["is reserved."])))
      "admin" "root")))

(deftest test-validate-format
  (testing "valid attribute"
    (are [value]
      (is (valid? (validate-format {:nick value} :nick #"(?i)[a-z0-9]+")))
      "nick" "n1ck" ))
  (testing "invalid attribute"
    (are [value]
      (let [record (validate-format {:nick value} :nick #"(?i)[a-z0-9]+")]
        (is (not (valid? record)))
        (is (= (error-messages-on record :nick) ["is invalid."])))
      nil "" "!" "\"" "§" "$" "%" "&" "/" "(" ")" "=" "?" "`" "´")))

(deftest test-validate-inclusion
  (testing "valid attribute"
    (are [value]
      (is (valid? (validate-inclusion {:gender value} :gender ["m" "f"])))
      "m" "f"))
  (testing "invalid attribute"
    (are [value]
      (let [record (validate-inclusion {:gender value} :gender ["admin" "root"])]
        (is (not (valid? record)))
        (is (= (error-messages-on record :gender) ["is not a valid option."])))
      nil "" "x")))

(deftest test-validate-max-length
  (testing "valid attribute"
    (are [value]
      (is (valid? (validate-max-length {:nick value} :nick 5)))
      nil "" "1" "12" "123" "1234" "1234"))
  (testing "invalid attribute"
    (are [value]
      (let [record (validate-max-length {:nick value} :nick 5)]
        (is (not (valid? record)))
        (is (= (error-messages-on record :nick) ["is too long (maximum is 5 characters)."])))
      "12345" "123456")))

(deftest test-validate-min-length
  (testing "valid attribute"
    (are [value]
      (is (valid? (validate-min-length {:nick value} :nick 2)))
      "12" "123" "1234" "1234"))
  (testing "invalid attribute"
    (are [value]
      (let [record (validate-min-length {:nick value} :nick 2)]
        (is (not (valid? record)))
        (is (= (error-messages-on record :nick) ["is too short (minimum is 2 characters)."])))
      nil "" "1")))

(deftest test-validate-presence
  (testing "valid attribute"
    (are [value]
      (is (valid? (validate-presence {:nick value} :nick)))
      "x" "root"))
  (testing "invalid attribute"
    (are [value]
      (let [record (validate-presence {:nick value} :nick)]
        (is (not (valid? record)))
        (is (= (error-messages-on record :nick) ["can't be blank."])))
      nil "")))
