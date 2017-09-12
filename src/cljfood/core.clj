(ns cljfood.core
  (:gen-class)
  (:require [net.cgrand.enlive-html :as html]
            [org.httpkit.client :as http]
            [clojurewerkz.balagan.core :as b]))

(def recipies ["https://www.bbcgoodfood.com/recipes/thai-green-pork-lettuce-cups"
               "https://www.bbcgoodfood.com/recipes/omelette-pancakes-tomato-pepper-sauce"
               "https://www.bbcgoodfood.com/recipes/chicken-fattoush"
               "https://www.bbcgoodfood.com/recipes/pomegranate-chicken-almond-couscous"])

(defn get-url
  "Get the dom for the URL provided"
  ([]
   (get-url (rand-nth recipies)))
  ([url]
   (:body @(http/get url {:insecure? true}))))

(defn get-dom
  [html]
  (html/html-snippet html))

(defn title
  "Extract the title of a recipe"
  [dom]
  (-> dom
      (html/select [:h1.recipe-header__title])
      first
      :content
      first))

(defn prep-time
  "Extract the prepping time from a recipe"
  [dom]
  (-> dom
      (html/select [:span.recipe-details__cooking-time-prep :span.mins])
      first
      :content
      first))

(defn cooking-time
  "Extract the cooking time from a recipe"
  [dom]
  (-> dom
      (html/select [:span.recipe-details__cooking-time-cook :span.mins])
      first
      :content
      first))

(defn skill-level
  "Extract the skill level from a recipe"
  [dom]
  (-> dom
      (html/select [:section.recipe-details__item--skill-level :span.recipe-details__text])
      first
      :content
      first
      clojure.string/trim))

(defn servings
  "Extract the number of servings from a recipe"
  [dom]
  (-> dom
      (html/select [:section.recipe-details__item--servings :span.recipe-details__text])
      first
      :content
      first
      clojure.string/trim))

(defn nutrition-value
  "Extract a single nutrition value (like 'sodiumContent' for instance) from the dom. "
  [dom value]
  (let [v (html/select dom [:ul.nutrition :li :span.nutrition__value])]
    (->
     (filter #(= (:itemprop (:attrs %)) value) v)
     first
     :content
     first)))

(defn nutrition
  "Extract nutrition data such as kcal, fat, saturates, carbs, sugars, fibre, protein and salt from a recipe"
  [dom]
  {:fat (nutrition-value dom "fatContent")
   :calories (nutrition-value dom "calories")
   :satfat (nutrition-value dom "saturatedFatContent")
   :carbs (nutrition-value dom "carbohydrateContent")
   :sugar (nutrition-value dom "sugarContent")
   :fibre (nutrition-value dom "fiberContent")
   :protein (nutrition-value dom "proteinContent")
   :salt (nutrition-value dom "sodiumContent")})

(defn extract-ingredients
  [node]
  (cond
    (= (count (:content node)) 1)
    (first (:content node))
    (> (count (:content node)) 1)
    (str
     (first (:content node))
     (-> node
         (html/select [:a.ingredients-list__glossary-link])
         first
         :content
         first))))

(defn get-ingredients
  [dom]
  (let [items (html/select dom [:li.ingredients-list__item])]
    (map extract-ingredients items)
    ))

(defn -main
  []
  (print "Nothing to see here, yet."))
