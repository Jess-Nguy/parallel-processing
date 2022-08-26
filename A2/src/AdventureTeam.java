import java.util.Random;

public class AdventureTeam implements Runnable {
    // Every team should share the same rope.
    private static RopeCrossing ropeCrossingOneRef;
    private static RopeCrossing ropeCrossingTwoRef;
    private static RopeCrossing ropeCrossingThreeRef;
    // Every team should have different name, racer counts, hasMedallion, and isFinished.
    private String name;
    private int racerCheckCount = 0;
    private boolean hasMedallion = false;
    private boolean isFinished = false;

    /**
     * Creates AdventureTeam object with set name and references to all 3 ropes.
     * @param name String, name of the team.
     * @param ropeCrossingOneRef RopeCrossing, 1st ropes.
     * @param ropeCrossingTwoRef RopeCrossing, 2nd ropes.
     * @param ropeCrossingThreeRef RopeCrossing, 3rd ropes.
     */
    public AdventureTeam(String name, RopeCrossing ropeCrossingOneRef, RopeCrossing ropeCrossingTwoRef, RopeCrossing ropeCrossingThreeRef ){
        this.name = name;
        this.ropeCrossingOneRef = ropeCrossingOneRef;
        this.ropeCrossingTwoRef = ropeCrossingTwoRef;
        this.ropeCrossingThreeRef = ropeCrossingThreeRef;
    }

    /**
     * Methods run when thread of the object get `start()`.
     * States the overall run time, and gear time.
     * Creates Racers objects and threads.
     * Checks if racers has all reach medallion and back.
     */
    @Override
    public void run() {
        // Track the whole running course.
        long adventureStartTime = System.nanoTime();
        // Sleep for gear time.
        Random random = new Random();
        int low = 100;
        int high = 500;
        int gearWaitTime = random.nextInt(high-low) + low;

        try {
            long gearStartTime = System.nanoTime();
            Thread.sleep(gearWaitTime);
            long gearTime = System.nanoTime() - gearStartTime;
            System.out.println(name + " currently setting up gear for " + gearTime/ 1_000_000);
        } catch (InterruptedException e) {
            System.out.println(name + " gear set up got interrupted...");
            e.printStackTrace();
        }

        // Create 4 racer object for 4 threads of racer objects.
        Racer racerOneObj = new Racer(this, ropeCrossingOneRef, ropeCrossingTwoRef, ropeCrossingThreeRef);
        Thread racerOne = new Thread(racerOneObj);
        Racer racerTwoObj = new Racer(this, ropeCrossingOneRef, ropeCrossingTwoRef, ropeCrossingThreeRef);
        Thread racerTwo = new Thread(racerTwoObj);
        Racer racerThreeObj = new Racer( this, ropeCrossingOneRef, ropeCrossingTwoRef, ropeCrossingThreeRef);
        Thread racerThree = new Thread(racerThreeObj);
        Racer racerFourObj = new Racer( this, ropeCrossingOneRef, ropeCrossingTwoRef, ropeCrossingThreeRef);
        Thread racerFour = new Thread(racerFourObj);

        // Run the racer threads.
        racerOne.start();
        racerTwo.start();
        racerThree.start();
        racerFour.start();

        // As long as this team doesn't have the medallion then sleep and check if all 4 has crossed.
        while(!hasMedallion){
            try {
                Thread.sleep(200);
                if (racerCheckCount == 4) {
                    System.out.println(name + " successfully obtained medallion");
                    hasMedallion = true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Reset count for crossing return. To print out finished time.
        racerCheckCount = 0;
        while (!isFinished) {
            try {
                Thread.sleep(200);
                if (racerCheckCount == 4 && hasMedallion) {
                    long adventureTime = System.nanoTime() - adventureStartTime;
                    System.out.println(name + " completed crossing, total time taken was " + adventureTime /  1_000_000);
                    isFinished = true;
                }
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

    }


    /**
     * Used for team name print.
     * @return String, name of team.
     */
    public String getName(){
        return name;
    }


    /**
     * Synchronized increment of racers count, threads are writing to the same memory.
     */
    synchronized void incrementRacerCounter(){
        racerCheckCount++;
    }

    /**
     * Used for racer while loop chec
     * @return boolean, This returns if the team has a medallion.
     */
    public boolean getHasMedallion(){
        return hasMedallion;
    }
}
