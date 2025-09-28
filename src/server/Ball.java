package server;

import java.awt.Color;
import java.awt.Graphics;

public class Ball 
{
	private double x, y, lowBound, rightBound, leftCBarX, rightCBarX, cBarY;
	private double radius;
	private Color color;
	
	private double gravity = 0.3;
	private double velY, velX;
	
	public Ball(double x, double y, double radius, Color color) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.color = color;
		
		lowBound = 0.814*Window.HEIGHT;
		rightBound = Window.WIDTH;
		leftCBarX = 0.093*Window.WIDTH;
		rightCBarX = Window.WIDTH - leftCBarX;
		cBarY = 0.648*Window.HEIGHT;
	}
	
	public void draw(Graphics g) {
		g.setColor(color);
		g.fillOval((int) (x - radius), (int) (y - radius), (int) (radius*2), (int) (radius*2));
	}
	
	public void boundaries() {
		if(y + radius > lowBound) {
			y = lowBound - (radius + 1);
			velY *= -0.5;
		}
		
		if(x - radius < 0) {
			x = 1 + radius;
			velX = -velX;
		}
		
		if(x + radius > rightBound) {
			x = rightBound - (radius + 1);
			velX = -velX;
		}
		
	}
	
	public void update(int speedfactor) {
		velY += gravity * speedfactor/10;
		y += velY * speedfactor/10;
		x += velX * speedfactor/10;
		
		if(y > lowBound) {
			// Apply horizontal friction.
			if(velX > 0) {
				velX -= 0.2;
			}
			else if(velX < 0) {
				velX += 0.2;
			}
			else if(velX < 0.2 && velX > 0) {
				velX = 0;
			}
			else if(velX > -0.2 && velX < 0) {
				velX = 0;
			}
		}
	}
	
	public void crossBarCheck() {
		if(y < cBarY + 10 && y > cBarY - 10) {
			if(x < leftCBarX || x > rightCBarX) {
				velY = -velY;
			}
		}
	}
	
	public void reset(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() { return x; }
	public void setX(double x) { this.x = x; }
	public double getY() { return y; }
	public void setY(double y) { this.y = y; }
	public double getRadius() { return radius; }
	public void setRadius(int radius) { this.radius = radius; }
	public double getVelX() { return velX; }
	public void setVelX(double velX) { this.velX = velX; }
	public double getVelY() { return velY; }
	public void setVelY(double velY) { this.velY = velY; }
}