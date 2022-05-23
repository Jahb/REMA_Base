"""
Train the model
"""
from joblib import dump, load
from sklearn.multiclass import OneVsRestClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.preprocessing import MultiLabelBinarizer

def train_classifier(x_data, y_data, penalty='l1', cof=1):
    """
      X_train, y_train — training data

      return: trained classifier
    """

    # Create and fit LogisticRegression wraped into OneVsRestClassifier.
    clf = LogisticRegression(penalty=penalty, C=cof, dual=False, solver='liblinear')
    clf = OneVsRestClassifier(clf)
    clf.fit(x_data, y_data)
    return clf

if __name__ == "__main__":
    tags_counts = load('output/tags_counts.joblib')
    X_train_tfidf, y_train = load('output/tfidf_train.joblib')
    X_val_tfidf, y_val = load('output/tfidf_val.joblib')
    X_test_tfidf = load('output/tfidf_test.joblib')

    mlb = MultiLabelBinarizer(classes=sorted(tags_counts.keys()))
    y_train = mlb.fit_transform(y_train)
    y_val = mlb.fit_transform(y_val)

    classifier_tfidf = train_classifier(X_train_tfidf, y_train, penalty='l2', cof=10)
    y_val_predicted_labels_tfidf = classifier_tfidf.predict(X_val_tfidf)
    y_val_predicted_scores_tfidf = classifier_tfidf.decision_function(X_val_tfidf)

    test_predictions = classifier_tfidf.predict(X_test_tfidf)######### YOUR CODE HERE #############
    test_pred_inversed = mlb.inverse_transform(test_predictions)

    dump(y_val_predicted_scores_tfidf, 'output/y_val_predicted_scores_tfidf.joblib')
    dump(y_val_predicted_labels_tfidf, 'output/y_val_predicted_scores_labels.joblib')
    dump(test_predictions, 'output/test_predictions_tfidf.joblib')
    dump(test_pred_inversed, 'output/test_pred_inversed.joblib')
