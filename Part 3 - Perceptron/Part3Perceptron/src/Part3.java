import java.io.File;
import java.io.IOException;
import java.util.*;

public class Part3 {

    private List<Image> images;
    private double dummyFeatureWeight;
    private double dummyFeature;
    private List<Feature> features;

    /**
     * Constructor for the part3 object - calls the run function
     */
    public Part3(){
        run(true);
        classifyTestSet(true);
        runMultiple();
        runMultipleTestClassifications();
    }


    public void runMultipleTestClassifications(){
        double minimumAccuracy=100;
        double maximumAccuracy = 0;
        double averageAccuracy=0;
        int runTimes = 200;
        for(int i=0;i<runTimes;i++){
            double accuracy = classifyTestSet(false);

                if(accuracy>maximumAccuracy){
                    maximumAccuracy= accuracy;
                }
                if(accuracy<minimumAccuracy){
                    minimumAccuracy= accuracy;
                }
            averageAccuracy += accuracy;

        }
        averageAccuracy = averageAccuracy/200;
        System.out.println();
        System.out.println("200 runs of the test dataset classification");
        System.out.println();
        System.out.printf("Average accuracy on the test set: %.0f%%\n", averageAccuracy);
        System.out.printf("Lowest accuracy on the test set: %.0f%%\n", minimumAccuracy);
        System.out.printf("Highest Accuracy on the test set: %.0f%%\n", maximumAccuracy);
        System.out.println();
    }


    /**
     * Runs the program multiple times to test the range of convergence epochs and other performance metrics
     */
    public void runMultiple(){
        int numberAllCorrect=0;
        double earliestConvergenceEpoch=100;
        double lastestConvergenceEpoch = 0;
        double averageEpoch=0;
        int runTimes = 200;
        for(int i=0;i<runTimes;i++){
            long epochAndCorrect = run(false);
            if(!(epochAndCorrect/1000000000 == 100)){

                long epoch = epochAndCorrect/1000000000;
                if(epoch>lastestConvergenceEpoch){
                    lastestConvergenceEpoch= epoch;
                }
                if(epoch<earliestConvergenceEpoch){
                    earliestConvergenceEpoch= epoch;
                }
                numberAllCorrect++;
                averageEpoch += epoch;
            }
        }
        averageEpoch = averageEpoch/200;
        System.out.println();
        System.out.println("Running the program 200 times (without test set)");
        System.out.printf("\nNumber of runs where convergence was reached: %d out of %d\n", numberAllCorrect, runTimes);
        System.out.printf("Average convergence epoch for runs which converged before 100 epochs: %.0f\n", averageEpoch);
        System.out.printf("Earliest convergence epoch for runs which converged before 100 epochs: %.0f\n", earliestConvergenceEpoch);
        System.out.printf("Latest convergence epoch for runs which converged before 100 epochs: %.0f\n", lastestConvergenceEpoch);
        System.out.println();
    }

    /**
     * Method to classify a test set
     * @return accuracy on the test set
     */
    public double classifyTestSet(boolean printOutput){
        run(false);
        String fileName = "Data/test-images.data";
        List<Image> testImages = this.images = load(fileName, false); //load the image data
        assignImageFeatureValues(this.images, this.features); //assign the feature values
        double numCorrect = 0;
        for (Image image: testImages) {
            String prediction = classify(image);
            String category = image.getCategory();
            if (prediction.equals(category)) {
                numCorrect++;
            }
        }
        if(printOutput) {
            System.out.println();
            System.out.println("Single run of the test dataset classification");
            System.out.println();
            System.out.println("Images classified correctly: " + numCorrect + "/" + testImages.size());
            System.out.printf("Accuracy on the test set: %.2f%%", (numCorrect / testImages.size()) * 100);
            System.out.println();
            System.out.println();
        }
        return (numCorrect/testImages.size())*100;
    }

