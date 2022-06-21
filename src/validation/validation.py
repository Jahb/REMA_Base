"""
Perform basic data validation using TFDV
"""

# pylint: disable= E0401
import tensorflow_data_validation as tfdv
import pandas as pd
# from sklearn.model_selection import train_test_split


train = pd.read_csv('./data/train.tsv', sep='\t')
validation = pd.read_csv('./data/validation.tsv', sep='\t')
test = pd.read_csv('./data/test.tsv', sep='\t')

print(train.head)
print(train.describe)

print('Train shape')
print(train.shape)
print('Test shape')
print(test.shape)

#stats
train_stats = tfdv.generate_statistics_from_dataframe(train)
tfdv.visualize_statistics(train_stats)

#schema
schema = tfdv.infer_schema(train_stats)
tfdv.display_schema(schema)

test_stats = tfdv.generate_statistics_from_dataframe(test)
anomalies = tfdv.validate_statistics(test_stats, schema=schema)
tfdv.display_anomalies(anomalies)
