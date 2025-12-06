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
}
