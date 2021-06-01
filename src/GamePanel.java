import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.*;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_HEIGHT*SCREEN_WIDTH)/UNIT_SIZE;
    static final int DELAY = 75;
    final int[] x = new int[GAME_UNITS]; // x coordinates of body parts
    final int[] y = new int[GAME_UNITS]; // y coordinates of body parts
    int bodyParts = 6; // initial number of body parts
    int applesEaten;
    int appleX; // x coordinate of the apple that appears in the screen
    int appleY; // y coordinate
    int pointMultiplier = 1;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;
    boolean hasOverlap; // TODO so apples don't appear underneath snake
    JButton playAgain = new JButton("Play again"); // TODO
    File file = new File("appleCrunch.wav");
    AudioInputStream audioStream;
    { try { audioStream = AudioSystem.getAudioInputStream(file); }
        catch (UnsupportedAudioFileException | IOException e) { e.printStackTrace(); } }
    Clip clip;
    { try { clip = AudioSystem.getClip(); }
        catch (LineUnavailableException e) { e.printStackTrace(); } }



    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(new Color(0, 200, 200));
        this.setFocusable(true);
        this.addKeyListener(new myKeyAdapter());
        playAgain.setBounds(0, 0, 0, 0); // TODO
        playAgain.setFont(new Font("Avenir", Font.PLAIN, 40)); // TODO
        playAgain.setForeground(Color.magenta); // TODO
        playAgain.setBackground(new Color(136, 26, 201)); // TODO
        this.add(playAgain); // TODO
        playAgain.setVisible(false); // TODO

        // TODO
        playAgain.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("New game");
            }
        });
        startGame();
    }

    // TODO
    public void resetGame() {


        final int[] x = new int[GAME_UNITS]; // x coordinates of body parts
        final int[] y = new int[GAME_UNITS]; // y coordinates of body parts
        int bodyParts = 6; // initial number of body parts
        int applesEaten;
    }

    public void startGame() {
        newApple();
        running = true;
        try { clip.open(audioStream); }
        catch (LineUnavailableException | IOException e) { e.printStackTrace(); }
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
          /*  // draw grid:
            for (int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE; i++) {
                g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
            }*/

            switch (pointMultiplier) {
                case 1 -> g.setColor(new Color(255, 60, 90));
                case 2 -> g.setColor(new Color(114, 198, 18));
                case 4 -> g.setColor(new Color(255, 116, 223));
                //case 4 -> g.setColor(new Color(239, 222, 38));
            }

            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // drawing snake:
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                }
                else {
                    g.setColor(new Color(45, 180, 0));
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            g.setColor(new Color(136, 26, 201));
            g.setFont(new Font("Avenir", Font.PLAIN, 20));
            g.drawString("Points: " + applesEaten, 5, 25);
        }
        else gameOver(g);
    }

    public void newApple () {
        pointMultiplier = 1;
        // TODO
        hasOverlap = true;
        while (hasOverlap) {
            appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
            appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;

            for (int i = bodyParts; i > 0; i--) {
                if (appleX == x[i] && appleY == y[i]) {
                    hasOverlap = true;
                    break;
                }
            }
            hasOverlap = false;
            System.out.println("apple not under snake");
        }

        // TODO
        if ((appleX <= 2*UNIT_SIZE || appleX >= SCREEN_WIDTH - 2*UNIT_SIZE) &&
                (appleY <= 2*UNIT_SIZE || appleY >= SCREEN_HEIGHT - 2*UNIT_SIZE))
            pointMultiplier += 3;
        else if (appleX == SCREEN_WIDTH - UNIT_SIZE || appleX == 0) {
            pointMultiplier++;
            if (appleY >= SCREEN_HEIGHT - UNIT_SIZE || appleY == 0) pointMultiplier++;
        }

        else if (appleY >= SCREEN_HEIGHT - UNIT_SIZE || appleY == 0) pointMultiplier++;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch (direction) {
            case 'U' :
                y[0] -= UNIT_SIZE;
                break;
            case 'D' :
                y[0] += UNIT_SIZE;
                break;
            case 'L' :
                x[0] -= UNIT_SIZE;
                break;
            case 'R' :
                x[0] += UNIT_SIZE;
                break;
        }
    }

    public void checkApple () {
        if (x[0] == appleX && y[0] == appleY) {
            clip.setMicrosecondPosition(0);
            bodyParts++;
            applesEaten += pointMultiplier;
            clip.start();
            newApple();
        }
    }

    public void checkCollision() {
        // collision with body
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
                break;
            }
        }
        //collision with borders
        if (x[0] < 0 || x[0] > SCREEN_WIDTH || y[0] < 0 || y[0] > SCREEN_HEIGHT)
            running = false;

        if (!running) timer.stop();
    }

    public void gameOver (Graphics g) {
        g.setColor(new Color(136, 26, 201));
        g.setFont(new Font("Avenir", Font.PLAIN, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Points: " + applesEaten,
                (SCREEN_WIDTH - metrics.stringWidth("Points: " + applesEaten))/2, SCREEN_HEIGHT/2 + g.getFont().getSize() + 10);

        g.setColor(Color.magenta);
        g.setFont(new Font("Avenir", Font.PLAIN, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics1.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);

        playAgain.setVisible(true); // TODO
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollision();
        }
        repaint();
    }

    public class myKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R')
                        direction = 'L';
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L')
                        direction = 'R';
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D')
                        direction = 'U';
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U')
                        direction = 'D';
                    break;
            }
        }
    }
}
