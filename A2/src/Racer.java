import java.util.Random;

public class Racer implements Runnable {
    // Every team should share the same rope.
    private static RopeCrossing ropeCrossingOneRef;
    private static RopeCrossing ropeCrossingTwoRef;
    private static RopeCrossing ropeCrossingThreeRef;
    // Every Racer should have different rope waiting number and different AdventureTeam.
    private int waitAtRope;
    private AdventureTeam team;

    /**
     * Creates Racer object with set AdventureTeam object and references to all 3 ropes.
     * @param team AdventureTeam, the team the racer is assigned to.
     * @param ropeCrossingOneRef RopeCrossing, 1st ropes.
     * @param ropeCrossingTwoRef RopeCrossing, 2nd ropes.
     * @param ropeCrossingThreeRef RopeCrossing, 3rd ropes.
     */
    public Racer(AdventureTeam team, RopeCrossing ropeCrossingOneRef, RopeCrossing ropeCrossingTwoRef, RopeCrossing ropeCrossingThreeRef ) {
        this.team = team;
        this.ropeCrossingOneRef = ropeCrossingOneRef;
        this.ropeCrossingTwoRef = ropeCrossingTwoRef;
        this.ropeCrossingThreeRef = ropeCrossingThreeRef;
    }

    /**
     * Methods run when thread of the object get `start()`.
     * Racer will randomly select a rope to cross.
     * Run the ropes crossing method.
     * Increment the team's racerCheckCount after crossing.
     */
    @Override
    public void run() {
        Random random = new Random();

        waitAtRope = random.nextInt(3-0) + 1;
        switch (waitAtRope) {
            case 1:
                ropeCrossingOneRef.racerIsCrossing(team.getName(), 1);
                team.incrementRacerCounter();
                while(!team.getHasMedallion()){
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            break;
            case 2:
                ropeCrossingTwoRef.racerIsCrossing(team.getName(), 2);
                team.incrementRacerCounter();
                while(!team.getHasMedallion()){
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            break;
            case 3:
                ropeCrossingThreeRef.racerIsCrossing(team.getName(), 3);
                team.incrementRacerCounter();
                while(!team.getHasMedallion()){
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            break;
        }

        // Go back...
        waitAtRope = random.nextInt(3-0) + 1;
        switch (waitAtRope){
            case 1:
                ropeCrossingOneRef.racerIsCrossing(team.getName(), 1);
                team.incrementRacerCounter();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            break;
            case 2:
                ropeCrossingTwoRef.racerIsCrossing(team.getName(), 2);
                team.incrementRacerCounter();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            break;
            case 3:
                ropeCrossingThreeRef.racerIsCrossing(team.getName(), 3);
                team.incrementRacerCounter();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            break;
        }

    }
}
