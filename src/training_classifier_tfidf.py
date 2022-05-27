from ast import literal_eval
import pandas as pd
from joblib import dump
from sklearn.preprocessing import MultiLabelBinarizer
from src.classification.train import train_classifier

from preprocessing.preprocessing_data import preprocess_data
from transformation.transformer_tfidf import transform_tfidf


def read_data(filename):
    data = pd.read_csv(filename, sep='\t')
    data['tags'] = data['tags'].apply(literal_eval)
    return data

def main():
    ## data being used
    train = read_data('data/train.tsv')
    validation = read_data('data/validation.tsv')
    test = pd.read_csv('data/test.tsv', sep='\t')
    
    #preprocess data
    print("Start Preprocessing")
    X_train, y_train, X_val, y_val, X_test = preprocess_data(train, validation, test)
    print("End Preprocessing")
    #transform data
    print("Start Transformation")
    X_train_tfidf, X_val_tfidf, X_test_tfidf, tfidf_vocab, tfidf_reversed_vocab, tags_counts = transform_tfidf(X_train, y_train, X_val, X_test)
    print("End Transformation")
    #train
    print("Start Training")
    mlb = MultiLabelBinarizer(classes=sorted(tags_counts.keys()))
    y_train = mlb.fit_transform(y_train)
    y_val = mlb.fit_transform(y_val)

    classifier_tfidf = train_classifier(X_train_tfidf, y_train)

    dump(classifier_tfidf, 'output/model_tfidf.joblib')
    print("Training done")

if __name__ == "__main__":
    main()
