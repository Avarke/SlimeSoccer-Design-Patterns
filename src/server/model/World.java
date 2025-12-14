package server.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import server.Ball;
import server.Slime;

public class World implements Iterable<Slime> {
    private final List<Slime> allPlayers;
    private final Team leftTeam;
    private final Team rightTeam;
    private final MatchParticipants participants;
    private Ball ball;


    public World() {
        this.allPlayers = new ArrayList<>();
        this.leftTeam = new Team("Left Team");
        this.rightTeam = new Team("Right Team");
        this.participants = new MatchParticipants(leftTeam, rightTeam);
    }

    public void addPlayer(Slime player, boolean isLeftTeam) {
        allPlayers.add(player);
        if (isLeftTeam) {
            leftTeam.addPlayer(player);
            player.setTeamSide(TeamSide.LEFT);
        } else {
            rightTeam.addPlayer(player);
            player.setTeamSide(TeamSide.RIGHT);
        }
    }

    public void addPlayer(Slime player, TeamSide side) {
        addPlayer(player, side == TeamSide.LEFT);
    }

    public void setBall(Ball ball) {
        this.ball = ball;
    }

    @Override
    public Iterator<Slime> iterator() {
        return allPlayers.iterator();
    }

    public Team getLeftTeam() {
        return leftTeam;
    }

    public Team getRightTeam() {
        return rightTeam;
    }

    public MatchParticipants getParticipants() {
        return participants;
    }

    public void updateAll(double deltaTime) {
        // Update all players using iterator
        for (Slime player : this) {
            player.updateState(deltaTime);
        }
    }

    /**
      Iterable over the players ordered by ascending distance to the current ball position.   
     */
    public Iterable<Slime> nearestToBall(final int maxCount) {
        if (ball == null) {
            return this; // fallback
        }
        final double bx = ball.getX();
        final double by = ball.getY();
        return new Iterable<Slime>() {
            @Override
            public Iterator<Slime> iterator() {
                // Build a sorted snapshot by distance
                java.util.ArrayList<Slime> snapshot = new java.util.ArrayList<>(allPlayers);
                java.util.Collections.sort(snapshot, new java.util.Comparator<Slime>() {
                    @Override
                    public int compare(Slime a, Slime b) {
                        double da = dist2(a.getX(), a.getY(), bx, by);
                        double db = dist2(b.getX(), b.getY(), bx, by);
                        return Double.compare(da, db);
                    }
                });
                if (maxCount > 0 && maxCount < snapshot.size()) {
                    snapshot = new java.util.ArrayList<>(snapshot.subList(0, maxCount));
                }
                final java.util.Iterator<Slime> it = snapshot.iterator();
                return new Iterator<Slime>() {
                    @Override public boolean hasNext() { return it.hasNext(); }
                    @Override public Slime next() { return it.next(); }
                    @Override public void remove() { throw new UnsupportedOperationException(); }
                };
            }
        };
    }

    /**
      Iterable over nearest opponents to a given source player (sorted by distance).
     */
    public Iterable<Slime> nearestOpponentsTo(final Slime source, final int maxCount) {
        if (source == null) return this;
        final server.model.TeamSide side = source.getTeamSide();
        final double sx = source.getX();
        final double sy = source.getY();
        return new Iterable<Slime>() {
            @Override
            public Iterator<Slime> iterator() {
                java.util.ArrayList<Slime> opponents = new java.util.ArrayList<>();
                for (Slime s : allPlayers) {
                    if (s == source) continue;
                    if (side == server.model.TeamSide.LEFT && s.getTeamSide() == server.model.TeamSide.RIGHT) {
                        opponents.add(s);
                    } else if (side == server.model.TeamSide.RIGHT && s.getTeamSide() == server.model.TeamSide.LEFT) {
                        opponents.add(s);
                    }
                }
                java.util.Collections.sort(opponents, new java.util.Comparator<Slime>() {
                    @Override
                    public int compare(Slime a, Slime b) {
                        double da = dist2(a.getX(), a.getY(), sx, sy);
                        double db = dist2(b.getX(), b.getY(), sx, sy);
                        return Double.compare(da, db);
                    }
                });
                if (maxCount > 0 && maxCount < opponents.size()) {
                    opponents = new java.util.ArrayList<>(opponents.subList(0, maxCount));
                }
                final java.util.Iterator<Slime> it = opponents.iterator();
                return new Iterator<Slime>() {
                    @Override public boolean hasNext() { return it.hasNext(); }
                    @Override public Slime next() { return it.next(); }
                    @Override public void remove() { throw new UnsupportedOperationException(); }
                };
            }
        };
    }

    private static double dist2(double ax, double ay, double bx, double by) {
        double dx = ax - bx, dy = ay - by;
        return dx*dx + dy*dy;
    }
}
