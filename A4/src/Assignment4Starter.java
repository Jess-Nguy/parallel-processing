import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 *
 * Jess Nguyen 000747411
 * Code modified from: http://www.hameister.org/JavaFX_MandelbrotSet.html
 */


public class Assignment4Starter  extends Application {
    // Size of the canvas for the Mandelbrot set
    public static final int CANVAS_WIDTH = 700;
    public static final int CANVAS_HEIGHT = 600;

    // Values for the Mandelbrot set
    private static double MANDELBROT_RE_MIN = -2;   // Real Number Minimum Value for this Mandelbrot
    private static double MANDELBROT_RE_MAX = 1;    // Real Number Maximum Value for this Mandelbrot
    private static double MANDELBROT_IM_MIN = -1.2; // Imaginary Number Minimum Value for this Mandelbrot
    private static double MANDELBROT_IM_MAX = 1.2;  // Imaginary Number Maximum Value for this Mandelbrot
    private static final int numThreads = 7; // Number of threads since width is 700 so 700/100 = 7
    private double cc = MANDELBROT_RE_MIN; // Copy of c from convergence calculations
    @Override
    public void start(Stage primaryStage) {

        Pane fractalRootPane = new Pane();
        Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        // Create thread pool from the executor service
        ExecutorService pool = Executors.newFixedThreadPool(numThreads);

        // decide how many rows each thread gets
        int numberOfRowsPerThread = (int)Math.ceil((double)Assignment4Starter.CANVAS_WIDTH / numThreads);
        double precision = Math.max((MANDELBROT_RE_MAX - MANDELBROT_RE_MIN) / Assignment4Starter.CANVAS_WIDTH, (MANDELBROT_IM_MAX - MANDELBROT_IM_MIN) / Assignment4Starter.CANVAS_HEIGHT);
        // Future object - a place for worker threads to put their result
        Future<ConvergenceCalculation>[] futureValues = new Future[numThreads];
        for(int i = 0; i < numThreads; i++) {
            int startRow = Math.min(i * numberOfRowsPerThread,Assignment4Starter.CANVAS_WIDTH);
            int endRow = Math.min((i + 1) * numberOfRowsPerThread, Assignment4Starter.CANVAS_WIDTH);
            // Calculate offset of c. Skip first thread since it can use the default
            if( i != 0) {
                for (int xR = startRow; xR < endRow;  xR++) {
                    cc = cc + precision;
                }
            }
            futureValues[i] = pool.submit(new ConvergenceCalculation(startRow, endRow, precision, MANDELBROT_IM_MIN, cc));
        }

        try {
            for (int w=0; w < futureValues.length; w++) {
                // retrieve value from future
                List<ConvergenceCalculation.LoadImage> partialConvergence = (List<ConvergenceCalculation.LoadImage>) futureValues[w].get();
                // draw every LoadImage pixel
                for(ConvergenceCalculation.LoadImage cci: partialConvergence){
                    cci.draw(canvas.getGraphicsContext2D());
                }
            }
            fractalRootPane.getChildren().add(canvas);
            Scene scene = new Scene(fractalRootPane, CANVAS_WIDTH, CANVAS_HEIGHT);
            scene.setFill(Color.BLACK);
            primaryStage.setTitle("Mandelbrot Set");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        pool.shutdown();

    }

    public static void main(String[]args) {launch(args);}
}
