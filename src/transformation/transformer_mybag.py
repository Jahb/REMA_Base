"""
Transform the input to fit the mybag model
"""
from scipy import sparse as sp_sparse
import numpy as np
from joblib import dump, load

def counters(x_train, y_train):
    """
    x_train: title of the training data
    y_train: tags of the training data
    return: tags_counts, words_counts
    """
    # Dictionary of all tags from train corpus with their counts.
    tags_counts = {}
    # Dictionary of all words from train corpus with their counts.
    words_counts = {}

    for sentence in x_train:
        for word in sentence.split():
            if word in words_counts:
                words_counts[word] += 1
            else:
                words_counts[word] = 1

    for tags in y_train:
        for tag in tags:
            if tag in tags_counts:
                tags_counts[tag] += 1
            else:
                tags_counts[tag] = 1
    return tags_counts, words_counts

def my_bag_of_words(text, words_to_index, dict_size):
    """
        text: a string
        dict_size: size of the dictionary
        return a vector which is a bag-of-words representation of 'text'
    """
    result_vector = np.zeros(dict_size)

    for word in text.split():
        if word in words_to_index:
            result_vector[words_to_index[word]] += 1
    return result_vector

def transform_mybag_training(x_data, y_data):
    """
        text: wrapper for the transformation used during training
    """
    # pylint: disable= C0103
    tags_counts, words_counts = counters(x_data, y_data)
    dump(tags_counts, 'output/tags_counts.joblib')
    dump(words_counts, 'output/words_counts.joblib')
    DICT_SIZE = 5000
    INDEX_TO_WORDS = sorted(words_counts, key=words_counts.get,
    reverse=True)[:DICT_SIZE]
    WORDS_TO_INDEX = {word:i for i, word in enumerate(INDEX_TO_WORDS)}

    x_train_mybag = sp_sparse.vstack([sp_sparse.csr_matrix(my_bag_of_words(text,
    WORDS_TO_INDEX, DICT_SIZE)) for text in x_data])

    return x_train_mybag, tags_counts

def transform_mybag_eval(x_data):
    """
        text: wrapper for the transformation used during evalution and serving
    """
    # pylint: disable= C0103
    tags_counts = load('output/tags_counts.joblib')
    words_counts = load('output/words_counts.joblib')
    DICT_SIZE = 5000
    INDEX_TO_WORDS = sorted(words_counts, key=words_counts.get,
    reverse=True)[:DICT_SIZE]
    WORDS_TO_INDEX = {word:i for i, word in enumerate(INDEX_TO_WORDS)}

    x_data = sp_sparse.vstack([sp_sparse.csr_matrix(my_bag_of_words(text,
     WORDS_TO_INDEX, DICT_SIZE)) for text in x_data])

    return x_data, tags_counts
