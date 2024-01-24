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
    final int x[] =  new int[GAME_UNITS];
    final int y[] =  new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEatens;
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

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        this.draw(g);
    }

    public void draw(Graphics g){
        for(int i = 0; i<SCREEN_HEIGHT/UNIT_SIZE; i++){
            g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
            g.drawLine( 0,i*UNIT_SIZE, SCREEN_HEIGHT, i*UNIT_SIZE);
        }
        g.setColor(Color.RED);
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

        for (int i = 0; i < this.bodyParts; i++) {
            if (i==0){
                g.setColor(Color.GREEN);
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }else{
                g.setColor(new Color(45, 180, 0));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }
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

    }

    public void checkCollision(){
        //check if head collides with body
        for(int i = this.bodyParts; i < 0; i--){
            if (x[0]==x[i] && y[0]==y[i]){
                running =  false;
            }
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
                case  'w' -> direction = Direction.UP;
                case  's' -> direction = Direction.Down;
                case  'a' -> direction = Direction.Left;
                case  'd' -> direction = Direction.Right;
            }
        }
    }


}
