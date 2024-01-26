package org.francoRobles;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
public class SnakePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/(UNIT_SIZE*UNIT_SIZE);
    static final int DELAY = 50;
    int x[] = new int[GAME_UNITS];
    int y[] = new int[GAME_UNITS];
    int bodyParts = 2;
    int applesEatens=0;
    int appleX;
    int appleY;
    Direction direction = Direction.Right;
    Timer timer;
    boolean running = false;
    Random random;


    //methods
    SnakePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }


    public void startGame(){
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void resetGame(){
        x =  new int[GAME_UNITS];
        y =  new int[GAME_UNITS];
        bodyParts = 15;
        applesEatens=0;
        direction = Direction.Right;
        timer.restart();
        running = true;
        newApple();

    }

    public void gameOver(Graphics g){
        //score text
        drawScore(g);
        //Game Over text
        g.setColor(Color.red);
        g.setFont( new Font("Ink Free",Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        this.draw(g);
    }

    public void draw(Graphics g){
        if(running){
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_HEIGHT, i * UNIT_SIZE);
            }
            drawApple(g);
            drawSnake(g);
            drawScore(g);
        }else {
            gameOver(g);
        }


    }

    public void newApple(){
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
    }

    public void move(){
        for (int i = this.bodyParts; i > 0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        switch (direction){
            case UP -> y[0] = y[0]-UNIT_SIZE;
            case Down -> y[0] = y[0]+UNIT_SIZE;
            case Left -> x[0] = x[0]-UNIT_SIZE;
            case Right -> x[0] = x[0]+UNIT_SIZE;
        }

    }

    public void checkApple(){
        //check if head collides with apple
        if(x[0] == this.appleX && y[0]==this.appleY){
            applesEatens+=1;
            bodyParts+=1;
            newApple();
        }
    }

    public void checkCollision(){
        //check if head collides with body
        for(int i = this.bodyParts; i > 0; i--){
            if (x[0]==x[i] && y[0]==y[i]){
                running =  false;
            }
        }
        //check if head collides with bounds
        if(x[0]<0 || y[0]<0 || x[0]> SCREEN_WIDTH || y[0]>SCREEN_HEIGHT){
            running =  false;
        }
        if(!running) {
            timer.stop();
        }

    }



    @Override
    public void actionPerformed(ActionEvent e) {
        if(this.running){
            move();
            checkApple();
            checkCollision();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            char ch = e.getKeyChar();
            switch (ch){
                case 'w' -> {
                    if(direction != Direction.Down){
                        direction = Direction.UP;
                    } ;
                }
                case  's' -> {
                    if(direction != Direction.UP){
                        direction = Direction.Down;
                    } ;
                }
                case  'a' -> {
                    if(direction != Direction.Right){
                        direction = Direction.Left;
                    } ;
                }
                case  'd' -> {
                    if(direction != Direction.Left){
                        direction = Direction.Right;
                    } ;
                }
                case 'h' -> resetGame();

            }
        }
    }

    //aux functions
    private void drawApple(Graphics g){
        g.setColor(Color.RED);
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
    }

    private void drawSnake(Graphics g){
        for (int i = 0; i < this.bodyParts; i++) {
            if (i == 0) {
                g.setColor(Color.GREEN);
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            } else {
                g.setColor(new Color(45, 180, 0));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
        }
    }

    private void drawScore(Graphics g){
        g.setColor(Color.red);
        g.setFont( new Font("Ink Free",Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: "+ applesEatens, (SCREEN_WIDTH - metrics1.stringWidth("Score: "+applesEatens))/2, g.getFont().getSize());
    }

}
