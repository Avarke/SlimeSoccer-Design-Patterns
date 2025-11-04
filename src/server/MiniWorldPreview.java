package server;

import javax.swing.*;
import java.awt.*;

public class MiniWorldPreview {
    public static void main(String[] args) {
        GameWorldFacade facade = new GameWorldFacade();
        GameWorldFacade.World world = facade.createWorld(
                BallType.FAST, Color.PINK, Color.ORANGE, Color.RED, Color.MAGENTA
        );

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Mini World Preview");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel canvas = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    world.background.draw(g);
                    world.floor.draw(g);
                    world.leftGoal.draw(g);
                    world.rightGoal.draw(g);
                    world.leftErrorBar.draw(g);
                    world.rightErrorBar.draw(g);
                    world.player1.draw(g, world.ball.getX(), world.ball.getY());
                    world.player2.draw(g, world.ball.getX(), world.ball.getY());
                    world.player3.draw(g, world.ball.getX(), world.ball.getY());
                    world.player4.draw(g, world.ball.getX(), world.ball.getY());
                    world.ballArrow.draw(g);
                }
            };

            canvas.setPreferredSize(new Dimension((int) Window.WIDTH, (int) Window.HEIGHT));
            frame.setContentPane(canvas);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
