package server.template;

/**
 * Training-oriented match controller: shorter halves, no half-time break,
 * and minimal phase-specific behavior.
 */
public final class TrainingMatchController extends AbstractMatchController {

    public TrainingMatchController() {
        this.halfDuration = 20_000; // 20 seconds per half
    }

    @Override
    protected void handlePhaseSpecificBehavior() {
        // Skip half-time; immediately resume play.
        if ("HALF_TIME".equals(currentPhase)) {
            transitionTo("SECOND_HALF");
        }
    }

    @Override
    protected boolean shouldEndMatch() {
        return "MATCH_END".equals(currentPhase);
    }
}
