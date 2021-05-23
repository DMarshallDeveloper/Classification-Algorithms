import java.util.Arrays;

public class Feature {
    private int[] rows;
    private int[] columns;
    private boolean[] signs;
    private double weight;


    public Feature(int[] rows, int[] columns, boolean[] signs) {
        this.rows = rows;
        this.columns = columns;
        this.signs = signs;
        this.weight=0;
    }


    public int[] getRows() {
        return rows;
    }

    public void setRows(int[] rows) {
        this.rows = rows;
    }

    public int[] getColumns() {
        return columns;
    }

    public void setColumns(int[] columns) {
        this.columns = columns;
    }

    public boolean[] getSigns() {
        return signs;
    }

    public void setSigns(boolean[] signs) {
        this.signs = signs;
    }

    public double getWeight() {return weight;}

    public void setWeight(double weight) {this.weight = weight;}


    public String finalOutput() {
        return "Rows=" + Arrays.toString(rows) +
                "\nColumns=" + Arrays.toString(columns) +
                "\nSigns=" + Arrays.toString(signs) +
                "\nWeight=" + this.weight +
                "\n";
    }


    @Override
    public String toString() {
        return "Feature{" +
                "rows=" + Arrays.toString(rows) +
                ", columns=" + Arrays.toString(columns) +
                ", signs=" + Arrays.toString(signs) +
                ", weight=" + this.weight +
                "}\n";
    }
}
