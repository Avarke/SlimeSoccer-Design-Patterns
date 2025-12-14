package server.template;

/**
 * Template Method pattern: controls the overall match flow while allowing
 * subclasses to customize phase-specific behavior and termination criteria.
 */
public abstract class AbstractMatchController {
    protected String currentPhase = "FIRST_HALF";
    protected long phaseStartTime;
    protected int halfDuration = 90000; // 90 seconds per half

    protected AbstractMatchController() {
        this.phaseStartTime = System.currentTimeMillis();
    }

    public final void updateMatch() {
        checkPhaseTransition();
        updatePhaseLogic();
        handlePhaseSpecificBehavior();
        if (!"MATCH_END".equals(currentPhase) && shouldEndMatch()) {
            transitionTo("MATCH_END");
        }
    }

    protected abstract void handlePhaseSpecificBehavior();
    protected abstract boolean shouldEndMatch();

    private void checkPhaseTransition() {
        long elapsed = System.currentTimeMillis() - phaseStartTime;

        if (currentPhase.equals("FIRST_HALF") && elapsed > halfDuration) {
            transitionTo("HALF_TIME");
        } else if (currentPhase.equals("HALF_TIME") && elapsed > 10000) { // 10 sec break
            transitionTo("SECOND_HALF");
        } else if (currentPhase.equals("SECOND_HALF") && elapsed > halfDuration) {
            transitionTo("MATCH_END");
        }
    }

    protected void transitionTo(String newPhase) {
        currentPhase = newPhase;
        phaseStartTime = System.currentTimeMillis();
        onPhaseChange(newPhase);
    }

    protected void onPhaseChange(String phase) {
        System.out.println("Match phase changed to: " + phase);
    }

    private void updatePhaseLogic() {
    }

    public String getCurrentPhase() {
        return currentPhase;
    }

    public boolean isPaused() {
        return false;
    }
}
