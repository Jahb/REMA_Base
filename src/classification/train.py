"""
  Contains the training function
"""
from sklearn.multiclass import OneVsRestClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.preprocessing import MultiLabelBinarizer
from joblib import dump, load


def train_classifier(x_train, y_train, penalty='l1', cof=1):
    """
      X_train, y_train — training data
      return: trained classifier
    """
    # Create and fit LogisticRegression wraped into OneVsRestClassifier.
    # pylint: disable= C0103
    clf = LogisticRegression(penalty=penalty, C=cof, dual=False, solver='liblinear')
    clf = OneVsRestClassifier(clf)
    clf.fit(x_train, y_train)
    return clf

if __name__ == "__main__":
    tags_counts_mybag = load('output/mybag_tags_counts.joblib')
    x_train_mybag, y_tr = load('output/transform_mybag_train.joblib')
    mlb_mybag = MultiLabelBinarizer(classes=sorted(tags_counts_mybag.keys()))
    y_tr = mlb_mybag.fit_transform(y_tr)
    classifier_mybag = train_classifier(x_train_mybag, y_tr, penalty='l2', cof=10)
    dump(mlb_mybag, 'output/mlb_mybag_dvc.joblib')
    dump(classifier_mybag, 'output/model_mybag_dvc.joblib')

    tags_counts_tfidf = load('output/tfidf_tags_counts.joblib')
    x_train_tfidf, y_tr = load('output/transform_tfidf_train.joblib')
    mlb_tfidf = MultiLabelBinarizer(classes=sorted(tags_counts_tfidf.keys()))
    y_tr = mlb_tfidf.fit_transform(y_tr)
    classifier_tfidf = train_classifier(x_train_tfidf, y_tr, penalty='l2', cof=10)
    dump(mlb_tfidf, 'output/mlb_tfidf_dvc.joblib')
    dump(classifier_tfidf, 'output/model_tfidf_dvc.joblib')
