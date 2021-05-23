Course COMP307 - Assignment 1: Basic Machine Learning Algorithms
Credit: Andrew Lensen (andrew.lensen.vuw.ac.nz) (for helper code) and Daniel Marshall (dmarshalldeveloper@gmail.com)

Running the code

- Each individual part of the program has it's own README.txt to tell you how to run that specific program,
  but I will discuss some common points here

  1. When running the given commands for each program, start off by navigating the console to the folder which
     the ENTIRE project is located in. This is most likely the DOWNLOADS folder.
  
  2. The first part of the program, kNN, I have coded in python, but parts 2 and 3, the decision tree and the perceptron, I have coded in Java
     This should not affect anything as each part is entirely separate from the others
  
  3. The assignment asks that each program accepts the file names as arguments
     I have implemented this for part 1, but for part 2 it seemed impractical to me, especially with the k-fold cross validation,
     having to pass around 22 files in, so I have not implemented it for this part 2 or part 3. They automatically use the correct files, and the files
     can be changed by just editing the load methods in both of the parts, however this is not recommended.



About this directory
This directory contains data and code/executables to run all parts of this assignment
The directory structure is as follows
 
.
├── Part 1 - kNN                                                                               # Part 1 Directory
|   ├── Data                                                                                  # Part 1 Datasets
|   |   ├── wine
|   |   ├── wine-training
|   |   └── wine-test
|   ├── Part1.py                                                                              # Part 1 Code
|   ├── README.txt                                                                            #Instructions for running Part 1
|   └── sampleoutput.txt                                                                      #Sample output for Part 1
├── Part 2 - Decision Tree
|   ├── Part2DecisionTree                                                                     # Part 2 Code
|   |   └── All the files associated with the code, including the dataset and the jar
|   ├── README.txt                                                                            #Instructions for running Part 2
|   └── sampleoutput.txt                                                                      #Sample output for Part 2
├── Part 3 - Perceptron
|   ├── Part3Perceptron                                                                       # Part 3 code
|   |   └── All the files associated with the code, including the dataset and the jar
|   ├── README.txt                                                                            #Instructions for running Part 3
|   └── sampleoutput.txt                                                                      #Sample output for Part 3
├── Assignment 1 Report.pdf                                                                     # Report for the Assignment
└── README.md                                                                                 #Instructions



