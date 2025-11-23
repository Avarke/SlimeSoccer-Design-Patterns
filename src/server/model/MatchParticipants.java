package server.model;

import java.util.Iterator;
import java.util.NoSuchElementException;
import server.Slime;

/**
 * Iterable wrapper that merges left and right teams into a single iterator.
 */
public final class MatchParticipants implements Iterable<Slime> {
    private final Team leftTeam;
    private final Team rightTeam;

    public MatchParticipants(Team leftTeam, Team rightTeam) {
        this.leftTeam = leftTeam;
        this.rightTeam = rightTeam;
    }

    @Override
    public Iterator<Slime> iterator() {
        return new Iterator<Slime>() {
            private final Iterator<Slime> leftIter = leftTeam.iterator();
            private final Iterator<Slime> rightIter = rightTeam.iterator();

            @Override
            public boolean hasNext() {
                return leftIter.hasNext() || rightIter.hasNext();
            }

            @Override
            public Slime next() {
                if (leftIter.hasNext()) {
                    return leftIter.next();
                }
                if (rightIter.hasNext()) {
                    return rightIter.next();
                }
                throw new NoSuchElementException("No more participants");
            }
        };
    }
}
