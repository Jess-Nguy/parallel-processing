import java.util.Random;

public class RopeCrossing {

    // Collecting the amount of racer that used the rope object
    private int numberOfCrosses;

    /**
     *
     * @param teamName string of the team name
     * @param ropeNum
     */
    synchronized void racerIsCrossing(String teamName, int ropeNum) {
        Random random = new Random();
        int low = 100;
        int high = 500;
        int onRopeTime = random.nextInt(high-low) + low;

        try {
            long startTime = System.nanoTime();
            Thread.sleep(onRopeTime);
            long time = System.nanoTime() - startTime;
            numberOfCrosses++;
            System.out.println( "       " + teamName + ", Member " + ropeNum + "  crossed the gorge in " + time/ 1_000_000);
        } catch (InterruptedException e) {
            System.out.println("Member " + ropeNum + " on the rope got interrupted...");
            e.printStackTrace();
        }
    }

    /**
     * Used for output.
     * @return int, the number of time a racer has crossed this rope.
     */
    public int getNumberOfCrosses() {
        return numberOfCrosses;
    }
}