# Load General Libraries
import math
import random

import numpy as np
import pandas as pd
import sys


def classify(test_inst, k=1):
    # This function classifies a single test instance using the training instances

    distances = dict()
    for l, lrow in df_training.iterrows():
        d = distance(lrow, test_inst)  # calculate the distance
        distances[d] = lrow["Class"]  # Save it to the dictionary, with the class of this point

    # Sort the points in order of euclidean distance from the test point, shortest to longest
    distances_sorted = {}
    sorted_keys = sorted(distances.keys())
    for w in sorted_keys:
        distances_sorted[w] = distances[w]

    classes = dict()  # this contains a list of all the classes that appear in the closest k neighbours, and how many
    # times each class appears

    for x in range(k):  # add all k nearby classes to the dict
        instance_class = list(distances_sorted.values())[x]
        if instance_class in classes:
            classes[instance_class] = classes[instance_class] + 1
        else:
            classes[instance_class] = 1

    # Sort the classes by their frequency, so that the most common class is first
    classes_sorted = {}
    sorted_keys = sorted(classes, key=classes.get, reverse=True)
    for w in sorted_keys:
        classes_sorted[w] = classes[w]

    classes_sorted_keys_list = list(classes_sorted)
    classes_sorted_values_list = list(classes_sorted.values())

    # If one most common class assign it, otherwise add all classes to a list and pick randomly from it
    if (len(classes_sorted_keys_list) == 1) or (classes_sorted_values_list[0] > classes_sorted_values_list[1]):
        test_class = classes_sorted_keys_list[0]  # assign the test instance the most common class in the k closest
        # instances
    else:
        top_classes = []
        for x in range(len(classes_sorted_keys_list)):
            if classes_sorted_values_list[x] == classes_sorted_values_list[0]:
                top_classes.append(classes_sorted_keys_list[x])
        test_class = random.choice(top_classes)  # assign the test instance a random class from the top ones

    return test_class


def normalise(df):
    # This function normalises the input to make sure that every feature has an even weight
    # This is done instead of dividing by the range when classifying
    normalised_df = df.copy()
    for label, feature in df.iteritems():
        if "Class" not in label:
            max_value = df[label].max()
            min_value = df[label].min()
            normalised_df[label] = (df[label] - min_value) / (max_value - min_value)
    return normalised_df
    # adapted from https://stackoverflow.com/questions/26414913/normalize-columns-of-pandas-data-frame


def distance(instance_1, instance_2):
    dist = 0

    # Add all distances together. Note we do not divide by range as we have normalised the data already
    for x in range(instance_1.size - 1):
        dist += math.pow((instance_1.iloc[x] - instance_2.iloc[x]), 2)  # Euclidean distance measure

    dist = math.sqrt(dist)
    return dist


if __name__ == '__main__':
    arguments = sys.argv[1:]
    # Load the data
    df_training = pd.read_csv(arguments[0], sep=" ")
    df_test = pd.read_csv(arguments[1], sep=" ")

    # Normalise the data
    num_rows = df_training.shape[0]
    df_training = normalise(df_training)
    df_test = normalise(df_test)

    # Classify the test instances
    k = 1
    correct_classifications = 0
    guesses = np.empty(
        shape=[df_test.shape[0], 2])
    for i, row in df_test.iterrows():
        guesses[i][0] = classify(row, k)
        guesses[i][1] = row["Class"]
        if guesses[i][0] == guesses[i][1]:  # if correct classification
            correct_classifications = correct_classifications + 1

    # Calculate accuracy and print information
    accuracy = 100 * (correct_classifications / df_test.shape[0])
    print(str(correct_classifications) + "/" + str(df_test.shape[0]) + " instances classified correctly")
    print("Accuracy: " + str(round(accuracy, 2)) + "%")
    print("Predicted, Actual")
    print(guesses)
