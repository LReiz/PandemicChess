package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;

import entidades.PecasMoveis;
import erros.ForaDeAlcance;
import erros.MuitoDistante;
import erros.NaoVazio;
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
	
	// Constantes de Configuração
	public static int LARGURA = Tabuleiro.DC*17;
	public static int ALTURA = Tabuleiro.DC*17;
	public static int ESCALA = 3;
	
	// Componentes
	public static Tabuleiro tabuleiro;
	public static ArrayList<PecasMoveis> entidadesMedicos;
	public static ArrayList<PecasMoveis> entidadesInfectados;
	
	// inicia o jogo
	private Jogo() {
		imagemPrincipal = new BufferedImage(LARGURA, ALTURA, BufferedImage.TYPE_INT_RGB);
		spritesheet = new Spritesheet("/spritesheet.png");
		
		entidadesMedicos = new ArrayList<PecasMoveis>();
		entidadesInfectados = new ArrayList<PecasMoveis>();
		tabuleiro = new Tabuleiro("/mapa1.png");
		PecasMoveis.medicoAtual = entidadesMedicos.get(0);
		PecasMoveis.infectadoAtual = entidadesInfectados.get(0);
		
		this.setPreferredSize(new Dimension(LARGURA*ESCALA, ALTURA*ESCALA));
		addKeyListener(this);
		iniciarFrame();
	}
	
	public static void main(String args[]) {
		Jogo jogo = new Jogo();
		jogo.start();
		jogo.stop();
	}
	
	// atualiza informações do jogo
	public void att() throws NaoVazio, ForaDeAlcance, MuitoDistante {
		for(int i = 0; i < entidadesMedicos.size(); i++) {
			entidadesMedicos.get(i).att();
		}
		
		for(int i = 0; i < entidadesInfectados.size(); i++) {
			entidadesInfectados.get(i).att();
		}
	}
	
	// renderiza gráficos
	public void renderizar() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = imagemPrincipal.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, LARGURA, ALTURA);
	
		// Renderização dos elementos do tabuleiro
		for(int yy = 0; yy < tabuleiro.alturaMapa; yy++) {
			for(int xx = 0; xx < tabuleiro.larguraMapa; xx++) {
				tabuleiro.vetorCelulas[yy][xx].renderizar(g);
			}
		}
		
		for(int yy = 0; yy < tabuleiro.alturaMapa; yy++) {
			for(int xx = 0; xx < tabuleiro.larguraMapa; xx++) {
				if(tabuleiro.vetorPecasMoveis[yy][xx] != null)
					tabuleiro.vetorPecasMoveis[yy][xx].renderizar(g);
			}
		}
		
		if(PecasMoveis.medicoSelecionado) {
			g.setColor(new Color(0xFFFF0000));			// vermelho (indica que a peça está selecionada)
		} else {
			g.setColor(new Color(0xFFFFAA00));			// amarelo (indica que a peça não está selecionada ainda)
		}
		g.drawRect(entidadesMedicos.get(PecasMoveis.indexMedico).pos[1], entidadesMedicos.get(PecasMoveis.indexMedico).pos[0], Tabuleiro.DC, Tabuleiro.DC);
		
		if(PecasMoveis.infectadoSelecionado) {
			g.setColor(new Color(0xFFFF0000));			// vermelho
		} else {
			g.setColor(new Color(0xFFFFAA00));			// amarelo
		}
		g.drawRect(entidadesInfectados.get(PecasMoveis.indexInfectado).pos[1], entidadesInfectados.get(PecasMoveis.indexInfectado).pos[0], Tabuleiro.DC, Tabuleiro.DC);
		/////
		
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
				try {
					att();
				} catch (NaoVazio | ForaDeAlcance | MuitoDistante e) {
					e.printStackTrace();
				}
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
	public void keyPressed(KeyEvent key) {
		// TODO Auto-generated method stub

		if(Tabuleiro.vezJogador == 1) {
			if(!PecasMoveis.medicoSelecionado) {
				if(key.getKeyCode() == KeyEvent.VK_ENTER) {
					PecasMoveis.medicoSelecionado = true;
				} else if(key.getKeyCode() == KeyEvent.VK_J) {
					PecasMoveis.medicoAtual = PecasMoveis.mudarSelecaoDePeca(entidadesMedicos, 1, -1);			
				} else if(key.getKeyCode() == KeyEvent.VK_L) {
					PecasMoveis.medicoAtual = PecasMoveis.mudarSelecaoDePeca(entidadesMedicos, 1, 1);
				}
			} else if(!PecasMoveis.medicoAtual.movendo){
				if(key.getKeyCode() == KeyEvent.VK_ENTER) {
					PecasMoveis.medicoSelecionado = false;
				} else if(key.getKeyCode() == KeyEvent.VK_J) {
					PecasMoveis.medicoAtualDirX = -1;
					PecasMoveis.medicoAtualDirY = 0;
					PecasMoveis.proxPosicaoMedicoX = PecasMoveis.medicoAtual.pos[1] + (PecasMoveis.medicoAtualDirX*Tabuleiro.DC);
					PecasMoveis.proxPosicaoMedicoY = PecasMoveis.medicoAtual.pos[0] + (PecasMoveis.medicoAtualDirY*Tabuleiro.DC);
					PecasMoveis.medicoAtual.movendo = true;
				} else if(key.getKeyCode() == KeyEvent.VK_L) {
					PecasMoveis.medicoAtualDirX = 1;
					PecasMoveis.medicoAtualDirY = 0;
					PecasMoveis.proxPosicaoMedicoX = PecasMoveis.medicoAtual.pos[1] + (PecasMoveis.medicoAtualDirX*Tabuleiro.DC);
					PecasMoveis.proxPosicaoMedicoY = PecasMoveis.medicoAtual.pos[0] + (PecasMoveis.medicoAtualDirY*Tabuleiro.DC);
					PecasMoveis.medicoAtual.movendo = true;
				} else if(key.getKeyCode() == KeyEvent.VK_I) {
					System.out.println(PecasMoveis.medicoAtual);
					PecasMoveis.medicoAtualDirY = -1;
					PecasMoveis.medicoAtualDirX = 0;
					PecasMoveis.proxPosicaoMedicoX = PecasMoveis.medicoAtual.pos[1] + (PecasMoveis.medicoAtualDirX*Tabuleiro.DC);
					PecasMoveis.proxPosicaoMedicoY = PecasMoveis.medicoAtual.pos[0] + (PecasMoveis.medicoAtualDirY*Tabuleiro.DC);
					PecasMoveis.medicoAtual.movendo = true;
				} else if(key.getKeyCode() == KeyEvent.VK_K) {
					PecasMoveis.medicoAtualDirY = 1;
					PecasMoveis.medicoAtualDirX = 0;
					PecasMoveis.proxPosicaoMedicoX = PecasMoveis.medicoAtual.pos[1] + (PecasMoveis.medicoAtualDirX*Tabuleiro.DC);
					PecasMoveis.proxPosicaoMedicoY = PecasMoveis.medicoAtual.pos[0] + (PecasMoveis.medicoAtualDirY*Tabuleiro.DC);
					PecasMoveis.medicoAtual.movendo = true;
				}
			}
		}
		
		else if(Tabuleiro.vezJogador == 2) {
			if(!PecasMoveis.infectadoSelecionado) {
				if(key.getKeyCode() == KeyEvent.VK_R) {
					PecasMoveis.infectadoSelecionado = true;
				} else if(key.getKeyCode() == KeyEvent.VK_A) {
					PecasMoveis.infectadoAtual = PecasMoveis.mudarSelecaoDePeca(entidadesInfectados, 2, -1);			
				} else if(key.getKeyCode() == KeyEvent.VK_D) {
					PecasMoveis.infectadoAtual = PecasMoveis.mudarSelecaoDePeca(entidadesInfectados, 2, 1);
				}
			} else if(!PecasMoveis.infectadoAtual.movendo){
				if(key.getKeyCode() == KeyEvent.VK_R) {
					PecasMoveis.infectadoSelecionado = false;
				} else if(key.getKeyCode() == KeyEvent.VK_A) {
					PecasMoveis.infectadoAtualDirX = -1;
					PecasMoveis.infectadoAtualDirY = 0;
					PecasMoveis.proxPosicaoInfectadoX = PecasMoveis.infectadoAtual.pos[1] + (PecasMoveis.infectadoAtualDirX*Tabuleiro.DC);
					PecasMoveis.proxPosicaoInfectadoY = PecasMoveis.infectadoAtual.pos[0] + (PecasMoveis.infectadoAtualDirY*Tabuleiro.DC);
					PecasMoveis.infectadoAtual.movendo = true;
				} else if(key.getKeyCode() == KeyEvent.VK_D) {
					PecasMoveis.infectadoAtualDirX = 1;
					PecasMoveis.infectadoAtualDirY = 0;
					PecasMoveis.proxPosicaoInfectadoX = PecasMoveis.infectadoAtual.pos[1] + (PecasMoveis.infectadoAtualDirX*Tabuleiro.DC);
					PecasMoveis.proxPosicaoInfectadoY = PecasMoveis.infectadoAtual.pos[0] + (PecasMoveis.infectadoAtualDirY*Tabuleiro.DC);
					PecasMoveis.infectadoAtual.movendo = true;
				} else if(key.getKeyCode() == KeyEvent.VK_W) {
					PecasMoveis.infectadoAtualDirY = -1;
					PecasMoveis.infectadoAtualDirX = 0;
					PecasMoveis.proxPosicaoInfectadoX = PecasMoveis.infectadoAtual.pos[1] + (PecasMoveis.infectadoAtualDirX*Tabuleiro.DC);
					PecasMoveis.proxPosicaoInfectadoY = PecasMoveis.infectadoAtual.pos[0] + (PecasMoveis.infectadoAtualDirY*Tabuleiro.DC);
					PecasMoveis.infectadoAtual.movendo = true;
				} else if(key.getKeyCode() == KeyEvent.VK_S) {
					PecasMoveis.infectadoAtualDirY = 1;
					PecasMoveis.infectadoAtualDirX = 0;
					PecasMoveis.proxPosicaoInfectadoX = PecasMoveis.infectadoAtual.pos[1] + (PecasMoveis.infectadoAtualDirX*Tabuleiro.DC);
					PecasMoveis.proxPosicaoInfectadoY = PecasMoveis.infectadoAtual.pos[0] + (PecasMoveis.infectadoAtualDirY*Tabuleiro.DC);
					PecasMoveis.infectadoAtual.movendo = true;
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent key) {
		// TODO Auto-generated method stub
	}

	
}
