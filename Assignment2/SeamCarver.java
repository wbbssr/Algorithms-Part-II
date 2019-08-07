import edu.princeton.cs.algs4.Picture;
import java.awt.Color;
import java.util.NoSuchElementException;
import java.util.Arrays;

public class SeamCarver {
    private Picture picture;
    private int width;
    private int height;
    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException();
        this.picture = new Picture(picture);
        this.width   = this.picture.width();
        this.height  = this.picture.height();
    }
    // current picture
    public Picture picture() {
        return new Picture(this.picture);
    }
    // width of current picture
    public int width() {
        return width;
    }
    // height of current picture
    public int height() {
        return height;
    }
    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        validateXCoordinate(x);
        validateYCoordinate(y);
        return dualGradientEnergy(x, y);

    }
    // sequence of indices for horizontal seam
    
    public int[] findHorizontalSeam() {
        int[]      horizontalSeam = new int[width];
        int[][]    edgeTo         = new int[width][height];
        double[][] energyArray    = new double[width][height];
        double[][] distTo         = new double[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                edgeTo[i][j] = -1;
                energyArray[i][j] = energy(i, j);
                distTo[i][j] = Double.POSITIVE_INFINITY;
            }
        }

        for (int j = 1; j < height - 1; j++) {
            edgeTo[0][j] = j;
            distTo[0][j] = 0;
        }

        // 它本身就是一个topological order
        for (int i = 0; i < width - 2; i++) {
            for (int j = 1; j < height - 1; j++) {
                if (distTo[i + 1][j - 1] > distTo[i][j] + energyArray[i + 1][j - 1]) {
                    distTo[i + 1][j - 1] = distTo[i][j] + energyArray[i + 1][j - 1];
                    edgeTo[i + 1][j - 1] = j;
                }
                if (distTo[i + 1][j] > distTo[i][j] + energyArray[i + 1][j]) {
                    distTo[i + 1][j] = distTo[i][j] + energyArray[i + 1][j];
                    edgeTo[i + 1][j] = j;

                }
                if (distTo[i + 1][j + 1] > distTo[i][j] + energyArray[i + 1][j + 1]) {
                    distTo[i + 1][j + 1] = distTo[i][j] + energyArray[i + 1][j + 1];
                    edgeTo[i + 1][j + 1] = j;

                }
            }
        }

        if (width <= 2 || height <= 2) {
            for (int i = 0; i < width; i++)
                horizontalSeam[i] = 0;
            return horizontalSeam;
        }

        int minID = -1;
        double minDist = Double.POSITIVE_INFINITY;
        for (int j = 1; j < height - 1; j++) {
            if (distTo[width - 2][j] < minDist) {
                minID = j;
                minDist = distTo[width - 2][j];
            }
        }

        horizontalSeam[width - 1] = horizontalSeam[width - 2] = minID;
        for (int i = width - 3; i > 0; i--)
            horizontalSeam[i] = edgeTo[i + 1][horizontalSeam[i + 1]];
        horizontalSeam[0] = horizontalSeam[1]; 

        return horizontalSeam; 
    }
    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int[] verticalSeam     = new int[height];
        int[][] edgeTo         = new int[height][width];
        double[][] energyArray = new double[height][width];
        double[][] distTo      = new double[height][width];

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                edgeTo[j][i] = -1;
                energyArray[j][i] = energy(i, j);
                distTo[j][i] = Double.POSITIVE_INFINITY;
            }
        }

        for (int i = 1; i < width - 1; i++) {
            edgeTo[0][i] = i;
            distTo[0][i] = 0;
        }
        // 它本身就是一个topological order
        for (int j = 0; j < height - 2; j++) {
            for (int i = 1; i < width - 1; i++) {
                if (distTo[j + 1][i - 1] > distTo[j][i] + energyArray[j + 1][i - 1]) {
                    distTo[j + 1][i - 1] = distTo[j][i] + energyArray[j + 1][i - 1];
                    edgeTo[j + 1][i - 1] = i;
                }
                if (distTo[j + 1][i] > distTo[j][i] + energyArray[j + 1][i]) {
                    distTo[j + 1][i] = distTo[j][i] + energyArray[j + 1][i];
                    edgeTo[j + 1][i] = i;
                }
                if (distTo[j + 1][i + 1] > distTo[j][i] + energyArray[j + 1][i + 1]) {
                    distTo[j + 1][i + 1] = distTo[j][i] + energyArray[j + 1][i + 1];
                    edgeTo[j + 1][i + 1] = i;
                }


            }
        }


        if (height <= 2 || width <= 2) {
            for (int i = 0; i < height; i++)
                verticalSeam[i] = 0;
            return verticalSeam;
        }

        int minID = -1;
        double minDist = Double.POSITIVE_INFINITY;
        for(int i = 1; i < width - 1; i++) {
            if (distTo[height - 2][i] < minDist) {
                minID = i;
                minDist = distTo[height - 2][i];
            }
        }



        verticalSeam[height - 1] = verticalSeam[height - 2] = minID;
        for (int j = height - 3; j > 0; j--) {
            verticalSeam[j] = edgeTo[j + 1][verticalSeam[j + 1]];
        }
        verticalSeam[0] = verticalSeam[1];

        return verticalSeam;
    }
    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null)
            throw new IllegalArgumentException();

        if (seam.length != width)
            throw new IllegalArgumentException();

        for (int i = 1; i < width; i++)
            if (seam[i] - seam[i - 1] > 1 || seam[i - 1] - seam[i] > 1)
                throw new IllegalArgumentException();

        for (int i = 0; i < width; i++) {
            validateYCoordinate(seam[i]);
            for (int j = seam[i]; j < height - 1; j++)
                this.picture.set(i, j, this.picture.get(i, j + 1));
        }

        this.height--;
        Picture pic = new Picture(width, height);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pic.set(i, j, this.picture.get(i, j));
            }
        }

        this.picture = pic;

    }
    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null)
            throw new IllegalArgumentException();

        if (seam.length != height)
            throw new IllegalArgumentException();

        for (int j = 1; j < height; j++) {
            if (seam[j] - seam[j - 1] > 1 || seam[j - 1] - seam[j] > 1)
                throw new IllegalArgumentException();
        }

        for (int j = 0; j < height; j++) {
            validateXCoordinate(seam[j]);
            for (int i = seam[j]; i < width - 1; i++)
                this.picture.set(i, j, this.picture.get(i + 1, j));
        }

        this.width--;
        Picture pic = new Picture(width, height);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pic.set(i, j, this.picture.get(i, j));
            }
        }

        this.picture = pic;
    }

    private void validateXCoordinate(int x) {
        if (x < 0 || x >= width)
            throw new IllegalArgumentException();
    }

    private void validateYCoordinate(int y) {
        if (y < 0 || y >= height)
            throw new IllegalArgumentException();
    }

    private double dualGradientEnergy(int x, int y) {
        if (x == 0 || x == width - 1 || y == 0 || y == height - 1)
            return 1000.0;

        Color upPixelColor    = getColor(x - 1, y);
        Color downPixelColor  = getColor(x + 1, y);
        Color leftPixelColor  = getColor(x, y - 1);
        Color rightPixelColor = getColor(x, y + 1);
        double energySquare = 0.0;

        energySquare = energySquare + (upPixelColor.getRed() - downPixelColor.getRed()) * (upPixelColor.getRed() - downPixelColor.getRed()) + (upPixelColor.getGreen() - downPixelColor.getGreen()) * (upPixelColor.getGreen() - downPixelColor.getGreen()) + (upPixelColor.getBlue() - downPixelColor.getBlue()) * (upPixelColor.getBlue() - downPixelColor.getBlue());
        energySquare = energySquare + (leftPixelColor.getRed() - rightPixelColor.getRed()) * (leftPixelColor.getRed() - rightPixelColor.getRed()) + (leftPixelColor.getGreen() - rightPixelColor.getGreen()) * (leftPixelColor.getGreen() - rightPixelColor.getGreen()) + (leftPixelColor.getBlue() - rightPixelColor.getBlue()) * (leftPixelColor.getBlue() - rightPixelColor.getBlue());
        return Math.sqrt(energySquare);
    }

    private Color getColor(int x, int y) {
        return picture.get(x, y);
    }

    public static void main(String[] args) {

    }
}