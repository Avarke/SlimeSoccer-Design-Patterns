package server.Interpreter;

import server.SlimeSoccer;


public class SetScoreCommand implements CommandExpression {
    @Override
    public String execute(String[] args) {
        if (args == null || args.length < 2) {
            return "Usage: /setscore <team1> <team2>";
        }
        try {
            int t1 = Integer.parseInt(args[0]);
            int t2 = Integer.parseInt(args[1]);
            if (t1 < 0 || t2 < 0) {
                return "Scores must be non-negative.";
            }
            
            SlimeSoccer.setScores(t1, t2);
            
            ScoreCommand.updateScores(t1, t2);
            ReportCommand.updateScores(t1, t2);
            return "Scores updated: Team1=" + t1 + " Team2=" + t2;
        } catch (NumberFormatException ex) {
            return "Invalid numbers. Usage: /setscore <team1> <team2>";
        }
    }

    @Override
    public String getCommandName() {
        return "setscore";
    }
}
