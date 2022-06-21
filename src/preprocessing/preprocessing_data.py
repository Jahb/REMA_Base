"""
    Preprocess the data
"""
# pylint: disable= R0801
from ast import literal_eval
import re
from nltk.corpus import stopwords
import nltk
import pandas as pd
from joblib import dump

nltk.download('stopwords')

def read_data(filename):
    """
      filename — name of the tsv file
      return: panda dataframe
    """

    data = pd.read_csv(filename, sep='\t')
    data['tags'] = data['tags'].apply(literal_eval)
    return data

def preprocess_data(data):
    """
        Apply text_prepare() to all titles in the data file
        Includes labeled data
    """
    x_data, y_data = data['title'].values, data['tags'].values
    x_data = [text_prepare(x) for x in x_data]

    return x_data, y_data

def preprocess_data_test(data):
    """
        Apply text_prepare() to all titles in the data file
        Includes unlabeled/test data
    """
    x_test = data['title'].values
    x_test = [text_prepare(x) for x in x_test]

    return x_test

def text_prepare(text):
    """
        text: a string
        return: modified initial string
    """
    # pylint: disable= C0103
    REPLACE_BY_SPACE_RE = re.compile('[/(){}\[\]\|@,;]') # pylint: disable= W1401
    BAD_SYMBOLS_RE = re.compile('[^0-9a-z #+_]')
    STOPWORDS = set(stopwords.words('english'))

    text = text.lower() # lowercase text
    # replace REPLACE_BY_SPACE_RE symbols by space in text
    text = re.sub(REPLACE_BY_SPACE_RE, " ", text)
    # delete symbols which are in BAD_SYMBOLS_RE from text
    text = re.sub(BAD_SYMBOLS_RE, "", text)
    # delete stopwords from text
    text = " ".join([word for word in text.split() if not word in STOPWORDS])
    return text

if __name__ == "__main__":
    train = read_data('data/train.tsv')
    validation = read_data('data/validation.tsv')
    test = pd.read_csv('data/test.tsv', sep='\t')

    x_train, y_train = preprocess_data(train)
    x_val, y_val = preprocess_data(validation)
    x_tst = preprocess_data_test(test)

    dump((x_train, y_train), 'output/text_processing_train.joblib')
    dump((x_val, y_val), 'output/text_processing_val.joblib')
    dump(x_tst, 'output/text_processing_test.joblib')
