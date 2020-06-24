package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;


public class Game extends Canvas implements KeyListener, Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1623940135482728576L;

	public int TS = 16;
	
	private Thread thread;
	public boolean isRunning = true;
	
	private BufferedImage mainImage;
	
	private int WIDTH = TS*20;
	private int HEIGHT = TS*20;
	private int SCALE = 2;
	
	private Game() {
		mainImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		this.setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		addKeyListener(this);
		initFrame();
	}
	
	public static void main(String args[]) {
		Game game = new Game();
		game.start();
		game.stop();
	}
	
	public void tick() {
		
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(6);
			return;
		}
		
		Graphics g = mainImage.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
	
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(mainImage, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
		
		bs.show();
	}
	
	private void initFrame() {
		JFrame frame = new JFrame("PandemicChess");
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.pack();
		frame.setLocationRelativeTo(null);
	}
	
	private synchronized void start() {
		if(isRunning)
			return;
		isRunning = true;
		thread = new Thread(this);
		thread.start();
	}
	
	private synchronized void stop() {
		if(!isRunning)
			return;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		requestFocus();
		// Game FPS control	
		int fps = 60;
		double ns = 1000000000/fps;
		double delta = 0;
		long pastNano = System.nanoTime();
		long currentNano;
		
		int numOfFrames = 0;
		long pastSec = System.currentTimeMillis();
		
		while(isRunning) {
			currentNano = System.nanoTime();
			delta += (currentNano - pastNano)/ns;
			pastNano = currentNano;
			if(delta >= 1) {
				tick();
				render();
				numOfFrames++;
				delta--;
			}
			if(System.currentTimeMillis() - pastSec >= 1000) {
				System.out.println("FPS: " + numOfFrames);
				numOfFrames = 0;
				pastSec += 1000;
			}
		}
		
	}
	
	// CONTROLE DO TECLADO/MOUSE
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
}
