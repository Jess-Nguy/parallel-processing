import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Jess Nguyen 000747411
 * Code modified from: http://www.hameister.org/JavaFX_MandelbrotSet.html
 */

public class ConvergenceCalculation implements Callable {
    private int startRow; // Thread's starting row position
    private int endRow; // Thread's ending row position
    private static double precision; // precision that every thread all should have the same. Also, it should never change
    private static double imMin; // imMin for y position calculations
    private double offsetC; // The offset C position

    public ConvergenceCalculation(int startRow, int endRow, double precision, double imMin, double offsetC){
        this.startRow = startRow;
        this.endRow = endRow;
        this.precision = precision;
        this.imMin = imMin;
        this.offsetC = offsetC;
    }
    @Override
    public List<LoadImage>  call() throws Exception {
        List<LoadImage>  loadSection = paintSet();
        return loadSection;
    }

    /**
     * Method to calculate the color of each pixel on the screen for the Mandelbrot
     * Returns a list of LoadImage objects each containing individual pixel.
     */
    public List<LoadImage> paintSet() {

        int convergenceSteps = 50;
        List<LoadImage> loadPaintSection = new ArrayList<LoadImage>();

        // Outer for loop is controlling the x position (xR) and the convergence for the real number
        for (double c = offsetC, xR = startRow; xR <  endRow; c = c + precision, xR++) {

            // Inner for loop is controlling the y position (yR) and the convergence for the imaginary number
            for (double ci = imMin, yR = 0; yR < Assignment4Starter.CANVAS_HEIGHT; ci = ci + precision, yR++) {
                double convergenceValue = checkConvergence(ci, c, convergenceSteps);  // check how many steps have occurred towards convergence
                double t1 = (double) convergenceValue / convergenceSteps;  // calculate the ratio of the current convergent step compared to the complete step (50)
                double c1 = Math.min(255 * 2 * t1, 255);  // calculate the ratio red and blue components of the color
                double c2 = Math.max(255 * (2 * t1 - 1), 0);  // calculate the ratio for the green component of the color

                // Create a pixel object and then add it to the list to be returned
                if (convergenceValue != convergenceSteps) {
                    loadPaintSection.add(new LoadImage(xR,yR,Color.color(c2 / 255.0, c1 / 255.0, c2 / 255.0)));
                } else {
                    loadPaintSection.add(new LoadImage(xR,yR,Color.PURPLE));
                }
            }
        }
        return loadPaintSection;
    }


    public class LoadImage {

        double xPosition = 0;
        double yPosition  = 0;
        Paint color  = Color.PURPLE;

        public LoadImage(double x, double y, Paint colorFill){
            this.xPosition = x;
            this.yPosition = y;
            this.color =colorFill;
        }

        /**
         * Draw this pixel for canvas.
         * @param gc 2d graphics canvas this pixel will draw on.
         */
        public void draw(GraphicsContext gc) {
            gc.setFill(color); // Set colour of pixel
            gc.fillRect(xPosition, yPosition, 1, 1);  // one pixel drawn (rectangle of 1 by 1)
        }
    }
    /**
     * Checks the convergence of a coordinate (c, ci) The convergence factor
     * determines the color of the point.
     * @param c real number current value
     * @param ci imaginary number current value
     * @param convergenceSteps number of steps to converge on
     * @return Which ever is greater of the number of steps it takes to converge or the total convergence steps
     */
    private int checkConvergence(double ci, double c, int convergenceSteps) {
        double z = 0;
        double zi = 0;
        for (int i = 0; i < convergenceSteps; i++) {
            double ziT = 2 * (z * zi);
            double zT = z * z - (zi * zi);
            z = zT + c;
            zi = ziT + ci;

            if (z * z + zi * zi >= 4.0) {
                return i;
            }
        }
        return convergenceSteps;
    }
}
