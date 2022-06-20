"""
Analyze the model
"""

def print_words_for_tag(classifier, tag, tags_classes, index_to_words):
    """
        classifier: trained classifier
        tag: particular tag
        tags_classes: a list of classes names from MultiLabelBinarizer
        index_to_words: index_to_words transformation
        all_words: all words in the dictionary

        return nothing, just print top 5 positive and top 5 negative words for current tag
    """
    print(f'Tag:\t{tag}')

    # Extract an estimator from the classifier for the given tag.
    # Extract feature coefficients from the estimator.

    model = classifier.estimators_[tags_classes.index(tag)]
    top_positive_words = [index_to_words[x] for x in model.coef_.argsort().tolist()[0][-5:]]
    top_negative_words = [index_to_words[x] for x in model.coef_.argsort().tolist()[0][:5]]

    # pylint: disable=consider-using-f-string
    print('Top positive words:\t{}'.format(', '.join(top_positive_words)))
    print('Top negative words:\t{}\n'.format(', '.join(top_negative_words)))
