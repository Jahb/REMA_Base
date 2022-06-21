import unittest
import numpy as np

from src.transformation.transformer_mybag import my_bag_of_words

class TestTextTransform(unittest.TestCase):
    def test_my_bag_of_words(self):
        words_to_index = {'hi': 0, 'you': 1, 'me': 2, 'are': 3}
        example = 'hi how are you'
        answer = [1, 1, 0, 1]
        np.testing.assert_equal(my_bag_of_words(example, words_to_index, 4), answer)
    
    def test_my_bag_of_words_empty(self):
        words_to_index = {'hi': 0, 'you': 1, 'me': 2, 'are': 3}
        example = ''
        answer = [0, 0, 0, 0]
        np.testing.assert_equal(my_bag_of_words(example, words_to_index, 4), answer)
    
    def test_my_bag_of_words_bad_words(self):
        words_to_index = {'hi': 0, 'you': 1, 'me': 2, 'are': 3}
        example = 'We Are Delft Students'
        answer = [0, 0, 0, 0]
        np.testing.assert_equal(my_bag_of_words(example, words_to_index, 4), answer)
    
    def test_my_bag_of_words_stacked_words(self):
        words_to_index = {'hi': 0, 'you': 1, 'me': 2, 'are': 3}
        example = 'hi hi hi me me are are are are'
        answer = [3, 0, 2, 4]
        np.testing.assert_equal(my_bag_of_words(example, words_to_index, 4), answer)
        
if __name__ == '__main__':
    unittest.main()
