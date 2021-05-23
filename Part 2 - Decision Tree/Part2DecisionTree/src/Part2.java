import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.*;
import java.io.*;


public class Part2 {

    private List<String> categoryNames;
    private List<Instance> trainingInstances;
    private List<Instance> testInstances;
    private Node root;
    ClassCountPair baseline;


    /**
     * Constructor for part 2
     * Used to run the entire program
     */
    public Part2(){

        //Part 2.1: Basic Hepatitis dataset
        runBasicHepatitisDataset();

        //Part 2.2: Hepatitis 10-fold cross-validation
        runHepatitis10FoldCrossValidation();
    }

    /**Part 2.1: Basic Hepatitis dataset
     * This trains on the file hepatitis-training
     * and runs on the file hepatitis-test
     */
    public void runBasicHepatitisDataset(){
        List<String> trainingFileNames = new ArrayList<>();
        trainingFileNames.add("Data/hepatitis-training");
        String testFileName = "Data/hepatitis-test";
        double accuracy = run(testFileName, trainingFileNames, true);
        double baseline_accuracy = runBaseline();
        System.out.println("Accuracy (using hepatitis-training and hepatitis-test): " + accuracy*100 + "%");
        System.out.println("Baseline Accuracy (using hepatitis-training and hepatitis-test): " + baseline_accuracy*100 + "%");
    }

    /**
     * Calculates the baseline accuracy of the dataset
     * @return accuracy if we just gues the most common class
     */
    private double runBaseline() {
        double numCorrect = 0;
        for(Instance instance: this.testInstances){
            if(this.baseline.getClassType().equals(instance.getClassType())){
                numCorrect++;
            }
        }

        return numCorrect/this.testInstances.size();
    }


    /**Part 2.2: Hepatitis 10-fold cross-validation
     * This trains on the files hepatitis-training-run-x
     * and runs on the file hepatitis-test-run-x
     * for all x's 0-9
     * This is if we need only to train on the same i training data - need to ask at the helpdesk if this is right
     */
    public void runHepatitis10FoldCrossValidation(){
        System.out.println("Running the 10-fold cross validation on the Hepatitis datasets");
        List<String> trainingFileNames = new ArrayList<>();
        double accuracy = 0;
        for(int fold =0; fold<10;fold++) { //run for 10 folds
            System.out.println("Fold: " + fold);
            trainingFileNames.add("Data/hepatitis-training-run-" + fold);
            String testFileName = "Data/hepatitis-test-run-" + fold;
            double foldAccuracy = run(testFileName, trainingFileNames, false); //generate accuracy for this fold
            System.out.printf("Accuracy for fold %d: %.2f%%\n", fold, foldAccuracy*100);
            accuracy = accuracy + foldAccuracy;
        }
        accuracy = accuracy/10;
        System.out.printf("Average accuracy across the 10-folds: %.2f%%",accuracy*100);
    }


    /**
     * Main run method for the program
     * @param testFileName the file to test
     * @param trainingFileNames the file(s) to train on
     * @param printTree whether or not to print out the decision tree
     * @return accuracy on the test set
     */
    public double run(String testFileName, List<String> trainingFileNames, boolean printTree){
        this.trainingInstances = new ArrayList<>();
        boolean firstTime = true;
        for(String trainingFileName: trainingFileNames) {
            List<Instance> toAdd = readTrainingDataFile(trainingFileName, firstTime);
            this.trainingInstances.addAll(toAdd);
            if(firstTime){firstTime = false;}
        }
        if(testFileName != null) {
            this.testInstances = readTestDataFile(testFileName);
        }
        List<ClassCountPair> overallClassCounts = generateClassCounts(this.trainingInstances);
        this.baseline = calculateMostCommonClass(overallClassCounts); // most common class in the dataset

        Set<Instance> trainingInstances = new HashSet<>(this.trainingInstances);
        List<String> categoryNames = new ArrayList<>(this.categoryNames);
        this.root = buildTree(trainingInstances, categoryNames); //build the tree
        if(printTree) {
            System.out.println(this.root.toString(0));
        }

        if(testFileName != null) {
            return classify(this.testInstances); //classify all instances in the test set
        }
        else{
            return 0; //no test set so no classifying to do
        }

    }

    /**
     * Calculates the most common class from a group of instances
     * @param classCounts List of classes and their associated count
     * @return the count and class of the most common class
     */
    public ClassCountPair calculateMostCommonClass(List<ClassCountPair> classCounts) {
        List<ClassCountPair> mostCommonClasses = new ArrayList<>();
        ClassCountPair mostCommonClass;
        double mostCommon = -1;

        for(ClassCountPair classCountPair : classCounts){
            if(classCountPair.getFrequency()>mostCommon){ //if more common than most common make most common
                mostCommon=classCountPair.getFrequency();
                mostCommonClasses.clear();
                mostCommonClasses.add(classCountPair);
            }
            else if(classCountPair.getFrequency()==mostCommon){
                mostCommonClasses.add(classCountPair);
            }
        }

        if (mostCommonClasses.isEmpty()){
            throw new NullPointerException("MostCommon Classes should contain at least one class");
        }
        else {
            mostCommonClass = mostCommonClasses.get((int)(Math.random()*mostCommonClasses.size()));
        }

        return mostCommonClass;
    }

