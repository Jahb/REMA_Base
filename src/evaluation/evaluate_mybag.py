"""
Evaluate the mybag model
"""
# pylint: disable= R0801
from ast import literal_eval
from sklearn.metrics import accuracy_score
from sklearn.metrics import f1_score
from sklearn.metrics import average_precision_score
from sklearn.metrics import roc_auc_score as roc_auc
from joblib import dump, load
import pandas as pd

# disable for dvc
from src.preprocessing.preprocessing_data import preprocess_data
from src.transformation.transformer_mybag import transform_mybag_eval


def print_evaluation_scores(y_val, predicted):
    """
    prints the scores of the mybag model
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
      Evaluates the mybag model
      return: None
    """
    classifier_mybag = load('output/model_mybag.joblib')

    ## data being used

    validation = read_data('data/validation.tsv')

    x_val, y_val = preprocess_data(validation)
    x_val_mybag, tags_counts = transform_mybag_eval(x_val) # pylint: disable=W0612

    mlb = load('output/mlb_mybag.joblib')
    y_val = mlb.fit_transform(y_val)

    y_val_predicted_labels_mybag = classifier_mybag.predict(x_val_mybag)
    y_val_predicted_scores_mybag = classifier_mybag.decision_function(x_val_mybag)

    print('Bag-of-words')
    print_evaluation_scores(y_val, y_val_predicted_labels_mybag)

    print(roc_auc(y_val, y_val_predicted_scores_mybag, multi_class='ovo'))

def main_dvc():
    """ Main function for dvc. Use this when running dvc pipeline.
    """
    classifier_mybag = load('output/model_mybag_dvc.joblib')

    ## data being used
    x_val_mybag, y_val = load('output/transform_mybag_val.joblib') # pylint: disable=W0612

    mlb = load('output/mlb_mybag_dvc.joblib')
    y_val = mlb.fit_transform(y_val)

    y_val_predicted_labels_mybag = classifier_mybag.predict(x_val_mybag)
    y_val_predicted_scores_mybag = classifier_mybag.decision_function(x_val_mybag)

    print('Bag-of-words')
    print_evaluation_scores(y_val, y_val_predicted_labels_mybag)

    print(roc_auc(y_val, y_val_predicted_scores_mybag, multi_class='ovo'))
    dump(y_val_predicted_labels_mybag, 'output/y_val_predicted_scores_mybag.joblib')
    dump(y_val_predicted_scores_mybag, 'output/y_val_predicted_scores_labels_mybag.joblib')

if __name__  == "__main__":
    main()
