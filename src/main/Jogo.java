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

import database.Conjunto;
import database.Time;
import entidades.PecasMoveis;
import erros.BauVazio;
import erros.ChaNaoColetado;
import erros.ForaDeAlcance;
import erros.MuitoDistante;
import erros.NaoVazio;
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
	public static String estadoDoJogo = "telaInicial";		// 1: "telaInicial"; 2: "conectando"; 3: "jogando"
	
	// Ferramentas
	private Thread thread;
	public static Spritesheet spritesheet;
	private BufferedImage imagemPrincipal;
	public static Random rand = new Random();
	
	// Constantes de Configuração
	public static int LARGURA = Tabuleiro.DC*18;
	public static int ALTURA = Tabuleiro.DC*17;
	public static int ESCALA = 3;
	
	// Componentes
	public static Tabuleiro tabuleiro;
	
	// Firebase (multiplayer)
	public static FirebaseDatabase db;
	public static DatabaseReference ref;
	public static DatabaseReference medRef;
	public static DatabaseReference infRef;
	public static DatabaseReference pecaMedRef;
	public static DatabaseReference pecaInfRef;
	public static DatabaseReference pecaBauRef;
	public static Time medDoc;
	public static Time infDoc;
	public static Conjunto medConj;
	public static Conjunto infConj;
	public static Conjunto bauConj;
	
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
	
	// atualiza informações do jogo

	public void att() throws NaoVazio, ForaDeAlcance, MuitoDistante, BauVazio, ChaNaoColetado {
		if(estadoDoJogo == "telaInicial") {
			InterfaceInicial.att();
		}else if(estadoDoJogo == "conectando") {
			if(multiplayerRemoto)
				iniciarFireBase();
			estadoDoJogo = "jogando";
		}else if(estadoDoJogo == "pausado") {
			
		} else if(estadoDoJogo == "jogando") {
			tabuleiro.att();
			if(multiplayerRemoto)
				attFireBase();			
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
	
		if(estadoDoJogo == "telaInicial") {
			InterfaceInicial.renderizar(g);

			g.dispose();
			g = bs.getDrawGraphics();
			g.drawImage(imagemPrincipal, 0, 0, LARGURA*ESCALA, ALTURA*ESCALA, null);
		} else if(estadoDoJogo == "jogando") {
			// Renderização dos elementos do tabuleiro
			tabuleiro.renderizar(g);
			
			g.dispose();
			g = bs.getDrawGraphics();
			g.drawImage(imagemPrincipal, 0, 0, LARGURA*ESCALA, ALTURA*ESCALA, null);
			
			// Renderização sem pixelização
			for(int i = 0; i < Tabuleiro.entidadesMedicos.size(); i++) {
				Tabuleiro.entidadesMedicos.get(i).renderizarSemPixelizar(g);
			}
		}
		
		bs.show();
	}
	
	private void iniciarFireBase() {
		FileInputStream serviceAccount = null;
		try {
			serviceAccount = new FileInputStream("./serviceAccountKey.json");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		FirebaseOptions options = null;
		try {
			options = new FirebaseOptions.Builder()
			  .setCredentials(GoogleCredentials.fromStream(serviceAccount))
			  .setDatabaseUrl("https://pandemicchess-16070.firebaseio.com")
			  .build();
		} catch (IOException e) {
			e.printStackTrace();
		}

		FirebaseApp.initializeApp(options);
		
		iniciarInstanciasFireBase();
		observadorFirebase();
	}
	
	private void iniciarInstanciasFireBase() {
		db = FirebaseDatabase.getInstance();
		ref = db.getReference();
		
		// Banco de Dados usado durante o jogo
		medRef = ref.child("medicos");
		infRef = ref.child("infectados");
		medDoc = new Time("1", Tabuleiro.entidadesMedicos.size());
		infDoc = new Time("2", Tabuleiro.entidadesInfectados.size());
		medRef.setValueAsync(medDoc);
		infRef.setValueAsync(infDoc);
		
		// Banco de Dados para inicializar as peças no tabuleiro
		pecaMedRef = ref.child("conjuntos/pecasmedicos");
		pecaInfRef = ref.child("conjuntos/pecasinfectados");
		pecaBauRef = ref.child("conjuntos/pecasbaus");
		medConj = new Conjunto(Tabuleiro.entidadesMedicos);
		infConj = new Conjunto(Tabuleiro.entidadesInfectados);
		bauConj = new Conjunto(Tabuleiro.entidadesBau);
		pecaMedRef.setValueAsync(medConj);
		pecaInfRef.setValueAsync(infConj);
		pecaBauRef.setValueAsync(bauConj);

	}
	
	private void observadorFirebase() {
		ref.child("conjuntos").addValueEventListener(new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				tabuleiro.reiniciarTabuleiro(snapshot);
				
			}
			
			@Override
			public void onCancelled(DatabaseError error) {
				
				
			}
			
		});
		
		ref.child("infectados").addValueEventListener(new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				PecasMoveis.infectadoAtualDirX = Integer.parseInt(String.valueOf(snapshot.child("dirX").getValue()));
				PecasMoveis.infectadoAtualDirY = Integer.parseInt(String.valueOf(snapshot.child("dirY").getValue()));
				PecasMoveis.proxPosicaoInfectadoX = Integer.parseInt(String.valueOf(snapshot.child("proxX").getValue()));
				PecasMoveis.proxPosicaoInfectadoY = Integer.parseInt(String.valueOf(snapshot.child("proxY").getValue()));
				PecasMoveis.indexInfectado = Integer.parseInt(String.valueOf(snapshot.child("index").getValue()));
				for(int i = 0; i < Tabuleiro.entidadesInfectados.size(); i++) {
					infDoc.vetor.get(String.valueOf(i)).movendo = String.valueOf(snapshot.child("vetor").child(String.valueOf(i)).child("movendo").getValue());
				}
			}
			
			@Override
			public void onCancelled(DatabaseError error) {
				
				
			}
			
		});
		
		ref.child("medicos").addValueEventListener(new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				PecasMoveis.medicoAtualDirX = Integer.parseInt(String.valueOf(snapshot.child("dirX").getValue()));
				PecasMoveis.medicoAtualDirY = Integer.parseInt(String.valueOf(snapshot.child("dirY").getValue()));
				PecasMoveis.proxPosicaoMedicoX = Integer.parseInt(String.valueOf(snapshot.child("proxX").getValue()));
				PecasMoveis.proxPosicaoMedicoY = Integer.parseInt(String.valueOf(snapshot.child("proxY").getValue()));
				PecasMoveis.indexMedico = Integer.parseInt(String.valueOf(snapshot.child("index").getValue()));
				for(int i = 0; i < Tabuleiro.entidadesMedicos.size(); i++) {
					medDoc.vetor.get(String.valueOf(i)).movendo = String.valueOf(snapshot.child("vetor").child(String.valueOf(i)).child("movendo").getValue());
				}
			}
			
			@Override
			public void onCancelled(DatabaseError error) {
				
				
			}
			
		});
	}
	
	private void attFireBase() {
		medDoc.att();
		infDoc.att();
		medRef.setValueAsync(medDoc);
		infRef.setValueAsync(infDoc);
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
				estadoDoJogo = "conectando";
			} else if(key.getKeyCode() == KeyEvent.VK_W) {
				InterfaceInicial.modoDeJogo--;
			} else if(key.getKeyCode() == KeyEvent.VK_S) {
				InterfaceInicial.modoDeJogo++;
			}
		} else if(estadoDoJogo == "jogando" && !Jogo.multiplayerRemoto) {
			// MOVIMENTO DO TIME DOS MÉDICOS
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
						PecasMoveis.medicoAtual.dir = 0;
						PecasMoveis.medicoAtual.movendo = true;
					} else if(key.getKeyCode() == KeyEvent.VK_L) {
						PecasMoveis.medicoAtualDirX = 1;
						PecasMoveis.medicoAtualDirY = 0;
						PecasMoveis.proxPosicaoMedicoX = PecasMoveis.medicoAtual.pos[1] + (PecasMoveis.medicoAtualDirX*Tabuleiro.DC);
						PecasMoveis.proxPosicaoMedicoY = PecasMoveis.medicoAtual.pos[0] + (PecasMoveis.medicoAtualDirY*Tabuleiro.DC);
						PecasMoveis.medicoAtual.dir = 1;
						PecasMoveis.medicoAtual.movendo = true;
					} else if(key.getKeyCode() == KeyEvent.VK_I) {
						PecasMoveis.medicoAtualDirY = -1;
						PecasMoveis.medicoAtualDirX = 0;
						PecasMoveis.proxPosicaoMedicoX = PecasMoveis.medicoAtual.pos[1] + (PecasMoveis.medicoAtualDirX*Tabuleiro.DC);
						PecasMoveis.proxPosicaoMedicoY = PecasMoveis.medicoAtual.pos[0] + (PecasMoveis.medicoAtualDirY*Tabuleiro.DC);
						PecasMoveis.medicoAtual.dir = 2;
						PecasMoveis.medicoAtual.movendo = true;
					} else if(key.getKeyCode() == KeyEvent.VK_K) {
						PecasMoveis.medicoAtualDirY = 1;
						PecasMoveis.medicoAtualDirX = 0;
						PecasMoveis.proxPosicaoMedicoX = PecasMoveis.medicoAtual.pos[1] + (PecasMoveis.medicoAtualDirX*Tabuleiro.DC);
						PecasMoveis.proxPosicaoMedicoY = PecasMoveis.medicoAtual.pos[0] + (PecasMoveis.medicoAtualDirY*Tabuleiro.DC);
						PecasMoveis.medicoAtual.dir = 3;
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
						PecasMoveis.infectadoAtual.dir = 0;
						PecasMoveis.infectadoAtual.movendo = true;
					} else if(key.getKeyCode() == KeyEvent.VK_D) {
						PecasMoveis.infectadoAtualDirX = 1;
						PecasMoveis.infectadoAtualDirY = 0;
						PecasMoveis.proxPosicaoInfectadoX = PecasMoveis.infectadoAtual.pos[1] + (PecasMoveis.infectadoAtualDirX*Tabuleiro.DC);
						PecasMoveis.proxPosicaoInfectadoY = PecasMoveis.infectadoAtual.pos[0] + (PecasMoveis.infectadoAtualDirY*Tabuleiro.DC);
						PecasMoveis.infectadoAtual.dir = 1;
						PecasMoveis.infectadoAtual.movendo = true;
					} else if(key.getKeyCode() == KeyEvent.VK_W) {
						PecasMoveis.infectadoAtualDirY = -1;
						PecasMoveis.infectadoAtualDirX = 0;
						PecasMoveis.proxPosicaoInfectadoX = PecasMoveis.infectadoAtual.pos[1] + (PecasMoveis.infectadoAtualDirX*Tabuleiro.DC);
						PecasMoveis.proxPosicaoInfectadoY = PecasMoveis.infectadoAtual.pos[0] + (PecasMoveis.infectadoAtualDirY*Tabuleiro.DC);
						PecasMoveis.infectadoAtual.dir = 2;
						PecasMoveis.infectadoAtual.movendo = true;
					} else if(key.getKeyCode() == KeyEvent.VK_S) {
						PecasMoveis.infectadoAtualDirY = 1;
						PecasMoveis.infectadoAtualDirX = 0;
						PecasMoveis.proxPosicaoInfectadoX = PecasMoveis.infectadoAtual.pos[1] + (PecasMoveis.infectadoAtualDirX*Tabuleiro.DC);
						PecasMoveis.proxPosicaoInfectadoY = PecasMoveis.infectadoAtual.pos[0] + (PecasMoveis.infectadoAtualDirY*Tabuleiro.DC);
						PecasMoveis.infectadoAtual.dir = 3;
						PecasMoveis.infectadoAtual.movendo = true;
					}
				}
			}
		} else if(estadoDoJogo == "jogando" && Jogo.multiplayerRemoto) {
			// MOVIMENTO DO TIME DOS MÉDICOS
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
									PecasMoveis.medicoAtual.dir = 0;
									PecasMoveis.medicoAtual.movendo = true;
								} else if(key.getKeyCode() == KeyEvent.VK_L) {
									PecasMoveis.medicoAtualDirX = 1;
									PecasMoveis.medicoAtualDirY = 0;
									PecasMoveis.proxPosicaoMedicoX = PecasMoveis.medicoAtual.pos[1] + (PecasMoveis.medicoAtualDirX*Tabuleiro.DC);
									PecasMoveis.proxPosicaoMedicoY = PecasMoveis.medicoAtual.pos[0] + (PecasMoveis.medicoAtualDirY*Tabuleiro.DC);
									PecasMoveis.medicoAtual.dir = 1;
									PecasMoveis.medicoAtual.movendo = true;
								} else if(key.getKeyCode() == KeyEvent.VK_I) {
									PecasMoveis.medicoAtualDirY = -1;
									PecasMoveis.medicoAtualDirX = 0;
									PecasMoveis.proxPosicaoMedicoX = PecasMoveis.medicoAtual.pos[1] + (PecasMoveis.medicoAtualDirX*Tabuleiro.DC);
									PecasMoveis.proxPosicaoMedicoY = PecasMoveis.medicoAtual.pos[0] + (PecasMoveis.medicoAtualDirY*Tabuleiro.DC);
									PecasMoveis.medicoAtual.dir = 2;
									PecasMoveis.medicoAtual.movendo = true;
								} else if(key.getKeyCode() == KeyEvent.VK_K) {
									PecasMoveis.medicoAtualDirY = 1;
									PecasMoveis.medicoAtualDirX = 0;
									PecasMoveis.proxPosicaoMedicoX = PecasMoveis.medicoAtual.pos[1] + (PecasMoveis.medicoAtualDirX*Tabuleiro.DC);
									PecasMoveis.proxPosicaoMedicoY = PecasMoveis.medicoAtual.pos[0] + (PecasMoveis.medicoAtualDirY*Tabuleiro.DC);
									PecasMoveis.medicoAtual.dir = 3;
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
									PecasMoveis.infectadoAtual.dir = 0;
									PecasMoveis.infectadoAtual.movendo = true;
								} else if(key.getKeyCode() == KeyEvent.VK_D) {
									PecasMoveis.infectadoAtualDirX = 1;
									PecasMoveis.infectadoAtualDirY = 0;
									PecasMoveis.proxPosicaoInfectadoX = PecasMoveis.infectadoAtual.pos[1] + (PecasMoveis.infectadoAtualDirX*Tabuleiro.DC);
									PecasMoveis.proxPosicaoInfectadoY = PecasMoveis.infectadoAtual.pos[0] + (PecasMoveis.infectadoAtualDirY*Tabuleiro.DC);
									PecasMoveis.infectadoAtual.dir = 1;
									PecasMoveis.infectadoAtual.movendo = true;
								} else if(key.getKeyCode() == KeyEvent.VK_W) {
									PecasMoveis.infectadoAtualDirY = -1;
									PecasMoveis.infectadoAtualDirX = 0;
									PecasMoveis.proxPosicaoInfectadoX = PecasMoveis.infectadoAtual.pos[1] + (PecasMoveis.infectadoAtualDirX*Tabuleiro.DC);
									PecasMoveis.proxPosicaoInfectadoY = PecasMoveis.infectadoAtual.pos[0] + (PecasMoveis.infectadoAtualDirY*Tabuleiro.DC);
									PecasMoveis.infectadoAtual.dir = 2;
									PecasMoveis.infectadoAtual.movendo = true;
								} else if(key.getKeyCode() == KeyEvent.VK_S) {
									PecasMoveis.infectadoAtualDirY = 1;
									PecasMoveis.infectadoAtualDirX = 0;
									PecasMoveis.proxPosicaoInfectadoX = PecasMoveis.infectadoAtual.pos[1] + (PecasMoveis.infectadoAtualDirX*Tabuleiro.DC);
									PecasMoveis.proxPosicaoInfectadoY = PecasMoveis.infectadoAtual.pos[0] + (PecasMoveis.infectadoAtualDirY*Tabuleiro.DC);
									PecasMoveis.infectadoAtual.dir = 3;
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
