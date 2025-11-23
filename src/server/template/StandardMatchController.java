package server.template;

public final class StandardMatchController extends AbstractMatchController {

    @Override
    protected void handlePhaseSpecificBehavior() {
        if (currentPhase.equals("HALF_TIME")) {
            // Pause gameplay during half time
        }
    }

    @Override
    protected boolean shouldEndMatch() {
        return currentPhase.equals("MATCH_END");
    }
}
