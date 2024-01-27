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
    int appleX;
    int appleY;
    private Clip sonidoColision;
    private Clip gameSound;
    Timer timer;
    Random random;
    boolean running = false;


    //methods
    SnakePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("C:\\Users\\franco\\Documents\\javaIntellij\\proyectos\\SnakeGame\\src\\main\\java\\org\\francoRobles\\music\\eat.wav"));
            sonidoColision = AudioSystem.getClip();
            sonidoColision.open(audioInputStream);

            AudioInputStream audioInputStreamBack = AudioSystem.getAudioInputStream(new File("C:\\Users\\franco\\Documents\\javaIntellij\\proyectos\\SnakeGame\\src\\main\\java\\org\\francoRobles\\music\\audio.wav"));
            gameSound = AudioSystem.getClip();
            gameSound.open(audioInputStreamBack);
        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        startGame();
    }


    public void startGame(){
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        gameSound();
        timer.start();
    }

    public void resetGame(){
        snake.resetSnake();
        timer.restart();
        running = true;
        gameSound();
        newApple();
    }

    public void gameOver(Graphics g){
        //score text
        drawScore(g);
        //stop sound
        gameSound.stop();
        gameSound.setFramePosition(0);
        //Game Over text
        g.setColor(Color.red);
        g.setFont( new Font("Ink Free",Font.HANGING_BASELINE, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        this.draw(g);
    }

    public void draw(Graphics g){
        if(running){
            paintBackground(g);
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.setColor(new Color(197, 1, 226).darker());
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_HEIGHT, i * UNIT_SIZE);
            }
            drawSnake(g);
            drawApple(g);
            drawScore(g);
        }else {
            gameOver(g);
        }
       Toolkit.getDefaultToolkit().sync();

    }

    public void newApple(){
        boolean invalidPosition = true;
        while(invalidPosition){
            int appleXAux = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
            int appleYAux = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
            if(isFreePosition(appleYAux, appleXAux)){
                appleX = appleXAux;
                appleY = appleYAux;
                invalidPosition = false;
            }
        }


    }

    public void move(){
        for (int i = snake.getBodyParts(); i > 0; i--) {
            snake.x[i] = snake.x[i-1];
            snake.y[i] = snake.y[i-1];
        }
        switch (snake.getDirection()){
            case UP -> snake.y[0] = snake.y[0]-UNIT_SIZE;
            case Down -> snake.y[0] = snake.y[0]+UNIT_SIZE;
            case Left -> snake.x[0] = snake.x[0]-UNIT_SIZE;
            case Right -> snake.x[0]= snake.x[0]+UNIT_SIZE;
        }

    }

    public void checkApple(){
        //check if head collides with apple
        if(snake.x[0] == this.appleX && snake.y[0]==this.appleY){
            // Reproducir el sonido
            sonidoColision.start();
            //Reiniciar la posición del clip de sonido para la próxima reproducción
            sonidoColision.setFramePosition(0);
            snake.addEatensApple();
            snake.addBodyPart();
            newApple();

        }
    }

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
            move();
            checkCollision();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            int ch = e.getKeyCode();
            switch (ch){
                case 87 -> {
                    if(snake.getDirection() != Direction.Down){
                        snake.setDirection(Direction.UP);
                    } ;
                }
                case  83 -> {
                    if(snake.getDirection() != Direction.UP){
                        snake.setDirection(Direction.Down);
                    } ;
                }
                case  65 -> {
                    if(snake.getDirection() != Direction.Right){
                        snake.setDirection(Direction.Left);
                    } ;
                }
                case  68 -> {
                    if(snake.getDirection() != Direction.Left){
                        snake.setDirection(Direction.Right);
                    } ;
                }
                case 72 -> resetGame();

            }
        }
    }

    //aux functions
    private void drawApple(Graphics g){
        g.setColor(Color.RED);
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
    }

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

    private void drawScore(Graphics g){
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.PLAIN, 30));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: "+ snake.getApplesEatens(), (SCREEN_WIDTH - metrics1.stringWidth("Score: "+snake.getApplesEatens()))/2, g.getFont().getSize());
    }

    private boolean isFreePosition(int appleYAux, int appleXAux){
        for (int i = 0; i < snake.getBodyParts(); i++) {
            if (snake.x[i]==appleXAux && snake.y[i]==appleYAux){
                return false;
            }
        }
        return true;
    }

    private void gameSound(){
        gameSound.start();
        gameSound.setLoopPoints(0, gameSound.getFrameLength()-10);
        gameSound.loop(Clip.LOOP_CONTINUOUSLY);
    }
    // Método para pintar el fondo:
    public void paintBackground(Graphics g) {
        int numRows = SCREEN_HEIGHT / UNIT_SIZE;
        int numCols = SCREEN_WIDTH / UNIT_SIZE;

        // Recorre cada fila y columna para pintar los cuadrados:
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                int x = col * UNIT_SIZE;
                int y = row * UNIT_SIZE;

                // Establece el color deseado para los cuadrados:
                boolean isBlack = (col + row) % 2 == 0;
                Color color = isBlack ? new Color(54, 157, 12) : new Color(43, 131, 6);

                // Pinta el cuadrado en la posición actual:
                g.setColor(color);
                g.fillRect(x, y, UNIT_SIZE, UNIT_SIZE);
            }
        }
    }
}
