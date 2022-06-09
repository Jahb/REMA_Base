"""
Transform the input to fit the tfidf model
"""
# pylint: disable= R0801
from sklearn.feature_extraction.text import TfidfVectorizer
from joblib import load, dump


def counters(y_train):
    """""
    y_train: tags of the training data
    return: tags_counts
    """
    # Dictionary of all tags from train corpus with their counts.
    tags_counts = {}

    for tags in y_train:
        for tag in tags:
            if tag in tags_counts:
                tags_counts[tag] += 1
            else:
                tags_counts[tag] = 1
    return tags_counts

def tfidf_features(x_data, training):
    """
        X_train, X_val, X_test — samples
        return TF-IDF vectorized representation of each sample and vocabulary
    """
    # Create TF-IDF vectorizer with a proper parameters choice
    # Fit the vectorizer on the train set
    # Transform the train, test, and val sets and return the result
     ####### YOUR CODE HERE #######
    if training:
        tfidf_vectorizer = TfidfVectorizer(min_df=5, max_df=0.9,
        ngram_range=(1,2), token_pattern='(\S+)')     # pylint: disable= W1401
        x_data = tfidf_vectorizer.fit_transform(x_data)
        dump(tfidf_vectorizer,'output/tfidf_vectorizer.joblib')
    else:
        tfidf_vectorizer = load('output/tfidf_vectorizer.joblib')
        x_data = tfidf_vectorizer.transform(x_data)

    return x_data, tfidf_vectorizer.vocabulary_

def transform_tfidf(x_train, y_train, training):
    """
        text: wrapper for the transformation
    """
    x_train_tfidf, tfidf_vocab = tfidf_features(x_train, training)
    tfidf_reversed_vocab = {i:word for word,i in tfidf_vocab.items()}

    tags_counts = counters(y_train)
    if training:
        dump(tags_counts, 'output/tags_counts_tfidf.joblib')

    return x_train_tfidf, tfidf_vocab, tfidf_reversed_vocab, tags_counts

if __name__ == "__main__":
    x_train, y_train = load('output/text_processing_train.joblib')
    x_val, y_val = load('output/text_processing_val.joblib')
    #x_test = load('output/text_processing_test.joblib')

    x_train_tfidf, tfidf_vocab, tfidf_reversed_vocab, tags_counts= transform_tfidf(x_train, y_train, True)
    x_val_tfidf, tfidf_vocab2, tfidf_reversed_vocab2, tags_counts2 = transform_tfidf(x_val, y_train, False)
    #x_test_tfidf, tfidf_vocab3, tfidf_reversed_vocab3, tags_counts3 = transform_tfidf(x_val, y_train, False)

    dump((x_train_tfidf, y_train), 'output/transform_tfidf_train.joblib')
    dump((x_val_tfidf, y_val), 'output/transform_tfidf_val.joblib')
    #dump(x_test_tfidf, 'output/tfidf_test.joblib')

    dump(tags_counts, 'output/tfidf_tags_counts.joblib')
    dump(tags_counts2, 'output/tfidf_tags_counts_val.joblib')
