"""
Flask API of the SMS Spam detection model model.
"""
import joblib
from flask import Flask, jsonify, request
from flasgger import Swagger
from src.preprocessing.preprocessing_data import text_prepare
from src.transformation.transformer_mybag import transform_mybag_eval
from src.transformation.transformer_tfidf import tfidf_features

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
    mybag_pred = predict_mybag(processed_title)
    tfidf_pred = predict_tfidf(processed_title)
    res = {
        "mybag_predictions": mybag_pred,
        "tfidf_predictions": tfidf_pred,
        "title": title #set to title
    }
    print(res)
    return jsonify(res)

def predict_mybag(processed_title):
    """
      processed_title: title after being processed
      return: list of the preditions of the mybag model
    """
    title_mybag, tags_counts = transform_mybag_eval([processed_title]) # pylint: disable= W0612
    model = joblib.load('output/model_mybag.joblib')
    prediction = model.predict(title_mybag)
    mlb = joblib.load('output/mlb_mybag.joblib')
    inv_pred = mlb.inverse_transform(prediction)
    results = []
    print(inv_pred)
    for i in inv_pred[0]:
        results.append(i)
    return results

def predict_tfidf(processed_title):
    """
      processed_title: title after being processed
      return: list of the preditions of the tfidf model
    """
    title_tfidf, tfidf_vocab = tfidf_features([processed_title], False) # pylint: disable= W0612
    model = joblib.load('output/model_tfidf.joblib')
    prediction = model.predict(title_tfidf)
    mlb = joblib.load('output/mlb_tfidf.joblib')
    inv_pred = mlb.inverse_transform(prediction)
    results = []
    for i in inv_pred[0]:
        results.append(i)
    return results

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=8080, debug=True)
