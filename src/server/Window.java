package server;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class Window extends JFrame {
    public static final double WIDTH = 1920.0, HEIGHT = 1080.0;

    private static final long serialVersionUID = 3685355721496314265L;
    SlimeSoccer slimesoccer;
    Panel panel;
    static boolean playerOneRight, playerOneLeft, playerTwoRight, playerTwoLeft, playerThreeLeft, playerThreeRight,
            playerFourLeft, playerFourRight, playerOneJump, playerTwoJump, playerThreeJump, playerFourJump, reset,
            saveBall, restoreBall;

    Window(SlimeSoccer temp) {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setFocusable(false);
        slimesoccer = temp;
        panel = new Panel(slimesoccer);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().add(panel);
        panel.setFocusable(true);
        panel.requestFocus();
        panel.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == 'D' || e.getKeyChar() == 'd') {
                    playerOneRight = true;
                }
                if (e.getKeyChar() == 'A' || e.getKeyChar() == 'a') {
                    playerOneLeft = true;
                }

                if (e.getKeyChar() == 'J' || e.getKeyChar() == 'j') {
                    playerTwoRight = true;
                }
                if (e.getKeyChar() == 'G' || e.getKeyChar() == 'g') {
                    playerTwoLeft = true;
                }

                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    playerThreeRight = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    playerThreeLeft = true;
                }

                if (e.getKeyChar() == '#') {
                    playerFourRight = true;
                }
                if (e.getKeyChar() == ';') {
                    playerFourLeft = true;
                }

                if (e.getKeyChar() == 'W' || e.getKeyChar() == 'w') {
                    playerOneJump = true;
                }
                if (e.getKeyChar() == 'Y' || e.getKeyChar() == 'y') {
                    playerTwoJump = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    playerThreeJump = true;
                }
                if (e.getKeyCode() == '[') {
                    playerFourJump = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    reset = true;
                }
                if (e.getKeyChar() == '=') {
                    saveBall = true;
                }
                if (e.getKeyChar() == '_' || e.getKeyChar() == '-') {
                    restoreBall = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyChar() == 'D' || e.getKeyChar() == 'd') {
                    playerOneRight = false;
                }
                if (e.getKeyChar() == 'A' || e.getKeyChar() == 'a') {
                    playerOneLeft = false;
                }

                if (e.getKeyChar() == 'J' || e.getKeyChar() == 'j') {
                    playerTwoRight = false;
                }
                if (e.getKeyChar() == 'G' || e.getKeyChar() == 'g') {
                    playerTwoLeft = false;
                }

                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    playerThreeRight = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    playerThreeLeft = false;
                }

                if (e.getKeyChar() == '#') {
                    playerFourRight = false;
                }
                if (e.getKeyChar() == ';') {
                    playerFourLeft = false;
                }

                if (e.getKeyChar() == 'W' || e.getKeyChar() == 'w') {
                    playerOneJump = false;
                }
                if (e.getKeyChar() == 'Y' || e.getKeyChar() == 'y') {
                    playerTwoJump = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    playerThreeJump = false;
                }
                if (e.getKeyCode() == '[') {
                    playerFourJump = false;
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });
        setUndecorated(true);
        setVisible(true);
        pack();
    }
}