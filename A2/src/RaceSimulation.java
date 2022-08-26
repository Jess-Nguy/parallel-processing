/**
 * Date: October 11, 2021
 *
 * StAuth10222: I Jess Nguyen, 000747411 certify that this material is my original work.
 * No other person's work has been used without due acknowledgement. I have not made my work
 * available to anyone else.
 */


public class RaceSimulation {
    // Every team should share the same rope.
    private static RopeCrossing ropeCrossingOne;
    private static RopeCrossing ropeCrossingTwo;
    private static RopeCrossing ropeCrossingThree;

    public static void main(String[] args)  {

        // Create Rope objects to pass to adventure team and racer.
        ropeCrossingOne = new RopeCrossing();
        ropeCrossingTwo = new RopeCrossing();
        ropeCrossingThree = new RopeCrossing();

        //  Create 5 AdventureTeam object for 5 threads of AdventureTeam objects.
        Thread adventureTeamOne = new Thread(new AdventureTeam("Team 1", ropeCrossingOne, ropeCrossingTwo, ropeCrossingThree));
        Thread adventureTeamTwo = new Thread(new AdventureTeam("Team 2", ropeCrossingOne, ropeCrossingTwo, ropeCrossingThree));
        Thread adventureTeamThree = new Thread(new AdventureTeam("Team 3", ropeCrossingOne, ropeCrossingTwo, ropeCrossingThree));
        Thread adventureTeamFour = new Thread(new AdventureTeam("Team 4", ropeCrossingOne, ropeCrossingTwo, ropeCrossingThree));
        Thread adventureTeamFive = new Thread(new AdventureTeam("Team 5", ropeCrossingOne, ropeCrossingTwo, ropeCrossingThree));

        // Run Adventure threads.
        adventureTeamOne.start();
        adventureTeamTwo.start();
        adventureTeamThree.start();
        adventureTeamFour.start();
        adventureTeamFive.start();

        // Making sure all threads are exiting appropriately.
        try {
            adventureTeamOne.join();
            adventureTeamTwo.join();
            adventureTeamThree.join();
            adventureTeamFour.join();
            adventureTeamFive.join();
        } catch(InterruptedException e){
            e.printStackTrace();
        }

        // Print the amount of racer that crossed a certain rope.
        System.out.printf("The number of racers to the rope was: " + ropeCrossingOne.getNumberOfCrosses());
        System.out.printf("\nThe number of racers to the rope was: " + ropeCrossingTwo.getNumberOfCrosses());
        System.out.printf("\nThe number of racers to the rope was: " + ropeCrossingThree.getNumberOfCrosses());
    }
}