    /**
     * Main method in the program - it loads images,
     * generates the features then learns the weights
     * @return value conveying epoch and num correctly classified
     */
    public long run(boolean printOutput){
        String fileName = "Data/image.data";
        this.images = load(fileName, printOutput); //load the image data
        int seed = 5;
        Random randomShuffle = new Random();
        Collections.shuffle(this.images, randomShuffle); //shuffle the images so we train randomly

        this.features = createFeatures(0, 50, false);
        this.dummyFeatureWeight=0;
        assignImageFeatureValues(this.images, features); //assign the feature values

        long returnValue = learn(this.images, printOutput); //learn the perceptron
        if(printOutput){ printFeaturesAndWeights(features);}
        return returnValue;
    }

    /**
     * Prints out all of the features and their associated weights
     * @param features list of features
     */
    private void printFeaturesAndWeights(List<Feature> features) {
        int i = 1;
        for(Feature feature: features){
            System.out.println("Feature " + i);
            System.out.println(feature.finalOutput());
            i++;
        }
    }

    /**
     * This is the main method in which the perceptron trains on the dataset
     * @param imageFeatureValues the list of feature values for each image, for all images
     * @return number of correctly classified instances
     */
    public long learn(List<Image> imageFeatureValues, boolean printOutput) {
        boolean allCorrect = false;
        long numCorrect = 0;
        long epoch = 0;
        while(!allCorrect && epoch < 100) {
            if(printOutput){System.out.println("Epoch " + (epoch+1));}
            numCorrect=0;
            allCorrect = true;
            for (Image image: images) {
                String prediction = classify(image);
                String category = image.getCategory();
                if (!prediction.equals(category)) {
                    //incorrect guess
                    allCorrect=false;

                    if (category.equals("X")) {
                        //Else if +ve example and wrong:
                        //(i.e. weights on active features are too low)
                        //Add feature vector to weight vector for all features
                        int i = 0;
                        for(Map.Entry<Feature, Integer> featureValue: image.getFeatureValues().entrySet()){
                            double newWeight = featureValue.getKey().getWeight() + featureValue.getValue();
                            featureValue.getKey().setWeight(newWeight);
                            i++;
                        }
                        this.dummyFeatureWeight = this.dummyFeatureWeight+1;

                    } else if (category.equals("O")) {
                        //else if -ve example and wrong:
                        //(i.e. weights on active features are too high)
                        //Subtract feature vector from weight vector
                        int i = 0;
                        //for features associated
                        for(Map.Entry<Feature, Integer> featureValue: image.getFeatureValues().entrySet()){
                            double newWeight = featureValue.getKey().getWeight() - featureValue.getValue();
                            featureValue.getKey().setWeight(newWeight);
                            i++;
                        }
                        this.dummyFeatureWeight = this.dummyFeatureWeight-1;


                    } else {
                        throw new IllegalStateException("In learn method: entry has invalid category");
                    }
                }
                else{
                    numCorrect++;
                }
            }
            epoch ++;
            if(printOutput){System.out.println("Correct: "+ numCorrect + " out of " + imageFeatureValues.size());}
        }
        if(printOutput) {
            if (allCorrect) {
                System.out.println("\nNumber of epochs until convergence: " + epoch + "\n");
            } else {
                System.out.println("\nNumber of instances still incorrectly classified after " + epoch + " epochs: " + (images.size() - numCorrect) + "\n");
            }
        }
        return (images.size()-numCorrect) + 1000000000*epoch;

    }

    /**
     * Method which classifies the image as a category using the features and weights it has learned
     * @param image the image
     * @return classification
     */
    public String classify(Image image) {
        double featureSum = 0;
        int position = 0;
        for(Map.Entry<Feature, Integer> featureValue : image.getFeatureValues().entrySet()){
            featureSum = featureSum + featureValue.getValue() * featureValue.getKey().getWeight();
        }
        featureSum=featureSum+this.dummyFeature*this.dummyFeatureWeight;
        if(featureSum > 0){
            return "X";
        }
        else{
            return "O";
        }
    }


