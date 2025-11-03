package server;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

public class SlimeSoccer
{
	Window window;
	Slime player1, player2, player3, player4, smile;
	Rectangle background, floor, leftGoalFoulZone, rightGoalFoulZone, leftErrorBar, rightErrorBar, test;
	Ball ball, ballArrow;
	Goal leftGoal, rightGoal;
	Text goalScoredText, foulText, team1ScoreText, team2ScoreText, fpsCounter;
	Picture image;
	ArrayList<ClientData> clients;
	Font scoreFont = new Font("Franklin Gothic Medium Italic", Font.PLAIN, 80);

	static int gamestate;
	static boolean goalScored = false;
	static boolean foul = false;
	static boolean runGame = true;
	final static int SCREENWIDTH = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
	final static int SCREENHEIGHT = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
	final static int SCREENRESOLUTION = SCREENWIDTH*SCREENHEIGHT;

	static int player1Score = 0;
	static int player2Score = 0;

	public SlimeSoccer() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				SlimeSoccer slimeSoccer;

				Runnable init(SlimeSoccer slimeSoccer) {
					this.slimeSoccer = slimeSoccer;
					return this;
				}
				@Override
				public void run() {
					window = new Window(slimeSoccer);
				}
			}.init(this));
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
		clients = new ArrayList<ClientData>();
		new Thread(new ConnectionReceiverRunnable(this)).start();
		init();
		while(true) {
			if(runGame) {
				tick();
				sendData();
				window.repaint();
				ball.crossBarCheck();
				try {
					Thread.sleep(16);
				} catch(Exception e) {}
			} else {
				if(Window.reset) {
					reset();
				}
				
			}
		}
	}

    public void init() {
        GameWorldFacade facade = new GameWorldFacade();
        GameWorldFacade.World w = facade.createDefaultWorld();

        player1 = w.player1;
        player2 = w.player2;
        player3 = w.player3;
        player4 = w.player4;
        background = w.background;
        floor = w.floor;
        ball = w.ball;
        ballArrow = w.ballArrow;
        leftGoal = w.leftGoal;
        rightGoal = w.rightGoal;
        leftGoalFoulZone = w.leftGoalFoulZone;
        rightGoalFoulZone = w.rightGoalFoulZone;
        leftErrorBar = w.leftErrorBar;
        rightErrorBar = w.rightErrorBar;

        goalScoredText = new Text("GOAL!", 0.286*Window.WIDTH, 0.278*Window.HEIGHT, (int)(0.278*Window.HEIGHT), Color.WHITE, "Franklin Gothic Medium Italic");
        foulText = new Text("FOUL!", 0.286*Window.WIDTH, 0.278*Window.HEIGHT, (int)(0.278*Window.HEIGHT), Color.WHITE, "Franklin Gothic Medium Italic");
        team1ScoreText = new Text("" + player1Score, 0.026*Window.WIDTH, 0.093*Window.HEIGHT, (int)(0.074*Window.HEIGHT), Color.WHITE, "Franklin Gothic Medium Italic");
        team2ScoreText = new Text("" + player2Score, 0.885*Window.WIDTH, 0.093*Window.HEIGHT, (int)(0.074*Window.HEIGHT), Color.WHITE, "Franklin Gothic Medium Italic");
        gamestate = 1;

		/*
		Ball normal = BallFactory.createBall(BallType.NORMAL, 300, 100);
		Ball heavy = BallFactory.createBall(BallType.HEAVY, 350, 100);
		*/
	}

	public void draw(Graphics g) {
		background.draw(g);
		floor.draw(g);
		if(goalScored) {
			goalScoredText.drawString(g);
		}
		if(foul) {
			foulText.drawString(g);
		}
		if(gamestate == 1) {
			team1ScoreText.drawString(g);
			team2ScoreText.drawString(g);
			leftGoalFoulZone.draw(g);
			rightGoalFoulZone.draw(g);
			leftErrorBar.draw(g);
			rightErrorBar.draw(g);
			player1.draw(g, ball.getX(), ball.getY());
			player2.draw(g, ball.getX(), ball.getY());
			player3.draw(g, ball.getX(), ball.getY());
			player4.draw(g, ball.getX(), ball.getY());
			ballArrow.draw(g);
			ball.draw(g);
		}
		leftGoal.draw(g);
		rightGoal.draw(g);
	}

	void controls()
	{
		if(Window.playerOneRight && !Window.playerOneLeft) {
			player1.setVelX(7);
		}
		else if(Window.playerOneLeft && !Window.playerOneRight) {
			player1.setVelX(-7);
		}
		else if((!Window.playerOneLeft && !Window.playerOneRight) || (Window.playerOneLeft && Window.playerOneRight)) {
			player1.setVelX(0);
		}

		if(Window.playerTwoRight && !Window.playerTwoLeft) {
			player2.setVelX(7);
		}
		else if(Window.playerTwoLeft && !Window.playerTwoRight) {
			player2.setVelX(-7);
		}
		else if((!Window.playerTwoLeft && !Window.playerTwoRight) || (Window.playerTwoLeft && Window.playerTwoRight)) {
			player2.setVelX(0);
		}

		if(Window.playerThreeRight && !Window.playerThreeLeft) {
			player3.setVelX(7);
		}
		else if(Window.playerThreeLeft && !Window.playerThreeRight) {
			player3.setVelX(-7);
		}
		else if((!Window.playerThreeLeft && !Window.playerThreeRight) || (Window.playerThreeLeft && Window.playerThreeRight)) {
			player3.setVelX(0);
		}

		if(Window.playerFourRight && !Window.playerFourLeft) {
			player4.setVelX(7);
		}
		else if(Window.playerFourLeft && !Window.playerFourRight) {
			player4.setVelX(-7);
		}
		else if((!Window.playerFourLeft && !Window.playerFourRight) || (Window.playerFourLeft && Window.playerFourRight)) {
			player4.setVelX(0);
		}


		if(Window.playerOneJump) {
			player1.jump();
		}
		if(Window.playerTwoJump) {
			player2.jump();
		}
		if(Window.playerThreeJump) {
			player3.jump();
		}
		if(Window.playerFourJump) {
			player4.jump();
		}
	}

	public void tick() {
		ballArrow.setX(ball.getX());
		ballArrow.setRadius((int)-(ball.getY())/50);
		player1.setX(player1.getX() + player1.getVelX());
		player1.updateEyes();
		player2.setX(player2.getX() + player2.getVelX());
		player2.updateEyes();
		player3.setX(player3.getX() + player3.getVelX());
		player3.updateEyes();
		player4.setX(player4.getX() + player4.getVelX());
		player4.updateEyes();
		Maths.bounceBallOffSlime(ball, player1);
		Maths.bounceBallOffSlime(ball, player2);
		Maths.bounceBallOffSlime(ball, player3);
		Maths.bounceBallOffSlime(ball, player4);
		controls();

		// Increase update freq if near goalpost.
		if(ball.getY()>=leftGoal.getY() && (ball.getX()<=leftGoal.getX()+leftGoal.getWidth() || ball.getX()>=rightGoal.getX())) {
			for(int i=0; i<10; i++) {
				if(runGame) {
					ball.update(1);
					ball.boundaries();
					ball.crossBarCheck();
				}
			}
		} else {
			ball.update(10);
			ball.boundaries();
			ball.crossBarCheck();
		}

		player1.downMovement();
		player1.floorCheck();
		player1.gravity();
		if(player1.foulCheckLeft() || player2.foulCheckLeft() || player1.foulCheckRight() || player2.foulCheckRight()) {
			leftErrorBar.shrinkLeft();
		}
		else leftErrorBar.setWidth(Window.WIDTH/2);


		player2.downMovement();
		player2.floorCheck();
		player2.gravity();

		player3.downMovement();
		player3.floorCheck();
		player3.gravity();
		if(player3.foulCheckRight() || player4.foulCheckRight() || player3.foulCheckLeft() || player4.foulCheckLeft()) {
			rightErrorBar.shrinkRight();
		} else {
			rightErrorBar.setWidth(Window.WIDTH/2);
			rightErrorBar.setX(Window.WIDTH/2);
		}

		player4.downMovement();
		player4.floorCheck();
		player4.gravity();

		if(rightErrorBar.getWidth() < 1)
		{
			if(gamestate == 1) {
				player1Score ++;
			}
			foul = true;
			runGame = false;
		}
		if(leftErrorBar.getWidth() < 1) {
			if(gamestate == 1) {
				player2Score ++;
			}
			foul = true;
			runGame = false;
		}
		if(ball.getX() < leftGoal.getX()+leftGoal.getWidth() && ball.getY() > leftGoal.getY()) {
			if(gamestate == 1) {
				player2Score ++;
			}
			goalScored = true;
			runGame = false;
		}
		if(ball.getX() > rightGoal.getX() && ball.getY() > rightGoal.getY()) {
			if(gamestate == 1) {
				player1Score ++;
			}
			goalScored = true;
			runGame = false;
		}
	}

	void reset() {
		init();
		Window.reset = false;
		if(foul)
			foul = false;
		else
			goalScored = false;
		runGame = true;
		gamestate = 1;
	}

	public void sendData() {	
		for(ClientData client : clients) {
			client.getOutputStream().println(player1.getX()+" "+player1.getY()+" "+player1.isFacingRight()+" "+player2.getX()+" "+player2.getY()+" "+player2.isFacingRight()+" "+player3.getX()+" "+player3.getY()+" "+player3.isFacingRight()+" "+player4.getX()+" "+player4.getY()+" "+player4.isFacingRight()+" "+ball.getX()+" "+ball.getY()+" "+player1.getColor().getRGB()+" "+player2.getColor().getRGB()+" "+player3.getColor().getRGB()+" "+player4.getColor().getRGB()+" "+goalScored+" "+foul+" "+leftErrorBar.getWidth()+" "+rightErrorBar.getWidth()+" "+rightErrorBar.getX()+" "+player1Score+" "+player2Score);
		}
	}

	public static void main(String[] args) {
		new SlimeSoccer();
	}
}