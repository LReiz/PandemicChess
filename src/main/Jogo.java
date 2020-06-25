package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;

import graficos.Spritesheet;
import tabuleiro.Tabuleiro;


public class Jogo extends Canvas implements KeyListener, Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1623940135482728576L;
	
	// Estados do Jogo
	public boolean jogoRodando = false;
	
	// Ferramentas
	private Thread thread;
	public static Spritesheet spritesheet;
	private BufferedImage imagemPrincipal;
	public static Random rand = new Random();
	
	// Constantes de Configura��o
	public static int LARGURA = Tabuleiro.DC*20;
	public static int ALTURA = Tabuleiro.DC*20;
	public static int ESCALA = 3;
	
	// Componentes
	private Tabuleiro tabuleiro;
	
	// inicia o jogo
	private Jogo() {
		imagemPrincipal = new BufferedImage(LARGURA, ALTURA, BufferedImage.TYPE_INT_RGB);
		spritesheet = new Spritesheet("/spritesheet.png");
		
		tabuleiro = new Tabuleiro("/mapa1.png");
		
		this.setPreferredSize(new Dimension(LARGURA*ESCALA, ALTURA*ESCALA));
		addKeyListener(this);
		iniciarFrame();
	}
	
	public static void main(String args[]) {
		Jogo jogo = new Jogo();
		jogo.start();
		jogo.stop();
	}
	
	// atualiza informa��es do jogo
	public void att() {
		
	}
	
	// renderiza gr�ficos
	public void renderizar() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(6);
			return;
		}
		
		Graphics g = imagemPrincipal.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, LARGURA, ALTURA);
	
		for(int yy = 0; yy < tabuleiro.alturaMapa; yy++) {
			for(int xx = 0; xx < tabuleiro.larguraMapa; xx++) {
				this.tabuleiro.vetorCelulas[yy][xx].renderizar(g);
			}
		}
		
		for(int yy = 0; yy < tabuleiro.alturaMapa; yy++) {
			for(int xx = 0; xx < tabuleiro.larguraMapa; xx++) {
				if(this.tabuleiro.vetorPecasMoveis[yy][xx] != null)
					this.tabuleiro.vetorPecasMoveis[yy][xx].renderizar(g);
			}
		}
		
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(imagemPrincipal, 0, 0, LARGURA*ESCALA, ALTURA*ESCALA, null);
		
		bs.show();
	}
	
	private void iniciarFrame() {
		JFrame frame = new JFrame("PandemicChess");
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.pack();
		frame.setLocationRelativeTo(null);
	}
	
	private synchronized void start() {
		if(jogoRodando)
			return;
		jogoRodando = true;
		thread = new Thread(this);
		thread.start();
	}
	
	private synchronized void stop() {
		if(!jogoRodando)
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
		// Controle de FPS do Jogo	
		int fps = 60;
		double ns = 1000000000/fps;
		double delta = 0;
		long nanoAnterior = System.nanoTime();
		long nanoAtual;
		
		int numDeFrames = 0;
		long segPassado = System.currentTimeMillis();
		
		while(jogoRodando) {
			nanoAtual = System.nanoTime();
			delta += (nanoAtual - nanoAnterior)/ns;
			nanoAnterior = nanoAtual;
			if(delta >= 1) {
				att();
				renderizar();
				numDeFrames++;
				delta--;
			}
			if(System.currentTimeMillis() - segPassado >= 1000) {
				System.out.println("FPS: " + numDeFrames);
				numDeFrames = 0;
				segPassado += 1000;
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