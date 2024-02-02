package org.francoRobles;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import static org.francoRobles.util.*;

public class SnakePanel extends JPanel implements ActionListener {
    Snake snake = new Snake();
    int applePositionX;
    int applePositionY;
    private Clip collisionSound;
    private Clip gameSound;
    Timer timer;
    Random random;
    boolean running = false;
    //flag to know if snake avanced one position
    boolean avanced = false;

    /**
     * Constructor
     */
    SnakePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        loadMusic();
        startGame();
    }

    /**
     * start the game
     *
     */
    public void startGame(){
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        gameSound();
        timer.start();
    }

    /**
     * reset parameters to restart the game
     */
    public void resetGame(){
        snake.resetSnake();
        timer.restart();
        running = true;
        gameSound();
        newApple();
    }


    /**
     * draw the score and game over text
     * reset the background music
     * @param g
     */
    public void gameOver(Graphics g){
        //score text
        drawScore(g);
        //stop sound
        gameSound.stop();
        gameSound.setFramePosition(0);//reset the clip
        //Game Over text
        g.setColor(Color.red);
        g.setFont( new Font("Ink Free",Font.HANGING_BASELINE, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
    }

    /** .
     * This method is responsible for painting the component's visual representation
     *
     */
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        this.draw(g);
    }
    /** .
     * This method paints the component's visual representation
     *
     */
    public void draw(Graphics g){
        if(running){
            paintBackground(g);
            drawSnake(g);
            drawApple(g);
            drawScore(g);
        }else {
            gameOver(g);
        }
       Toolkit.getDefaultToolkit().sync();

    }

    /**
     * create an apple in a new free position
     */
    public void newApple(){
        boolean invalidPosition = true;
        while(invalidPosition){
            int appleXAux = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
            int appleYAux = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
            if(isFreePosition(appleYAux, appleXAux)){//check if the position is free to use
                applePositionX = appleXAux;
                applePositionY = appleYAux;
                invalidPosition = false;
            }
        }
    }


    public void checkApple(){
        //check if head collides with apple
        if(snake.x[0] == this.applePositionX && snake.y[0]==this.applePositionY){
            eatSound(); //play eat sound
            snake.addEatensApple();
            snake.addBodyPart();
            newApple();
        }
    }

    /**
     * check different  collisions:
     *      -snake with body
     *      -head with window limits
     */
    public void checkCollision(){
        //check if head collides with body
        for(int i = snake.getBodyParts(); i > 0; i--){
            if (snake.x[0]==snake.x[i] && snake.y[0]==snake.y[i]){
                running =  false;
            }
        }
        //check if head collides with bounds
       if(snake.x[0]<0 || snake.y[0]<0 || snake.x[0]> SCREEN_WIDTH-UNIT_SIZE || snake.y[0]>SCREEN_HEIGHT-UNIT_SIZE){
           running =  false;
       }
        if(!running) {
            timer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(this.running){
            checkApple();
            snake.move();
            avanced = true;
            checkCollision();
        }
        repaint();
    }
    /**
     * class to indicate which action occurs according to the key pressed
     */
    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            int ch = e.getKeyCode();
                switch (ch) {
                    case 87 -> { // w
                        if (snake.getDirection() != Direction.Down && avanced) {
                            snake.setDirection(Direction.UP);
                        }
                        ;
                    }
                    case 83 -> { // s
                        if (snake.getDirection() != Direction.UP && avanced) {
                            snake.setDirection(Direction.Down);
                        }
                        ;
                    }
                    case 65 -> { // a
                        if (snake.getDirection() != Direction.Right && avanced) {
                            snake.setDirection(Direction.Left);
                        }
                        ;
                    }
                    case 68 -> {// d
                        if (snake.getDirection() != Direction.Left && avanced) {
                            snake.setDirection(Direction.Right);
                        }
                        ;
                    }
                    case 82 -> resetGame(); // r

            }
            avanced = false;
        }
    }

    /**
     * method for draw apple
     * @param g
     */
    private void drawApple(Graphics g){
        g.setColor(Color.RED);
        g.fillOval(applePositionX, applePositionY, UNIT_SIZE, UNIT_SIZE);
    }
    /**
     * method for draw snake
     * @param g
     */
    private void drawSnake(Graphics g){
        int red = 45;
        int green = 180;
        int blue = 0;

        for (int i = 0; i < snake.getBodyParts(); i++) {
            if (i == 0) {
                g.setColor(Color.green);
                g.fillRect(snake.x[i], snake.y[i], UNIT_SIZE, UNIT_SIZE);
            } else {
                Color bodyColor = new Color(red,green,blue).brighter();
                g.setColor(bodyColor);
                g.fillOval(snake.x[i], snake.y[i], UNIT_SIZE, UNIT_SIZE);
            }
        }
    }

    /**
     * method for draw score
     * @param g
     */
    private void drawScore(Graphics g){
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.PLAIN, 30));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: "+ snake.getApplesEatens(), (SCREEN_WIDTH - metrics1.stringWidth("Score: "+snake.getApplesEatens()))/2, g.getFont().getSize());
    }

    /**
     *
     * Check if the indicated position is not occupied for the snake
     * return false: position is not free to use
     * return true: position is free to use
     */
    private boolean isFreePosition(int appleYAux, int appleXAux){
        for (int i = 0; i < snake.getBodyParts(); i++) {
            if (snake.x[i]==appleXAux && snake.y[i]==appleYAux){
                return false;
            }
        }
        return true;
    }

    /**
     * start the background sound and set the loop
      */
    private void gameSound(){
        gameSound.start();
        gameSound.setLoopPoints(0, gameSound.getFrameLength()-10);
        gameSound.loop(Clip.LOOP_CONTINUOUSLY);
    }

    /**
     *  method for painting the background
      */

    public void paintBackground(Graphics g) {
        int numRows = SCREEN_HEIGHT / UNIT_SIZE;
        int numCols = SCREEN_WIDTH / UNIT_SIZE;

        // Go through each row and column to paint the squares:
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                int x = col * UNIT_SIZE;
                int y = row * UNIT_SIZE;

                // Set the desired color for the squares:
                boolean isBlack = (col + row) % 2 == 0;
                Color color = isBlack ? new Color(55, 164, 11) : new Color(43, 131, 6);

                // Paints the square at the current position:
                g.setColor(color);
                g.fillRect(x, y, UNIT_SIZE, UNIT_SIZE);
            }
        }
    }

    /**
     * loads audio files and saves them to collisionSound and gameSound
     */
    private void loadMusic(){
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("C:\\Users\\franco\\Documents\\javaIntellij\\proyectos\\SnakeGame\\src\\main\\java\\org\\francoRobles\\music\\eat.wav"));
            collisionSound = AudioSystem.getClip();
            collisionSound.open(audioInputStream);

            AudioInputStream audioInputStreamBack = AudioSystem.getAudioInputStream(new File("C:\\Users\\franco\\Documents\\javaIntellij\\proyectos\\SnakeGame\\src\\main\\java\\org\\francoRobles\\music\\audio.wav"));
            gameSound = AudioSystem.getClip();
            gameSound.open(audioInputStreamBack);
        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    private void eatSound(){
        // play sound
        collisionSound.start();
        //Reset sound clip position for next playback
        collisionSound.setFramePosition(0);
    }
}
