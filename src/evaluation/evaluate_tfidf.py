"""
Evaluate the tfidf model
"""
# pylint: disable= R0801
from ast import literal_eval
from sklearn.metrics import accuracy_score
from sklearn.metrics import f1_score
from sklearn.metrics import average_precision_score
from sklearn.metrics import roc_auc_score as roc_auc
from sklearn.preprocessing import MultiLabelBinarizer
from joblib import load
import pandas as pd
from src.transformation.transformer_tfidf import transform_tfidf
from src.preprocessing.preprocessing_data import preprocess_data


def print_evaluation_scores(y_val, predicted):
    """
    prints the scores of the tfidf model
    return: None
    """
    print('Accuracy score: ', accuracy_score(y_val, predicted))
    print('F1 score: ', f1_score(y_val, predicted, average='weighted'))
    print('Average precision score: ', average_precision_score(y_val, predicted, average='macro'))

def read_data(filename):
    """
      filename — name of the tsv file
      return: panda dataframe
    """
    data = pd.read_csv(filename, sep='\t')
    data['tags'] = data['tags'].apply(literal_eval)
    return data

def main():
    """
      Evaluates the tfidf model
      return: None
    """
    classifier_tfidf = load('output/model_tfidf.joblib')

    ## data being used
    validation = read_data('data/validation.tsv')

    x_val, y_val = preprocess_data(validation)
    # pylint: disable=W0612
    x_val_tfidf, tfidf_vocab, tfidf_reversed_vocab, tags_counts = \
        transform_tfidf(x_val, y_val, False)

    y_val_predicted_labels_tfidf = classifier_tfidf.predict(x_val_tfidf)
    y_val_predicted_scores_tfidf = classifier_tfidf.decision_function(x_val_tfidf)

    mlb = MultiLabelBinarizer(classes=sorted(tags_counts.keys()))
    y_val = mlb.fit_transform(y_val)

    print('Tfidf')
    print_evaluation_scores(y_val, y_val_predicted_labels_tfidf)

    roc_auc(y_val, y_val_predicted_scores_tfidf, multi_class='ovo')

if __name__  == "__main__":
    main()