    /**
     * Puts the feature values for each image into the map
     * @param images list of images in the dataset
     * @param features list of the randomly generated features
     */
    public void assignImageFeatureValues(List<Image> images, List<Feature> features) {
        for(Image image: images) {
            image.setFeatureValues(assignFeatureValues(image, features));        }
    }

    /**
     * This assigns all the feature values for a specific image
     * @param image image to use
     * @param features features to fit to the image
     * @return the list of features generated for the image
     */
    public Map<Feature, Integer> assignFeatureValues(Image image, List<Feature> features) {
        Map<Feature, Integer> featureValues = new HashMap<>();
        for(Feature feature: features){
            featureValues.put(feature, assignFeatureValue(image, feature));
        }
        return featureValues;
    }

    /**
     * Assigns the values of a specific feature for a specific image
     * @param image the image to check
     * @param feature the feature to check
     * @return 1 if two or more signs match, 0 if otherwise
     */
    public int assignFeatureValue(Image image, Feature feature) {
        int sum = 0;
        for(int i=0; i<3; i++){
            if(image.getImageData()[feature.getRows()[i]][feature.getColumns()[i]] == feature.getSigns()[i]){
                sum++;
            }
        }
        return (sum>=2)?1:0;
    }

    /**
     * Generates the list of random features to be used in the perceptron
     * @param seed the seed to ensure the random values are the same every time
     * @param numFeatures number of features to generate
     * @param useSeed - whether or not to use a seed for the random number generation
     * @return list of generated features
     */
    public List<Feature> createFeatures(int seed, int numFeatures, boolean useSeed){
        List<Feature> generatedFeatures = new ArrayList<>();
        Random randomGenerator;
        if(useSeed) {
            randomGenerator = new Random(seed);
        }
        else{
            randomGenerator = new Random();
        }

        while(generatedFeatures.size() < numFeatures){
            generatedFeatures.add(generateFeature(randomGenerator));
        }
        this.dummyFeature=1;
        return generatedFeatures;
    }

    /**
     *
     * @param randomGenerator random number generator to use
     * @return the generated feature
     */
    public Feature generateFeature(Random randomGenerator) {
        int rows1 = 10;
        int[] rows = {randomGenerator.nextInt(rows1), randomGenerator.nextInt(rows1), randomGenerator.nextInt(rows1)};
        int cols = 10;
        int[] columns = {randomGenerator.nextInt(cols), randomGenerator.nextInt(cols), randomGenerator.nextInt(cols)};
        boolean[] signs = {randomGenerator.nextBoolean(), randomGenerator.nextBoolean(), randomGenerator.nextBoolean()};
        return new Feature(rows, columns, signs);
    }


    /**
     * Method to load the image data into a list
     * @param fileName file where all the images are located
     * @return a list of images
     */
    public List<Image> load(String fileName, boolean printOutput) {
        List<Image> images = new ArrayList<>();
        try {
            java.util.regex.Pattern bit = java.util.regex.Pattern.compile("[01]");

            Scanner sc = new Scanner(new File(fileName));
            while(sc.hasNext()) {
                if (!sc.next().equals("P1")) System.out.println("Not a P1 PBM file");
                String category = sc.next().substring(1);
                int rows = sc.nextInt();
                int cols = sc.nextInt();
                boolean[][] newImage = new boolean[rows][cols];
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        newImage[r][c] = (sc.findWithinHorizon(bit, 0).equals("1"));
                    }
                }
                Image im = new Image(rows, cols, category, newImage);
                //System.out.println(im.toString());
                images.add(new Image(rows, cols, category, newImage));
            }
            sc.close();
            if(printOutput){System.out.println("Loaded " + images.size() + " images from file");}
        } catch (IOException e) {
            System.out.println("Load from file failed");
        }
        return images;
    }

    /**
     * Main method for the program
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new Part3();

    }
}

