from src.preprocessing.text_processing import text_prepare
import unittest
import numpy as np

class TestTextProcessing(unittest.TestCase):
    def test_text_prepare_symbols(self):
        example = "SQL Server - any equivalent of Excel's CHOOSE function?"
        answer = "sql server equivalent excels choose function"
        np.testing.assert_equal(text_prepare(example), answer)
        
    def test_text_prepare_stop_word(self):
        example = "How to free c++ memory vector<int> * arr?"
        answer = "free c++ memory vectorint arr"
        np.testing.assert_equal(text_prepare(example), answer)

    def test_text_prepare_all_symbols(self):
        example = "-------------------------------------------------------"
        answer = ""
        np.testing.assert_equal(text_prepare(example), answer)


if __name__ == '__main__':
    unittest.main()