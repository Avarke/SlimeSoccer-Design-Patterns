package server.Interpreter;

public class ScoreCommand implements CommandExpression {
    private static int team1Score = 0;
    private static int team2Score = 0;
    
    public static void updateScores(int t1, int t2) {
        team1Score = t1;
        team2Score = t2;
    }
    
    @Override
    public String execute(String[] args) {
        return "Current Score - Team 1: " + team1Score + " | Team 2: " + team2Score;
    }
    
    @Override
    public String getCommandName() {
        return "score";
    }
}
