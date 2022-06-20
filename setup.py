# -*- coding: utf-8 -*-
# For setting up the project structure following github has been referred.
# https://github.com/navdeep-G/samplemod

# Learn more: https://github.com/kennethreitz/setup.py

from setuptools import setup, find_packages


with open('README.rst') as f:
    readme = f.read()

with open('LICENSE') as f:
    license = f.read()

setup(
    name='src',
    version='0.1.0',
    description='REMLA Group 6',
    long_description=readme,
    license=license,
    packages=find_packages(exclude=('tests', 'docs'))
)

