"""
Read in Data and construct dataset
"""

from ast import literal_eval
import pandas as pd

def read_data(filename):
    """
    Read data from the following path
    """
    data = pd.read_csv(filename, sep='\t')
    data['tags'] = data['tags'].apply(literal_eval)
    return data

def train_test_split():
    """
    Split data into three datasets
    """
    train = read_data('data/train.tsv')
    validation = read_data('data/validation.tsv')
    test = pd.read_csv('data/test.tsv', sep='\t')
    return train, validation, test
