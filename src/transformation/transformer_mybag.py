"""
Transform the input to fit the mybag model
"""
# pylint: disable= R0801
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
    #Disable below two dumps for dvc
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

def transform_mybag_dvc(x_train, x_val, y_train):
    """
    Transform functions merged into one function.
    Mainly used for DVC pipeline.
    """
    # pylint: disable= C0103
    tags_counts, words_counts = counters(x_train, y_train)
    DICT_SIZE = 5000
    INDEX_TO_WORDS = sorted(words_counts, key=words_counts.get,
    reverse=True)[:DICT_SIZE]
    WORDS_TO_INDEX = {word:i for i, word in enumerate(INDEX_TO_WORDS)}

    x_train_mybag = sp_sparse.vstack([sp_sparse.csr_matrix(my_bag_of_words(text,
    WORDS_TO_INDEX, DICT_SIZE)) for text in x_train])
    x_val_mybag = sp_sparse.vstack(
        [sp_sparse.csr_matrix(my_bag_of_words(text, WORDS_TO_INDEX, DICT_SIZE)) for text in x_val])

    return x_train_mybag, x_val_mybag, tags_counts, words_counts

if __name__ == "__main__":
    x_tr, y_tr = load('output/text_processing_train.joblib')
    x_vali, y_vali = load('output/text_processing_val.joblib')

    x_train_mybag_transform, x_val_mybag_transform, tags_counts_tr, words_counts_tr \
        = transform_mybag_dvc(x_tr, x_vali, y_tr)

    dump((x_train_mybag_transform, y_tr), 'output/transform_mybag_train.joblib')
    dump((x_val_mybag_transform, y_vali), 'output/transform_mybag_val.joblib')
    dump(tags_counts_tr, 'output/mybag_tags_counts.joblib')
    dump(words_counts_tr, 'output/mybag_words_counts_val.joblib')
