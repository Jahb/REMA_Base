"""
Flask API of the SMS Spam detection model model.
"""
#import traceback
import joblib
from flask import Flask, jsonify, request
from flasgger import Swagger
import pandas as pd

from sklearn.preprocessing import MultiLabelBinarizer
from preprocessing.text_processing import text_prepare

app = Flask(__name__)
swagger = Swagger(app)

@app.route('/predict', methods=['POST'])
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
    input_data = request.get_json()
    title = input_data.get('title')
    processed_title = text_prepare(title)
    model = joblib.load('output/model.joblib')
    tags_counts = joblib.load('output/tags_counts.joblib')
    prediction = model.predict(processed_title)
    mlb = MultiLabelBinarizer(classes=sorted(tags_counts.keys()))
    inv_pred = mlb.inverse_transform(prediction)
    results = []
    for i in inv_pred:
      results.append(i)
    res = {
        "result": results,
        "title": title
    }
    print(res)
    return jsonify(res)

if __name__ == '__main__':
    #clf = joblib.load('output/model.joblib')
    app.run(host="0.0.0.0", port=8080, debug=True)