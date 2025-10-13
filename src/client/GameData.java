package client;

import java.awt.Color;

public class GameData
{
    private static GameData instance;
	private float p1PosX;
	private float p1PosY;
	private float p2PosX;
	private float p2PosY;
	private float p3PosX;
	private float p3PosY;
	private float p4PosX;
    private float p4PosY;
    private float ballPosX;
    private float ballPosY;
    private float p1FoulBarWidth;
    private float p2FoulBarWidth;
    private float p2FoulBarX;
    private int player1Score, player2Score;
    private Color p1Color, p2Color, p3Color, p4Color;
    private boolean p1FacingRight;
    private boolean p2FacingRight;
    private boolean p3FacingRight;
    private boolean p4FacingRight;
    private boolean goalScored;
    private boolean foul;
	
	private boolean upIsPressed;
	private boolean rightIsPressed;
	private boolean leftIsPressed;

    private int ballEffectCode;


    private GameData(){}

    public static synchronized GameData getInstance(){
        if (instance == null) {
            instance = new GameData();
        }
        return instance;
    }

    // --- Player positions ---
    public synchronized float getP1PosX() { return p1PosX; }
    public synchronized void setP1PosX(float p1PosX) { this.p1PosX = p1PosX; }

    public synchronized float getP1PosY() { return p1PosY; }
    public synchronized void setP1PosY(float p1PosY) { this.p1PosY = p1PosY; }

    public synchronized float getP2PosX() { return p2PosX; }
    public synchronized void setP2PosX(float p2PosX) { this.p2PosX = p2PosX; }

    public synchronized float getP2PosY() { return p2PosY; }
    public synchronized void setP2PosY(float p2PosY) { this.p2PosY = p2PosY; }

    public synchronized float getP3PosX() { return p3PosX; }
    public synchronized void setP3PosX(float p3PosX) { this.p3PosX = p3PosX; }

    public synchronized float getP3PosY() { return p3PosY; }
    public synchronized void setP3PosY(float p3PosY) { this.p3PosY = p3PosY; }

    public synchronized float getP4PosX() { return p4PosX; }
    public synchronized void setP4PosX(float p4PosX) { this.p4PosX = p4PosX; }

    public synchronized float getP4PosY() { return p4PosY; }
    public synchronized void setP4PosY(float p4PosY) { this.p4PosY = p4PosY; }

    // --- Ball ---
    public synchronized float getBallPosX() { return ballPosX; }
    public synchronized void setBallPosX(float ballPosX) { this.ballPosX = ballPosX; }

    public synchronized float getBallPosY() { return ballPosY; }
    public synchronized void setBallPosY(float ballPosY) { this.ballPosY = ballPosY; }

    public synchronized int getBallEffectCode() { return ballEffectCode; }
    public synchronized void setBallEffectCode(int ballEffectCode) { this.ballEffectCode = ballEffectCode; }

    // --- Foul bars ---
    public synchronized float getP1FoulBarWidth() { return p1FoulBarWidth; }
    public synchronized void setP1FoulBarWidth(float p1FoulBarWidth) { this.p1FoulBarWidth = p1FoulBarWidth; }

    public synchronized float getP2FoulBarWidth() { return p2FoulBarWidth; }
    public synchronized void setP2FoulBarWidth(float p2FoulBarWidth) { this.p2FoulBarWidth = p2FoulBarWidth; }

    public synchronized float getP2FoulBarX() { return p2FoulBarX; }
    public synchronized void setP2FoulBarX(float p2FoulBarX) { this.p2FoulBarX = p2FoulBarX; }

    // --- Scores ---
    public synchronized int getPlayer1Score() { return player1Score; }
    public synchronized void setPlayer1Score(int player1Score) { this.player1Score = player1Score; }

    public synchronized int getPlayer2Score() { return player2Score; }
    public synchronized void setPlayer2Score(int player2Score) { this.player2Score = player2Score; }

    // --- Player colors ---
    public synchronized Color getP1Color() { return p1Color; }
    public synchronized void setP1Color(Color p1Color) { this.p1Color = p1Color; }

    public synchronized Color getP2Color() { return p2Color; }
    public synchronized void setP2Color(Color p2Color) { this.p2Color = p2Color; }

    public synchronized Color getP3Color() { return p3Color; }
    public synchronized void setP3Color(Color p3Color) { this.p3Color = p3Color; }

    public synchronized Color getP4Color() { return p4Color; }
    public synchronized void setP4Color(Color p4Color) { this.p4Color = p4Color; }

    // --- Facing directions ---
    public synchronized boolean isP1FacingRight() { return p1FacingRight; }
    public synchronized void setP1FacingRight(boolean p1FacingRight) { this.p1FacingRight = p1FacingRight; }

    public synchronized boolean isP2FacingRight() { return p2FacingRight; }
    public synchronized void setP2FacingRight(boolean p2FacingRight) { this.p2FacingRight = p2FacingRight; }

    public synchronized boolean isP3FacingRight() { return p3FacingRight; }
    public synchronized void setP3FacingRight(boolean p3FacingRight) { this.p3FacingRight = p3FacingRight; }

    public synchronized boolean isP4FacingRight() { return p4FacingRight; }
    public synchronized void setP4FacingRight(boolean p4FacingRight) { this.p4FacingRight = p4FacingRight; }

    // --- Game events ---
    public synchronized boolean isGoalScored() { return goalScored; }
    public synchronized void setGoalScored(boolean goalScored) { this.goalScored = goalScored; }

    public synchronized boolean isFoul() { return foul; }
    public synchronized void setFoul(boolean foul) { this.foul = foul; }

    // --- Input states ---
    public synchronized boolean isUpPressed() { return upIsPressed; }
    public synchronized void setUpPressed(boolean upIsPressed) { this.upIsPressed = upIsPressed; }

    public synchronized boolean isRightPressed() { return rightIsPressed; }
    public synchronized void setRightPressed(boolean rightIsPressed) { this.rightIsPressed = rightIsPressed; }

    public synchronized boolean isLeftPressed() { return leftIsPressed; }
    public synchronized void setLeftPressed(boolean leftIsPressed) { this.leftIsPressed = leftIsPressed; }



}