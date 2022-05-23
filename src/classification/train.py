"""
Train the model
"""

from sklearn.multiclass import OneVsRestClassifier
from sklearn.linear_model import LogisticRegression


def train_classifier(x_train, y_train, penalty='l1', C=1):
    """
      X_train, y_train — training data

      return: trained classifier
    """

    # Create and fit LogisticRegression wraped into OneVsRestClassifier.

    clf = LogisticRegression(penalty=penalty, C=C, dual=False, solver='liblinear')
    clf = OneVsRestClassifier(clf)
    clf.fit(x_train, y_train)

    return clf
