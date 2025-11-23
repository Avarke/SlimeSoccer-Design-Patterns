package server.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import server.Slime;

public class Team implements Iterable<Slime> {
    private final List<Slime> players;
    private final String teamName;

    public Team(String teamName) {
        this.teamName = teamName;
        this.players = new ArrayList<>();
    }

    public void addPlayer(Slime player) {
        players.add(player);
    }

    public void removePlayer(Slime player) {
        players.remove(player);
    }

    @Override
    public Iterator<Slime> iterator() {
        return players.iterator();
    }

    public String getTeamName() {
        return teamName;
    }

    public int size() {
        return players.size();
    }
}
