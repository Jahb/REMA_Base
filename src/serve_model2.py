"""
Flask API of the SMS Spam detection model model.
"""
#import traceback
import joblib
import pandas as pd
import numpy as np
from sklearn.preprocessing import MultiLabelBinarizer
from preprocessing.text_processing import text_prepare
from sklearn.feature_extraction.text import TfidfVectorizer
def predict():
    """
    Predict tags on StackOverflow based on the title
    ---
    consumes:
      - application/json
    parameters:
        - name: input_data
          in: body
          description: message to be classified.
          required: True
          schema:
            type: object
            required: title
            properties:
                title:
                    type: string
                    example: How to draw a stacked dotplot in R?
    responses:
      200:
        description: "The result of the classification is list of tags"
    """
    title = "How to draw a stacked dotplot in R?"
    processed_title = text_prepare(title)
    model = joblib.load('output/model.joblib')
    tags_counts = joblib.load('output/tags_counts.joblib')
    mlb = MultiLabelBinarizer(classes=sorted(tags_counts.keys()))
    tfidf_vectorizer = TfidfVectorizer(min_df=5, max_df=0.9, ngram_range=(1,2), token_pattern='(\S+)') ####### YOUR CODE HERE #######
    prediction = model.predict(tfidf_vectorizer.transform(processed_title))
    inv_pred = mlb.inverse_transform(prediction)
    results = []
    for i in inv_pred:
      results.append(i)
    resultstring = " ".join(results)
    print(resultstring)

if __name__ == '__main__':
    #clf = joblib.load('output/model.joblib')
    predict()