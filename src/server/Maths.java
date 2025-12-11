package server;

public class Maths {
    public static boolean checkCircleCollision(double x1, double y1, double x2, double y2, double r1) {
        if(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)) <= r1) {
            return true;
        }
        else return false;
    }

    public static double[] bounceOffCircle(double x1, double y1, double x2, double y2, double r1, double r2, double velX1, double velY1, double velX2, double velY2) {
        double disX = x1 - x2;
        double disY = y1 - y2;
        double velX = velX1 - velX2;
        double velY = velY1 - velY2;
        disX /= Math.sqrt(Math.pow(x1-x2,2) + Math.pow(y1-y2,2));
        disY /= Math.sqrt(Math.pow(x1-x2,2) + Math.pow(y1-y2,2));
        double dist = Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2,2));

        if(disX*velX + disY*velY > 0) {
            return null;
        }
        if(!checkCircleCollision(x1, y1, x2, y2, r1 + r2)) {
            return null;
        }
        double normal = Math.atan((x2 - x1)/(y2 - y1));
        double collisionVelocityAngle = Math.atan((velX2 - velX1)/(velY2 - velY1));
        double collisionVelocity = Math.sqrt(Math.pow(velX2 - velX1, 2) + Math.pow(velY2 - velY1, 2));
        if(y2 - y1 < 0) {
            normal += Math.PI;
        }
        if(velY2 - velY1 < 0) {
            collisionVelocityAngle += Math.PI;
        }
        double reflectionAngle = (2*normal - collisionVelocityAngle + Math.PI);

        return new double[]{Math.sin(reflectionAngle)*collisionVelocity*0.6 + velX1, Math.cos(reflectionAngle)*collisionVelocity*0.6 + velY1, x2+(r1+r2-dist)*Math.sin(normal), y2+(r1+r2-dist)*Math.cos(normal)};
    }

    public static void bounceBallOffSlime(Ball ball, Slime slime) {
        if(ball.getY() > slime.getY()) { return; }
        if(ball.getRadius()+slime.getRadius()<Math.sqrt(Math.pow(ball.getX()-slime.getX(),2)+Math.pow(ball.getY()-slime.getY(),2))) { return; }

        double normal = Math.atan2((ball.getY() - slime.getY()),(ball.getX() - slime.getX()));

        ball.setX(slime.getX()+Math.cos(normal)*(slime.getRadius()+ball.getRadius()+1));
        ball.setY(slime.getY()+Math.sin(normal)*(slime.getRadius()+ball.getRadius()+1));
        if((ball.getX()-slime.getX())*(ball.getVelX()-slime.getVelX())+(ball.getY()-slime.getY())*(ball.getVelY()-slime.getVelY())>0) { return; }
        double collisionVelocityAngle = Math.atan2((ball.getVelY() - slime.getVelY()),(ball.getVelX() - slime.getVelX()));
        double collisionVelocity = Math.sqrt(Math.pow(ball.getVelX() - slime.getVelX(), 2) + Math.pow(ball.getVelY() - slime.getVelY(), 2));
        double reflectionAngle = 2*normal - collisionVelocityAngle + Math.PI;
        ball.setVelX((Math.cos(reflectionAngle)*collisionVelocity*0.5)+slime.getVelX());
        ball.setVelY((Math.sin(reflectionAngle)*collisionVelocity*0.5)+slime.getVelY());

        ball.setLastTouchingSlot(slime.getSlot());
    }

    public static boolean circleIntersectsRect(
            double cx, double cy, double r,
            double rx, double ry, double rw, double rh) {

        double nx = Math.abs(cx - (rx + rw * 0.5));
        double ny = Math.abs(cy - (ry + rh * 0.5));
        if (nx > (rw * 0.5 + r) || ny > (rh * 0.5 + r)) return false;
        if (nx <= rw * 0.5 || ny <= rh * 0.5) return true;
        double dx = nx - rw * 0.5, dy = ny - rh * 0.5;
        return dx * dx + dy * dy <= r * r;
    }
}