"""
Flask API of the SMS Spam detection model model.
"""
#import traceback
import joblib
from flask import Flask, jsonify, request
from flasgger import Swagger
import pandas as pd

from preprocessing.text_processing import text_prepare

app = Flask(__name__)
swagger = Swagger(app)

@app.route('/predict', methods=['POST'])
def predict():
    """
    Predict whether an SMS is Spam.
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
            required: sms
            properties:
                sms:
                    type: string
                    example: This is an example of an SMS.
    responses:
      200:
        description: "The result of the classification: 'spam' or 'ham'."
    """
    input_data = request.get_json()
    title = input_data.get('title')
    processed_title = text_prepare(title)
    model = joblib.load('output/model.joblib')
    prediction = model.predict(processed_title)
    print(prediction)
    res = {
        "result": prediction,
        "title": title
    }
    print(res)
    return jsonify(res)

if __name__ == '__main__':
    #clf = joblib.load('output/model.joblib')
    app.run(host="0.0.0.0", port=8080, debug=True)