package server.Visitor;

import server.Ball;
import server.Slime;
import server.PowerUp;
import server.PowerUpType;
import server.Goal;

import java.util.HashMap;
import java.util.Map;

/**
 * Visitor that generates end-of-game reports by collecting statistics
 * from various game elements.
 */
public class GameReportVisitor implements GameElementVisitor {
    private int ballTouches = 0;
    private int goalCount = 0;
    private Map<String, Integer> slimeStats = new HashMap<>();
    private Map<PowerUpType, Integer> powerUpUsage = new HashMap<>();
    private double totalBallDistance = 0;
    
    // Track previous ball position for distance calculation
    private Double lastBallX = null;
    private Double lastBallY = null;
    
    @Override
    public void visitBall(Ball ball) {
        ballTouches++;
        
        // Calculate distance traveled
        if (lastBallX != null && lastBallY != null) {
            double dx = ball.getX() - lastBallX;
            double dy = ball.getY() - lastBallY;
            totalBallDistance += Math.sqrt(dx * dx + dy * dy);
        }
        
        lastBallX = ball.getX();
        lastBallY = ball.getY();
    }
    
    @Override
    public void visitSlime(Slime slime) {
        String nickname = slime.getNickname();
        if (nickname == null || nickname.isEmpty()) {
            nickname = "Player";
        }
        
        slimeStats.put(nickname, slimeStats.getOrDefault(nickname, 0) + 1);
    }
    
    @Override
    public void visitPowerUp(PowerUp powerUp) {
        PowerUpType type = powerUp.getType();
        powerUpUsage.put(type, powerUpUsage.getOrDefault(type, 0) + 1);
    }
    
    @Override
    public void visitGoal(Goal goal) {
        goalCount++;
    }
    
    /**
     * Generates a formatted report of collected statistics.
     */
    public String generateReport(int team1Score, int team2Score, int fouls) {
        StringBuilder report = new StringBuilder();
        report.append("===== GAME REPORT =====\n\n");
        
        // Scores
        report.append("FINAL SCORE:\n");
        report.append("  Team 1: ").append(team1Score).append("\n");
        report.append("  Team 2: ").append(team2Score).append("\n");
        report.append("  Fouls: ").append(fouls).append("\n\n");
        
        // Ball statistics
        report.append("BALL STATISTICS:\n");
        report.append("  Total distance traveled: ").append(String.format("%.2f", totalBallDistance)).append(" pixels\n");
        report.append("  Position updates: ").append(ballTouches).append("\n\n");
        
        // Player statistics
        if (!slimeStats.isEmpty()) {
            report.append("PLAYER ACTIVITY:\n");
            for (Map.Entry<String, Integer> entry : slimeStats.entrySet()) {
                report.append("  ").append(entry.getKey()).append(": ")
                      .append(entry.getValue()).append(" updates\n");
            }
            report.append("\n");
        }
        
        // PowerUp usage
        if (!powerUpUsage.isEmpty()) {
            report.append("POWER-UP USAGE:\n");
            for (Map.Entry<PowerUpType, Integer> entry : powerUpUsage.entrySet()) {
                report.append("  ").append(entry.getKey()).append(": ")
                      .append(entry.getValue()).append(" times\n");
            }
            report.append("\n");
        } else {
            report.append("POWER-UP USAGE:\n");
            report.append("  No power-ups collected\n\n");
        }
        
        report.append("======================");
        
        return report.toString();
    }
    
    /**
     * Resets all statistics to start a new report.
     */
    public void reset() {
        ballTouches = 0;
        goalCount = 0;
        slimeStats.clear();
        powerUpUsage.clear();
        totalBallDistance = 0;
        lastBallX = null;
        lastBallY = null;
    }
    
    // Getters for individual stats if needed
    public int getBallTouches() { return ballTouches; }
    public int getGoalCount() { return goalCount; }
    public double getTotalBallDistance() { return totalBallDistance; }
    public Map<String, Integer> getSlimeStats() { return new HashMap<>(slimeStats); }
    public Map<PowerUpType, Integer> getPowerUpUsage() { return new HashMap<>(powerUpUsage); }
}
