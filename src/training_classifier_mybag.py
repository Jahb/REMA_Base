"""
Train the mybag model
"""

from ast import literal_eval
import pandas as pd
from joblib import dump
from sklearn.preprocessing import MultiLabelBinarizer
from src.classification.train import train_classifier
from src.preprocessing.preprocessing_data import preprocess_data
from src.transformation.transformer_mybag import transform_mybag_training


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
      return: trained mybag classifier
    """

    ## data being used
    train = read_data('data/train.tsv')
    #preprocess data
    print("Start Preprocessing")
    x_train, y_train = preprocess_data(train)
    print("End Preprocessing")
    #transform data
    print("Start Transformation")
    x_train_mybag, tags_counts = transform_mybag_training(x_train, y_train)
    print("End Transformation")
    # train
    print("Start Training")
    mlb = MultiLabelBinarizer(classes=sorted(tags_counts.keys()))
    y_train = mlb.fit_transform(y_train)

    classifier_mybag = train_classifier(x_train_mybag, y_train)
    dump(mlb, 'output/mlb_mybag.joblib')
    dump(classifier_mybag, 'output/model_mybag.joblib')
    print("End Training")

if __name__ == "__main__":
    main()
