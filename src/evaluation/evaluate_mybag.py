from sklearn.metrics import accuracy_score
from sklearn.metrics import f1_score
from sklearn.metrics import roc_auc_score 
from sklearn.metrics import average_precision_score
from sklearn.metrics import recall_score
from joblib import load
import pandas as pd
from src.preprocessing.preprocessing_data import preprocess_data
from src.transformation.transformer_mybag import transform_mybag
from ast import literal_eval
from sklearn.metrics import roc_auc_score as roc_auc


def print_evaluation_scores(y_val, predicted):
    print('Accuracy score: ', accuracy_score(y_val, predicted))
    print('F1 score: ', f1_score(y_val, predicted, average='weighted'))
    print('Average precision score: ', average_precision_score(y_val, predicted, average='macro'))

def read_data(filename):
    data = pd.read_csv(filename, sep='\t')
    data['tags'] = data['tags'].apply(literal_eval)
    return data

def main():
    classifier_mybag = load('output/model_mybag.joblib')
    
    ## data being used
    train = read_data('data/train.tsv')
    validation = read_data('data/validation.tsv')
    test = pd.read_csv('data/test.tsv', sep='\t')

    X_train, y_train, X_val, y_val, X_test = preprocess_data(train, validation, test)
    X_train_mybag, X_val_mybag, X_test_mybag, tags_counts = transform_mybag(X_train, y_train, X_val, X_test)
    
    y_val_predicted_labels_mybag = classifier_mybag.predict(X_val_mybag)
    y_val_predicted_scores_mybag = classifier_mybag.decision_function(X_val_mybag)

    print('Bag-of-words')
    print_evaluation_scores(y_val, y_val_predicted_labels_mybag)

    roc_auc(y_val, y_val_predicted_scores_mybag, multi_class='ovo')

if __name__  == "__main__":
    main()