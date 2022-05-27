from ast import literal_eval
import pandas as pd
from joblib import dump
from sklearn.preprocessing import MultiLabelBinarizer
from src.classification.train_mybag import train_classifier

from src.preprocessing.preprocessing_data import preprocess_data
from src.transformation.transformer_mybag import transform_mybag


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
    X_train, y_train, X_val, y_val, X_test = preprocess_data(train, validation, test)
    #transform data
    X_train_mybag, X_val_mybag, X_test_mybag, tags_counts = transform_mybag(X_train, y_train, X_val, X_test)
    # train
    mlb = MultiLabelBinarizer(classes=sorted(tags_counts.keys()))
    y_train = mlb.fit_transform(y_train)
    y_val = mlb.fit_transform(y_val)

    classifier_mybag = train_classifier(X_train_mybag, y_train)

    dump(classifier_mybag, 'output/model_mybag.joblib')

if __name__ == "__main__":
    main()
