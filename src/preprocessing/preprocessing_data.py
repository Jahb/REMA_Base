import re
import nltk
nltk.download('stopwords')
from nltk.corpus import stopwords


def preprocess_data(data):
    X_data, y_data = data['title'].values, data['tags'].values
    X_data = [text_prepare(x) for x in X_data]

    return X_data, y_data

def preprocess_data_test(data):
    X_test = data['title'].values
    X_test = [text_prepare(x) for x in X_test]

    return X_test

def text_prepare(text):
    """
        text: a string
        
        return: modified initial string
    """
    REPLACE_BY_SPACE_RE = re.compile('[/(){}\[\]\|@,;]')
    BAD_SYMBOLS_RE = re.compile('[^0-9a-z #+_]')
    STOPWORDS = set(stopwords.words('english'))

    text = text.lower() # lowercase text
    text = re.sub(REPLACE_BY_SPACE_RE, " ", text) # replace REPLACE_BY_SPACE_RE symbols by space in text
    text = re.sub(BAD_SYMBOLS_RE, "", text) # delete symbols which are in BAD_SYMBOLS_RE from text
    text = " ".join([word for word in text.split() if not word in STOPWORDS]) # delete stopwords from text
    return text