    /**
     * Generates a list saying how many instances of each class are in the instance list
     * @param instances list of instances
     * @return a list of each class with its associated count in the instance list
     */
    public List<ClassCountPair> generateClassCounts(List<Instance> instances) {
        List<ClassCountPair> classCounts = new ArrayList<>();
        for(Instance instance: instances){
            String currentClass = instance.getClassType(); //get class of current instance
            boolean exists = false;
            for(ClassCountPair classCountPair : classCounts){
                if(classCountPair.getClassType().equals(currentClass)){ //if class already in list just increment associated count
                    classCountPair.setFrequency(classCountPair.getFrequency()+1);
                    exists = true;
                }
            }
            if(!exists){
                classCounts.add(new ClassCountPair(currentClass, 1)); //otherwise add class to list with a count of 1
            }
        }
        return classCounts;
    }

    /**
     * The method used to generate an accuracy on the test set
     * @param instances - the set of instances to classify, most likely to be the test set
     * @return the accuracy of the decision tree, that is how many instance it correctly classified
     */
    public double classify(List<Instance> instances){
        double totalCorrect = 0;
        for(Instance instance: instances){
            String prediction = classifyRecursive(this.root, instance); // guess class of instance
            if(prediction.equals(instance.getClassType())){
                totalCorrect ++;
            }
        }
        System.out.printf("Classified %.0f out of %d instances correctly, an accuracy of %.2f%%\n", totalCorrect, instances.size(), 100*totalCorrect/instances.size());
        return totalCorrect/instances.size(); //this is the accuracy
    }

    /**
     * Recursively classifies an instance by looking at the current node and determining which path down the tree it needs to take
     * @param currentNode the current node to look at
     * @param currentInstance the instance we are classifying
     * @return the predicted class
     */
    public String classifyRecursive(Node currentNode, Instance currentInstance){
        if(currentNode.getAttribute() == null){ //if a leaf node
            return currentNode.getClassName();
        }
        else {
            /*
             * if the current attribute is true or false for the given instance
             * go down the relevant branch of the tree
             */
            if(currentInstance.getAtt(this.categoryNames.indexOf(currentNode.getAttribute()))){
                return classifyRecursive(currentNode.getTrueNode(), currentInstance);
            }
            else{
                return classifyRecursive(currentNode.getFalseNode(), currentInstance);
            }
        }
    }

    /**
     *
     * @param instances : the set of training instances that have been provided to the node being constructed.
     * @param remainingAttributes : the list of attributes that were not used on the path from the root to this node.
     * @return the node that belongs at the relevant spot in the tree i.e. the node with the best split
     */
    public Node buildTree(Set<Instance> instances, List<String> remainingAttributes){

        /* if instances is empty:
         * return a leaf node that contains the name and probability of the most probable
         * class across the whole training set (i.e. the ‘‘baseline’’ predictor)
         */
        if(instances.isEmpty()){
            return new Node(this.baseline.getClassType(), (double)this.baseline.getFrequency()/this.trainingInstances.size());
        }
        else {
            List<Instance> instancesList = new ArrayList<>(instances);
            ClassCountPair mostCommonClass = calculateMostCommonClass(generateClassCounts(instancesList));

            /* else if instances are pure (i.e. all belong to the same class):
             * return a leaf node that contains the name of the class and probability 1
             */
            if (mostCommonClass.getFrequency() == instancesList.size()) {
                return new Node(mostCommonClass.getClassType(), 1);
            }

            /*else if attributes is empty:
             * return a leaf node that contains the name and probability of the majority
             * class of instances (chosen randomly if classes are equal)
             */
            else if (remainingAttributes.isEmpty()) {
                return new Node(mostCommonClass.getClassType(), (double)mostCommonClass.getFrequency() / instancesList.size());
            }
            //else find best attribute:
            else {
                double bestWeightedAverageImpurity = 1;
                String bestAttribute = "";
                Set<Instance> bestInstTrue = new HashSet<>();
                Set<Instance> bestInstFalse = new HashSet<>();
                /*for each attribute:
                 * separate instances into two sets:
                 * 1) instances for which the attribute is true, and
                 * 2) instances for which the attribute is false
                 * compute impurity of each set.
                 * if weighted average impurity of these sets is best so far:
                 * bestAtt = this attribute
                 * bestInstsTrue = set of true instances
                 * bestInstsFalse = set of false instances
                 */
                for(String attribute : remainingAttributes){
                    Set<Instance> trueInstances = new HashSet<>();
                    Set<Instance> falseInstances = new HashSet<>();

                 // separate instances into two sets:
                    for(Instance instance : instances){
                        if(instance.getAtt(this.categoryNames.indexOf(attribute))){
                            trueInstances.add(instance);
                        }
                        else{
                            falseInstances.add(instance);
                        }
                    }
                    // Calculating false set impurity
                    List<Instance> falseInstancesList = new ArrayList<>(falseInstances);
                    List<ClassCountPair> falseClassCountPairs = generateClassCounts(falseInstancesList);
                    double falseImpurity = 1;
                    if(falseClassCountPairs.size()==1){
                        falseImpurity = 0;
                    }
                    for(ClassCountPair classCountPair : falseClassCountPairs){
                        falseImpurity=falseImpurity*((double)classCountPair.getFrequency()/falseInstancesList.size());
                    }
                    double falseWeightedImpurity = ((double)falseInstancesList.size()/instancesList.size())*falseImpurity;


                    // Calculating true set impurity
                    List<Instance> trueInstancesList = new ArrayList<>(trueInstances);
                    List<ClassCountPair> trueClassCountPairs = generateClassCounts(trueInstancesList);
                    double trueImpurity = 1;
                    if(trueClassCountPairs.size()==1){
                        trueImpurity = 0;
                    }
                    for(ClassCountPair classCountPair : trueClassCountPairs){

                        trueImpurity=trueImpurity*((double)classCountPair.getFrequency()/trueInstancesList.size());
                    }
                    double trueWeightedImpurity = ((double)trueInstancesList.size()/instancesList.size())*trueImpurity;

                    // calculate average weighted impurity
                    double weightedAverageImpurity = trueWeightedImpurity+falseWeightedImpurity;

                    //determine if this is the best split
                    if(weightedAverageImpurity<bestWeightedAverageImpurity){
                        bestAttribute = attribute;
                        bestInstTrue = trueInstances;
                        bestInstFalse = falseInstances;
                        bestWeightedAverageImpurity = weightedAverageImpurity;
                    }

                }

                //Build the next layer of the tree
                remainingAttributes.remove(bestAttribute);
                Node trueNode = buildTree(bestInstTrue, remainingAttributes);
                Node falseNode = buildTree(bestInstFalse, remainingAttributes);
                return new Node(trueNode, falseNode, bestAttribute);
            }
        }
    }


