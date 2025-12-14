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
        // Initialize start time so the first half runs for the expected duration.
        this.phaseStartTime = System.currentTimeMillis();
    }

    // Template method
    public final void updateMatch() {
        checkPhaseTransition();
        updatePhaseLogic();
        handlePhaseSpecificBehavior();
        if (!"MATCH_END".equals(currentPhase) && shouldEndMatch()) {
            transitionTo("MATCH_END");
        }
    }

    // Hook methods (to be implemented by subclasses)
    protected abstract void handlePhaseSpecificBehavior();

    protected abstract boolean shouldEndMatch();

    // Common logic
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
        // Common update logic for all phases
    }

    public String getCurrentPhase() {
        return currentPhase;
    }

    /**
     * Hook for subclasses to indicate a temporary pause (e.g., half-time break).
     * Default: not paused.
     */
    public boolean isPaused() {
        return false;
    }
}
