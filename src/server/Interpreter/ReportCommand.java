package server.Interpreter;

import server.Visitor.GameReportVisitor;

public class ReportCommand implements CommandExpression {
    private static GameReportVisitor reportVisitor = new GameReportVisitor();
    private static int team1Score = 0;
    private static int team2Score = 0;
    private static int totalFouls = 0;
    
    public static GameReportVisitor getReportVisitor() {
        return reportVisitor;
    }
    
    public static void updateScores(int t1Score, int t2Score) {
        team1Score = t1Score;
        team2Score = t2Score;
    }
    
    public static void incrementFouls() {
        totalFouls++;
    }
    
    public static void resetStats() {
        team1Score = 0;
        team2Score = 0;
        totalFouls = 0;
        reportVisitor.reset();
    }
    
    @Override
    public String execute(String[] args) {
        return reportVisitor.generateReport(team1Score, team2Score, totalFouls);
    }
    
    @Override
    public String getCommandName() {
        return "report";
    }
}
