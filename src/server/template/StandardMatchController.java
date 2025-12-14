package server.template;

public final class StandardMatchController extends AbstractMatchController {
    private long halfTimeResumeAt = 0L;

    @Override
    protected void handlePhaseSpecificBehavior() {
        if (currentPhase.equals("HALF_TIME")) {
            if (halfTimeResumeAt == 0L) {
                halfTimeResumeAt = System.currentTimeMillis() + 10_000; // 10s pause
            }
        } else {
            halfTimeResumeAt = 0L;
        }
    }

    @Override
    protected boolean shouldEndMatch() {
        return currentPhase.equals("MATCH_END");
    }

    @Override
    public boolean isPaused() {
        if (!"HALF_TIME".equals(currentPhase)) {
            return false;
        }
        if (halfTimeResumeAt == 0L) {
            return true;
        }
        return System.currentTimeMillis() < halfTimeResumeAt;
    }
}
