"""
Retrieve data
"""
import re
from ast import literal_eval
import nltk
from nltk.corpus import stopwords
import pandas as pd
from joblib import dump

nltk.download('stopwords')
# pylint: disable=anomalous-backslash-in-string
REPLACE_BY_SPACE_RE = re.compile('[/(){}\[\]\|@,;]')
BAD_SYMBOLS_RE = re.compile('[^0-9a-z #+_]')
STOPWORDS = set(stopwords.words('english'))

def read_data(filename):
    """
    Read in the data from the filepath
    """
    data = pd.read_csv(filename, sep='\t')
    data['tags'] = data['tags'].apply(literal_eval)
    return data

def train_test_split():
    """
    Return train, validation, test dataset
    """
    train_data = read_data('data/train.tsv')
    validation_data = read_data('data/validation.tsv')
    test_data = pd.read_csv('data/test.tsv', sep='\t')
    return train_data, validation_data, test_data

def text_prepare(text):
    """
        text: a string
        return: modified initial string
    """
    # lowercase text
    text = text.lower()
    # replace REPLACE_BY_SPACE_RE symbols by space in text
    text = re.sub(REPLACE_BY_SPACE_RE, " ", text)
    # delete symbols which are in BAD_SYMBOLS_RE from text
    text = re.sub(BAD_SYMBOLS_RE, "", text)
    # delete stopwords from text
    text = " ".join([word for word in text.split() if not word in STOPWORDS])
    return text

# def test_text_prepare():
#     examples = ["SQL Server - any equivalent of Excel's CHOOSE function?",
#                 "How to free c++ memory vector<int> * arr?"]
#     answers = ["sql server equivalent excels choose function",
#                "free c++ memory vectorint arr"]
#     for ex, ans in zip(examples, answers):
#         if text_prepare(ex) != ans:
#             return "Wrong answer for the case: '%s'" % ex
#     return 'Basic tests are passed.'

# pylint: disable=C0103
if __name__ == "__main__":
    print("Starting with the preprocessing")
    train = read_data('data/train.tsv')
    validation = read_data('data/validation.tsv')
    test = pd.read_csv('data/test.tsv', sep='\t')

    X_train, y_train = train['title'].values, train['tags'].values
    X_val, y_val = validation['title'].values, validation['tags'].values
    X_test = test['title'].values

    prepared_questions = []
    with open('data/text_prepare_tests.tsv', encoding='utf-8') as prepare_file:
        for line in prepare_file:
            line = text_prepare(line.strip())
            prepared_questions.append(line)
    text_prepare_results = '\n'.join(prepared_questions)

    X_train = [text_prepare(x) for x in X_train]
    X_val = [text_prepare(x) for x in X_val]
    X_test = [text_prepare(x) for x in X_test]

    dump((X_train, y_train), 'output/text_processing_train.joblib')
    dump((X_val, y_val), 'output/text_processing_val.joblib')
    dump(X_test, 'output/text_processing_test.joblib')
    print("Preprocessing done")