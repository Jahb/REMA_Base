from ast import literal_eval
import pandas as pd
from joblib import dump
from sklearn.preprocessing import MultiLabelBinarizer
from src.classification.train import train_classifier

from src.preprocessing.preprocessing_data import preprocess_data
from src.transformation.transformer_tfidf import transform_tfidf


def read_data(filename):
    data = pd.read_csv(filename, sep='\t')
    data['tags'] = data['tags'].apply(literal_eval)
    return data

def main():
    ## data being used
    train = read_data('data/train.tsv')
    
    #preprocess data
    print("Start Preprocessing")
    X_train, y_train = preprocess_data(train)
    print("End Preprocessing")
    #transform data
    print("Start Transformation")
    X_train_tfidf, tfidf_vocab, tfidf_reversed_vocab, tags_counts = transform_tfidf(X_train, y_train, True)
    print("End Transformation")
    #train
    print("Start Training")
    mlb = MultiLabelBinarizer(classes=sorted(tags_counts.keys()))
    y_train = mlb.fit_transform(y_train)

    classifier_tfidf = train_classifier(X_train_tfidf, y_train)

    dump(classifier_tfidf, 'output/model_tfidf.joblib')
    print("Training done")

if __name__ == "__main__":
    main()
