package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BreakoutPanel extends JPanel implements Runnable {

    Thread gameLoop;
    static final Color[] colors = {Color.RED, new Color(255, 144, 0), Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, new Color(138, 43, 226)};
    static final int PADDLE_Y = 450, BALL_SIZE = 10, PADDLE_WIDTH = 70, PADDLE_HEIGHT = 10, BRICK_WIDTH = 70, BRICK_HEIGHT = 10;
    static int paddleX, ballX, ballY, score, heading, gameOver;
    static int[] ballVector = new int[2];
    static Rectangle[][] bricks = new Rectangle[7][10];
    static boolean loop, contact;
    static int[][] grid = new int[7][10];

    public BreakoutPanel() {

        reset();
        drawGameScreen();

        InputMap[] inputMaps = new InputMap[]{
                this.getInputMap(JComponent.WHEN_FOCUSED),
                this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT),
                this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW),
        };
        for (InputMap i : inputMaps) {
            i.put(KeyStroke.getKeyStroke("LEFT"), "left");
            i.put(KeyStroke.getKeyStroke("RIGHT"), "right");
            i.put(KeyStroke.getKeyStroke("released LEFT"), "release left");
            i.put(KeyStroke.getKeyStroke("released RIGHT"), "release right");
            i.put(KeyStroke.getKeyStroke("SPACE"), "reset");
        }
        this.getActionMap().put("left", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                heading = 1;
            }
        });
        this.getActionMap().put("right", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                heading = 2;
            }
        });
        this.getActionMap().put("release left", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                heading = 0;
            }
        });
        this.getActionMap().put("release right", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                heading = 0;
            }
        });
        this.getActionMap().put("reset", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                reset();
                repaint();
            }
        });
        if (loop) {
            gameLoop = new Thread(this);
            gameLoop.start();
            loop = false;
        }
    }

    public void drawGameScreen() {

        this.removeAll();
        this.setPreferredSize(new Dimension(700, 520));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
    }

    public void reset() {

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = 1;
                Rectangle brick = new Rectangle();
                brick.setBounds(BRICK_WIDTH * j, BRICK_HEIGHT * i + 40, BRICK_WIDTH, BRICK_HEIGHT);
                bricks[i][j] = brick;
            }
        }
        loop = true;
        contact = false;
        gameOver = 0;
        score = 0;
        heading = 0;
        paddleX = 0;
        ballX = 0;
        ballY = 120;
        ballVector[0] = 5;
        ballVector[1] = 5;
    }

    public void drawPaddle(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillRect(paddleX, PADDLE_Y, PADDLE_WIDTH, PADDLE_HEIGHT);
    }

    public void drawBall(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillOval(ballX, ballY, BALL_SIZE, BALL_SIZE);
    }

    public void drawBricks(Graphics2D g) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 1) {
                    g.setColor(colors[i]);
                    g.fillRect(BRICK_WIDTH * j, BRICK_HEIGHT * i + 40, BRICK_WIDTH, BRICK_HEIGHT);
                    g.setColor(Color.BLACK);
                    g.drawRect(BRICK_WIDTH * j, BRICK_HEIGHT * i + 40, BRICK_WIDTH, BRICK_HEIGHT);
                }
            }
        }
    }

    public void drawScore(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 500, 700, 20);
        String scoreString = "Score: " + score * 10;
        g.setColor(Color.BLACK);
        g.setFont(new Font("Times New Roman", Font.BOLD, 19));
        g.drawString(scoreString, 10, 516);
    }

    public void step() {

        if ((ballX + BALL_SIZE == paddleX && ((ballY + BALL_SIZE >= PADDLE_Y && ballY + BALL_SIZE <= PADDLE_Y + PADDLE_HEIGHT) || (ballY >= PADDLE_Y && ballY <= PADDLE_Y + PADDLE_HEIGHT))) || (ballX == paddleX + PADDLE_WIDTH && ((ballY + BALL_SIZE >= PADDLE_Y && ballY + BALL_SIZE <= PADDLE_Y + PADDLE_HEIGHT) || (ballY >= PADDLE_Y && ballY <= PADDLE_Y + PADDLE_HEIGHT)))) {
            ballVector[0] *= -1;
            contact = true;
        }
        if ((ballY + BALL_SIZE == PADDLE_Y && ((ballX + BALL_SIZE >= paddleX && ballX + BALL_SIZE <= paddleX + PADDLE_WIDTH) || (ballX >= paddleX && ballX <= paddleX + PADDLE_WIDTH))) || (ballY == PADDLE_Y + PADDLE_HEIGHT && ((ballX + BALL_SIZE >= paddleX && ballX + BALL_SIZE <= paddleX + PADDLE_WIDTH) || (ballX >= paddleX && ballX <= paddleX + PADDLE_WIDTH)))) {
            ballVector[1] *= -1;
            contact = true;
        }
        if (contact) {
            if (Math.abs(ballVector[0]) < 60) {
                if (heading == 1) {
                    ballVector[0] -= 5;
                } else if (heading == 2) {
                    ballVector[0] += 5;
                }
            }
            contact = false;
            return;
        }
        if (ballY <= 0) {
            ballVector[1] *= -1;
            contact = true;
        }
        if (ballX <= 0 || ballX + BALL_SIZE >= 700) {
            ballVector[0] *= -1;
            contact = true;
        }
        if (contact) {
            contact = false;
            return;
        }
        if (ballY + BALL_SIZE >= 500) {
            gameOver = 2;
        } else {
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    if (bricks[i][j] != null) {
                        if ((ballX + BALL_SIZE == bricks[i][j].getX() && ((ballY + BALL_SIZE >= bricks[i][j].getY() && ballY + BALL_SIZE <= bricks[i][j].getY() + BRICK_HEIGHT) || (ballY >= bricks[i][j].getY() && ballY <= bricks[i][j].getY() + BRICK_HEIGHT))) || (ballX == bricks[i][j].getX() + BRICK_WIDTH && ((ballY + BALL_SIZE >= bricks[i][j].getY() && ballY + BALL_SIZE <= bricks[i][j].getY() + BRICK_HEIGHT) || (ballY >= bricks[i][j].getY() && ballY <= bricks[i][j].getY() + BRICK_HEIGHT)))) {
                            System.out.println("x");
                            ballVector[0] *= -1;
                            contact = true;
//                            if ((ballY == bricks[i][j].getY() && (ballX + ballSize == bricks[i][j].getX() || ballX == bricks[i][j].getX() + brickWidth)) || (ballY + ballSize == bricks[i][j].getY() + brickHeight) && (ballX + ballSize == bricks[i][j].getX() || ballX == bricks[i][j].getX() + brickWidth)) {
//                                System.out.println("extra x");
//                                ballVector[1] *= -1;
//                            }
                        }
                        if ((ballY + BALL_SIZE == bricks[i][j].getY() && ((ballX + BALL_SIZE >= bricks[i][j].getX() && ballX + BALL_SIZE <= bricks[i][j].getX() + BRICK_WIDTH) || (ballX >= bricks[i][j].getX() && ballX <= bricks[i][j].getX() + BRICK_WIDTH))) || (ballY == bricks[i][j].getY() + BRICK_HEIGHT && ((ballX + BALL_SIZE >= bricks[i][j].getX() && ballX + BALL_SIZE <= bricks[i][j].getX() + BRICK_WIDTH) || (ballX >= bricks[i][j].getX() && ballX <= bricks[i][j].getX() + BRICK_WIDTH)))) {
                            System.out.println("y");
                            ballVector[1] *= -1;
                            contact = true;
//                            if ((ballX == bricks[i][j].getX() && ((ballY + ballSize == bricks[i][j].getY()) || (ballY == bricks[i][j].getY() + brickHeight)) || (ballX + ballSize == bricks[i][j].getX() + brickWidth && ((ballY + ballSize == bricks[i][j].getY()) || (ballY == bricks[i][j].getY() + brickHeight))))) {
//                                System.out.println("extra y");
//                                ballVector[0] *= -1;
//                            }
                        }
                        if (contact) {
                            System.out.println("");
                            score++;
                            bricks[i][j] = null;
                            grid[i][j] = 0;
                            contact = false;
                            return;
                        }
                    }
                }
            }
        }
    }

    public void isGridEmpty() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 1) {
                    return;
                }
            }
        }
        gameOver = 1;
    }

    public void drawEndScreen(Graphics2D g) {
        removeAll();
        g.setColor(Color.WHITE);
        g.setFont(new Font("Times New Roman", Font.BOLD, 60));
        String gameOver = "Game Over";
        g.drawString(gameOver, 200, 250);
    }

    @Override
    protected void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (gameOver != 0) {
            drawEndScreen(g);
        } else {
            drawPaddle(g);
            drawBall(g);
            drawBricks(g);
            drawScore(g);
        }
    }

    @Override
    public void run() {
        while (true) {
            if (heading == 1 && paddleX - 10 >= 0) {
                paddleX -= 10;
            } else if (heading == 2 && paddleX + PADDLE_WIDTH + 10 <= 700) {
                paddleX += 10;
            }
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ballX += ballVector[0];
            ballY += ballVector[1];

            step();
            isGridEmpty();
            repaint();
        }
    }
}