    /**
     *  Load the training instances
     * @param fname training file name
     * @param firstFile whether this is the first file
     * @return List of training instances
     */
    public List<Instance> readTrainingDataFile(String fname, boolean firstFile){

        /* format of names file:
         * names of categories, separated by spaces
         * names of attributes
         * category followed by true's and false's for each instance
         */
        //System.out.println("Reading data from file "+fname);

        List<Instance> trainingInstances;
        try {
            Scanner din = new Scanner(new File(fname));
            if(firstFile) {
                this.categoryNames = new ArrayList<>();
                for (Scanner s = new Scanner(din.nextLine()); s.hasNext(); ) this.categoryNames.add(s.next());
                this.categoryNames.remove("Class");
                int numCategories = this.categoryNames.size();
                //System.out.println(numCategories + " categories");
            }
            else{
                din.nextLine();
            }
            trainingInstances = readInstances(din);
            din.close();
        }
        catch (IOException e) {
            throw new RuntimeException("Data File caused IO exception!");
        }
        return trainingInstances;
    }

    /**
     *  Load the training instances
     * @param fname training file name
     * @return List of training instances
     */
    public List<Instance> readTestDataFile(String fname){

        /* format of names file:
         * names of categories, separated by spaces
         * names of attributes
         * category followed by true's and false's for each instance
         */


        // System.out.println("Reading data from file "+fname);
        List<Instance> testInstances;
        try {
            Scanner din = new Scanner(new File(fname));
            din.nextLine();
            testInstances = readInstances(din);
            din.close();
        }
        catch (IOException e) {
            throw new RuntimeException("Data File caused IO exception");
        }
        return testInstances;
    }

    public List<Instance> readInstances(Scanner din){
        /* instance = classname and space separated attribute values */
        List<Instance> instances = new ArrayList<>();
        while (din.hasNext()){
            Scanner line = new Scanner(din.nextLine());
            instances.add(new Instance(line.next(),line));
        }
        // System.out.println("Read " + instances.size()+" instances");
        return instances;
    }

    /**
     * Class to hold a classtype and the associated count
     */
    private class ClassCountPair {
        private final String classType;
        private int frequency;


        private ClassCountPair(String classType, int frequency) {
            this.classType = classType;
            this.frequency = frequency;
        }

        public String getClassType() {
            return classType;
        }

        public int getFrequency() {
            return frequency;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }

        public String toString(){
            return ("Class: " + this.classType + ", Frequency: " + this.frequency);
        }
    }

    private class Instance {

        private final String category;
        private final List<Boolean> vals;

        public Instance(String cat, Scanner s){
            if(s == null ){throw new NullPointerException("Null scanner passed into Instance class constructor");}
            if(cat == null ){throw new NullPointerException("Null category passed into Instance class constructor");}
            category = cat;
            vals = new ArrayList<>();
            while (s.hasNextBoolean()) vals.add(s.nextBoolean());
        }

        public boolean getAtt(int index){
            return vals.get(index);
        }

        public String getClassType(){
            return category;
        }

        public String toString(){
            StringBuilder ans = new StringBuilder(category);
            ans.append(" ");
            for (Boolean val : vals)
                ans.append(val?"true ":"false ");
            return ans.toString();
        }

    }

    public static void main(String[] args) {

        new Part2();
    }



}
