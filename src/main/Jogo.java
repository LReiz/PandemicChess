package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import javax.swing.JFrame;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import database.ConjuntoBau;
import database.ConjuntoPeca;
import database.DocCha;
import database.Firebase;
import database.Time;
import entidades.PecasMoveis;
import erros.BauVazio;
import erros.ChaNaoColetado;
import erros.ForaDeAlcance;
import erros.MuitoDistante;
import erros.NaoVazio;
import graficos.InterfaceFinal;
import graficos.InterfaceInicial;
import graficos.Spritesheet;
import tabuleiro.Tabuleiro;



public class Jogo extends Canvas implements KeyListener, Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1623940135482728576L;
	
	// Estados do Jogo
	public boolean jogoRodando = false;
	public static boolean multiplayerRemoto = false;
	public static String estadoDoJogo = "telaInicial";		// 1: "telaInicial"; 2: escolherTime; 3: "conectando"; 4: "jogando"
	public static int mapaAtual = 2;						// mapas dispon�veis --> [1, 2]
	
	// Ferramentas
	private Thread thread;
	public static Spritesheet spritesheet;
	private BufferedImage imagemPrincipal;
	public static Random rand = new Random();
	
	// Constantes de Configura��o
	public static int LARGURA = Tabuleiro.DC*18;
	public static int ALTURA = Tabuleiro.DC*17;
	public static int ESCALA = 3;
	
	// Componentes
	public static Tabuleiro tabuleiro;
	
	// Firebase (multiplayer)
	public static int timeDoMultiplayerRemoto;
	public static Firebase firebase;

	
	// inicia o jogo
	private Jogo() {
		imagemPrincipal = new BufferedImage(LARGURA, ALTURA, BufferedImage.TYPE_INT_RGB);
		spritesheet = new Spritesheet("/spritesheet.png");
		
		tabuleiro = new Tabuleiro(String.valueOf(mapaAtual));
		
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

	public void att() throws NaoVazio, ForaDeAlcance, MuitoDistante, BauVazio, ChaNaoColetado {
		if(estadoDoJogo == "telaInicial") {
			InterfaceInicial.att();
		}else if(estadoDoJogo == "escolherTime") {
			InterfaceInicial.att();
		} else if(estadoDoJogo == "conectando") {
			if(multiplayerRemoto) {
				firebase = new Firebase();
				firebase.iniciarFireBase();
			}
			estadoDoJogo = "jogando";
		}else if(estadoDoJogo == "pausado") {
			
		} else if(estadoDoJogo == "jogando") {
			tabuleiro.att();
			if(multiplayerRemoto)
				firebase.att();	
		}

	}
	
	// renderiza gr�ficos
	public void renderizar() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = imagemPrincipal.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, LARGURA, ALTURA);
	
		if(estadoDoJogo == "telaInicial") {
			InterfaceInicial.renderizar(g);

		} else if(estadoDoJogo == "escolherTime") {
			InterfaceInicial.renderizar(g);
			
		} else if(estadoDoJogo == "jogando") {
			// Renderiza��o dos elementos do tabuleiro
			tabuleiro.renderizar(g);
			
		} else if(estadoDoJogo == "vitoriaInfectados") {
			tabuleiro.renderizar(g);
			InterfaceFinal.renderizar(g);
		} else if(estadoDoJogo == "vitoriaMedicosLock") {
			tabuleiro.renderizar(g);
			InterfaceFinal.renderizar(g);
		} else if (estadoDoJogo == "vitoriaMedicosCura") {
			tabuleiro.renderizar(g);
			InterfaceFinal.renderizar(g);			
		}
		g.dispose();
		g = bs.getDrawGraphics();
		g.drawImage(imagemPrincipal, 0, 0, LARGURA*ESCALA, ALTURA*ESCALA, null);
		
		// Renderiza��o sem pixeliza��o
		if(estadoDoJogo == "jogando") {
			for(int i = 0; i < Tabuleiro.entidadesMedicos.size(); i++) {
				Tabuleiro.entidadesMedicos.get(i).renderizarSemPixelizar(g);
			}			
		}
		tabuleiro.renderizarSemPixelizar(g);
		
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
		double nanosegundosDeUmFrame = 1000000000/fps;
		double delta = 0;
		long nanoAnterior = System.nanoTime();
		long nanoAtual;
		
		int numDeFrames = 0;
		long segPassado = System.currentTimeMillis();
		
		while(jogoRodando) {
			nanoAtual = System.nanoTime();
			delta += (nanoAtual - nanoAnterior)/nanosegundosDeUmFrame;
			nanoAnterior = nanoAtual;
			if(delta >= 1) {
				try {
					att();
				} catch (NaoVazio | ForaDeAlcance | MuitoDistante | BauVazio | ChaNaoColetado e) {
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

		if(estadoDoJogo == "telaInicial") {
			if(key.getKeyCode() == KeyEvent.VK_ENTER) {
				if(Jogo.multiplayerRemoto)
					estadoDoJogo = "escolherTime";
				else
					estadoDoJogo = "conectando";
			} else if(key.getKeyCode() == KeyEvent.VK_W) {
				InterfaceInicial.modoDeJogo--;
			} else if(key.getKeyCode() == KeyEvent.VK_S) {
				InterfaceInicial.modoDeJogo++;
			}
		} else if(estadoDoJogo == "escolherTime") {
			if(key.getKeyCode() == KeyEvent.VK_ENTER) {
				estadoDoJogo = "conectando";
			} else if(key.getKeyCode() == KeyEvent.VK_W) {
				InterfaceInicial.time--;
			} else if(key.getKeyCode() == KeyEvent.VK_S) {
				InterfaceInicial.time++;
			}
		}
		else if(estadoDoJogo == "jogando" && !Jogo.multiplayerRemoto) {
			// MOVIMENTO DO TIME DOS M�DICOS
			if(Tabuleiro.vezJogador == 1) {		
				if(!PecasMoveis.medicoSelecionado) {
					if(key.getKeyCode() == KeyEvent.VK_ENTER) {
						PecasMoveis.medicoSelecionado = true;
					} else if(key.getKeyCode() == KeyEvent.VK_J) {
						PecasMoveis.medicoAtual = PecasMoveis.mudarSelecaoDePeca(Tabuleiro.entidadesMedicos, 1, -1);			
					} else if(key.getKeyCode() == KeyEvent.VK_L) {
						PecasMoveis.medicoAtual = PecasMoveis.mudarSelecaoDePeca(Tabuleiro.entidadesMedicos, 1, 1);
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
				// MOVIMENTO DO TIME DOS INFECTADOS
				if(!PecasMoveis.infectadoSelecionado) {
					if(key.getKeyCode() == KeyEvent.VK_R) {
						PecasMoveis.infectadoSelecionado = true;
					} else if(key.getKeyCode() == KeyEvent.VK_A) {
						PecasMoveis.infectadoAtual = PecasMoveis.mudarSelecaoDePeca(Tabuleiro.entidadesInfectados, 2, -1);			
					} else if(key.getKeyCode() == KeyEvent.VK_D) {
						PecasMoveis.infectadoAtual = PecasMoveis.mudarSelecaoDePeca(Tabuleiro.entidadesInfectados, 2, 1);
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
		} else if(estadoDoJogo == "jogando" && Jogo.multiplayerRemoto) {
			// MOVIMENTO DO TIME DOS M�DICOS
						if(Tabuleiro.vezJogador == 1 && timeDoMultiplayerRemoto == 1) {		
							if(!PecasMoveis.medicoSelecionado) {
								if(key.getKeyCode() == KeyEvent.VK_ENTER) {
									PecasMoveis.medicoSelecionado = true;
								} else if(key.getKeyCode() == KeyEvent.VK_J) {
									PecasMoveis.medicoAtual = PecasMoveis.mudarSelecaoDePeca(Tabuleiro.entidadesMedicos, 1, -1);			
								} else if(key.getKeyCode() == KeyEvent.VK_L) {
									PecasMoveis.medicoAtual = PecasMoveis.mudarSelecaoDePeca(Tabuleiro.entidadesMedicos, 1, 1);
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
						
						else if(Tabuleiro.vezJogador == 2 && timeDoMultiplayerRemoto == 2) {
							// MOVIMENTO DO TIME DOS INFECTADOS
							if(!PecasMoveis.infectadoSelecionado) {
								if(key.getKeyCode() == KeyEvent.VK_R) {
									PecasMoveis.infectadoSelecionado = true;
								} else if(key.getKeyCode() == KeyEvent.VK_A) {
									PecasMoveis.infectadoAtual = PecasMoveis.mudarSelecaoDePeca(Tabuleiro.entidadesInfectados, 2, -1);			
								} else if(key.getKeyCode() == KeyEvent.VK_D) {
									PecasMoveis.infectadoAtual = PecasMoveis.mudarSelecaoDePeca(Tabuleiro.entidadesInfectados, 2, 1);
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
