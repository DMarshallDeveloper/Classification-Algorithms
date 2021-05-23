import java.util.Arrays;
import java.util.Map;

public class Image {
    private int numRows;
    private int numColumns;
    private String category;
    private boolean[][] imageData;
    private Map<Feature, Integer> featureValues;

    public Image(int numRows, int numColumns, String category, boolean[][] imageData) {
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.category = category;
        this.imageData = imageData;
    }

    public int getNumRows() {
        return this.numRows;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public int getNumColumns() {
        return this.numColumns;
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean[][] getImageData() {
        return this.imageData;
    }

    public void setImageData(boolean[][] imageData) {
        this.imageData = imageData;
    }

    public Map<Feature, Integer> getFeatureValues() {return this.featureValues;}

    public void setFeatureValues(Map<Feature, Integer> featureValues) {this.featureValues = featureValues;}


    private String printImageData(){
        String imageData = "";
        for (int row = 0; row < this.numRows; row++) {
            imageData += "[ ";
            for (int column = 0; column < this.numColumns; column++) {
                imageData += this.imageData[row][column] + ", ";
            }
            imageData += " ]\n";
        }

        return imageData;
    }

    @Override
    public String toString() {
        return "Image{" +
                "numRows=" + this.numRows +
                ", numColumns=" + this.numColumns +
                ", category='" + this.category + '\'' +
                ", feature values='" + this.featureValues + '\'' +
                "\n, imageData=" + printImageData() +
                "}\n";
    }
}
