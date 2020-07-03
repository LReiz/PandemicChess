package tabuleiro;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.google.firebase.database.DataSnapshot;

import entidades.PecaBau;
import entidades.PecaCha;
import entidades.PecaInfectado;
import entidades.PecaMedico;
import entidades.PecasMoveis;
import erros.BauVazio;
import erros.ChaNaoColetado;
import erros.ForaDeAlcance;
import erros.MuitoDistante;
import erros.NaoVazio;
import interfaces.IAtaque;
import interfaces.ICriaCha;
import interfaces.IMovimento;
import main.Jogo;

public class Tabuleiro implements IMovimento, ICriaCha, IAtaque {
	
	// Constantes de Configuração
	public static int DC = 16;		// dimensões de cada célula (16 x 16)
	public int larguraMapa;
	public int alturaMapa;
	public static int vezJogador = 1;			// medicos 1; infectados: 2
	public static int rodada = 1;
	public static int rodadaCriaCha = 48;
	public static boolean chaCriado;
	
	// Vetores dos elementos do mapa
	public PecasMoveis vetorPecasMoveis[][];
	public PecaBau vetorBaus[][];
	public Celulas vetorCelulas[][];
	public PecaCha pecaCha;
	
	// Componentes
	public static ArrayList<PecasMoveis> entidadesMedicos;
	public static ArrayList<PecasMoveis> entidadesInfectados;
	public static ArrayList<PecaBau> entidadesBau;
	
	// Controle da quantidade de Peças para inicializar no mapa
	private int maxMedicos = 8;
	private int numMedicos = 0;
	private int maxInfectados = 5;
	private int numInfectados = 0;
	private int maxBaus = 8;
	private int numBaus = 0;
	
	public Tabuleiro(String endereco) {
		this.numMedicos = this.maxMedicos;
		this.numInfectados = this.maxInfectados;
		this.numBaus = this.maxBaus;
		
		entidadesMedicos = new ArrayList<PecasMoveis>();
		entidadesInfectados = new ArrayList<PecasMoveis>();
		entidadesBau = new ArrayList<PecaBau>();
		this.pecaCha = new PecaCha(0, 0, null);
		
		inicializarMapa(endereco);
		inicializarPecasParadas();
		inicializarPecasMoveis();
		
		PecasMoveis.medicoAtual = entidadesMedicos.get(0);
		PecasMoveis.infectadoAtual = entidadesInfectados.get(0);
	}
	
	// construtor para multiplayer
	public void reiniciarTabuleiro(DataSnapshot snapshot) {
		vetorPecasMoveis = new PecasMoveis[alturaMapa][larguraMapa];
		vetorBaus = new PecaBau[alturaMapa][larguraMapa];
		
		for(int i = 0; i < entidadesMedicos.size(); i++) {
			entidadesMedicos.get(i).pos[1] = Integer.parseInt(String.valueOf(snapshot.child("pecasmedicos").child("vetor").child(String.valueOf(i)).child("x").getValue()));
			entidadesMedicos.get(i).pos[0] = Integer.parseInt(String.valueOf(snapshot.child("pecasmedicos").child("vetor").child(String.valueOf(i)).child("y").getValue()));
			vetorPecasMoveis[(int)(entidadesMedicos.get(i).pos[0]/Tabuleiro.DC)][(int)(entidadesMedicos.get(i).pos[1]/Tabuleiro.DC)] = entidadesMedicos.get(i);
		}
		
		for(int i = 0; i < entidadesInfectados.size(); i++) {
			entidadesInfectados.get(i).pos[1] = Integer.parseInt(String.valueOf(snapshot.child("pecasinfectados").child("vetor").child(String.valueOf(i)).child("x").getValue()));
			entidadesInfectados.get(i).pos[0] = Integer.parseInt(String.valueOf(snapshot.child("pecasinfectados").child("vetor").child(String.valueOf(i)).child("y").getValue()));
			vetorPecasMoveis[(int)(entidadesInfectados.get(i).pos[0]/Tabuleiro.DC)][(int)(entidadesInfectados.get(i).pos[1]/Tabuleiro.DC)] = entidadesInfectados.get(i);
		}
		
		for(int i = 0; i < entidadesBau.size(); i++) {
			entidadesBau.get(i).pos[1] = Integer.parseInt(String.valueOf(snapshot.child("pecasbaus").child("vetor").child(String.valueOf(i)).child("x").getValue()));
			entidadesBau.get(i).pos[0] = Integer.parseInt(String.valueOf(snapshot.child("pecasbaus").child("vetor").child(String.valueOf(i)).child("y").getValue()));
			entidadesBau.get(i).mascaras = Integer.parseInt(String.valueOf(snapshot.child("pecasbaus").child("vetor").child(String.valueOf(i)).child("mascaras").getValue()));
			entidadesBau.get(i).algemas = Integer.parseInt(String.valueOf(snapshot.child("pecasbaus").child("vetor").child(String.valueOf(i)).child("algemas").getValue()));
			vetorBaus[(int)(entidadesBau.get(i).pos[0]/Tabuleiro.DC)][(int)(entidadesBau.get(i).pos[1]/Tabuleiro.DC)] = entidadesBau.get(i);
		}
		
		rodada = 1;
		vezJogador = 1;
		
	}
	
	int f = 0;

	public void att() throws NaoVazio, ForaDeAlcance, MuitoDistante, BauVazio, ChaNaoColetado {
		// atualiza chá
		if(chaCriado)
			pecaCha.att();
		else
			criaCha();
		
		
		// atualiza infectados
		for(int i = 0; i < entidadesInfectados.size(); i++) {
			entidadesInfectados.get(i).att();
			entidadesInfectados.get(i).indexNoVetor = i;
		}
		// atualiza médicos
		for(int i = 0; i < entidadesMedicos.size(); i++) {
			entidadesMedicos.get(i).att();
			entidadesMedicos.get(i).indexNoVetor = i;
		}

		// atualiza baús
		for(int i = 0; i < entidadesBau.size(); i++) {
			entidadesBau.get(i).att();
		}
		
	}
	
	public void renderizar(Graphics g) {
		// renderiza células
		for(int yy = 0; yy < alturaMapa; yy++) {
			for(int xx = 0; xx < larguraMapa; xx++) {
				vetorCelulas[yy][xx].renderizar(g);
			}
		}

		// renderiza infectados
		for(int i = 0; i < entidadesInfectados.size(); i++) {
			entidadesInfectados.get(i).renderizar(g);
		}

		// renderiza médicos
		for(int i = 0; i < entidadesMedicos.size(); i++) {
			entidadesMedicos.get(i).renderizar(g);
		}
		
		// renderiza baús
		for(int i = 0; i < entidadesBau.size(); i++) {
			entidadesBau.get(i).renderizar(g);
		}
		
		// renderiza chá
		if(chaCriado)
			pecaCha.renderizar(g);

		// renderiza indicador de seleção de peça
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
	}
	
	private void inicializarMapa(String endereco) {
		vetorCelulas = new Celulas[Jogo.ALTURA][Jogo.ALTURA];
		
		BufferedImage MapaDePixels = null;
		try {
			MapaDePixels = ImageIO.read(getClass().getResource(endereco));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		larguraMapa = MapaDePixels.getWidth();
		alturaMapa = MapaDePixels.getHeight();
		
		int[] pixelsHexCelulas = new int[alturaMapa*larguraMapa];
		MapaDePixels.getRGB(0, 0, larguraMapa, alturaMapa, pixelsHexCelulas, 0, larguraMapa);
		
		for(int yy = 0; yy < alturaMapa; yy++) {
			for(int xx = 0; xx < larguraMapa; xx++) {
				if(pixelsHexCelulas[xx + yy*larguraMapa] == 0xFF635F89) {		// Roxo (Chão Roxo)
					this.vetorCelulas[yy][xx] = new ChaoRoxo(xx*DC, yy*DC, Celulas.CELULA_CHAO_ROXO);
				} else if(pixelsHexCelulas[xx + yy*larguraMapa] == 0xFFC15A26) {
					this.vetorCelulas[yy][xx] = new ParedeTijolos(xx*DC, yy*DC, Celulas.CELULA_PAREDE_TIJOLOS);
				} else if(pixelsHexCelulas[xx + yy*larguraMapa] == 0xFFE8E8EA) {
					this.vetorCelulas[yy][xx] = new ParedeHospital(xx*DC, yy*DC, Celulas.CELULA_PAREDE_HOSPITAL1);
				} else if(pixelsHexCelulas[xx + yy*larguraMapa] == 0xFFDEDEE0) {
					this.vetorCelulas[yy][xx] = new ParedeHospital(xx*DC, yy*DC, Celulas.CELULA_PAREDE_HOSPITAL2);
				} else if(pixelsHexCelulas[xx + yy*larguraMapa] == 0xFFD4D4D6) {
					this.vetorCelulas[yy][xx] = new ParedeHospital(xx*DC, yy*DC, Celulas.CELULA_PAREDE_HOSPITAL3);
				} else if(pixelsHexCelulas[xx + yy*larguraMapa] == 0xFFC9C9CC) {
					this.vetorCelulas[yy][xx] = new ParedeHospital(xx*DC, yy*DC, Celulas.CELULA_PAREDE_HOSPITAL4);
				} else {
					this.vetorCelulas[yy][xx] = new ChaoRoxo(xx*DC, yy*DC, Celulas.CELULA_CHAO_ROXO);
				}
			}
		}
	}
	
	private void inicializarPecasParadas() {
		vetorBaus = new PecaBau[alturaMapa][larguraMapa];

		// inicialização dos Baus (metade superior do mapa)
		int xx = larguraMapa-1;
		int yy = (int)(alturaMapa/2);
		
		while(numBaus > 0) {
			if(vetorBaus[yy][xx] == null
					&& !vetorCelulas[yy][xx].colisao
					&& Jogo.rand.nextInt(100) < 1) {
				
				PecaBau bau = new PecaBau(xx*DC, yy*DC, Jogo.rand.nextInt(6), Jogo.rand.nextInt(2), PecaBau.PECA_BAU);
				
				vetorBaus[yy][xx] = bau;
				entidadesBau.add(bau);
				numBaus--;
			}
			xx--;
			if(xx < 0) {
				xx = larguraMapa-1;
				yy--;
				if(yy < 0) {
					yy = (int)(alturaMapa/2);
				}
			}
		}
	}
	
	private void inicializarPecasMoveis() {
		vetorPecasMoveis = new PecasMoveis[alturaMapa][larguraMapa];
		
		// inicialização dos Infectados (parte superior do mapa)
		int xx = larguraMapa-1;
		int yy = (int)((alturaMapa-1)/3);
		while(numInfectados > 0) {
			if(vetorPecasMoveis[yy][xx] == null 
					&& vetorBaus[yy][xx] == null
					&& !vetorCelulas[yy][xx].colisao
					&& Jogo.rand.nextInt(100) < 1) {
				 PecasMoveis inf = new PecaInfectado(xx*DC, yy*DC, PecaInfectado.PECA_INFECTADO);
				
				 vetorPecasMoveis[yy][xx] = inf;
				 entidadesInfectados.add(inf);
				 numInfectados--;
			}
			xx--;
			if(xx < 0) {
				xx = larguraMapa-1;
				yy--;
				if(yy < 0) {
					yy = (int)(alturaMapa/3);
				}
			}
		}
		
		// inicialização dos Médicos (parte inferior do mapa)
		xx = larguraMapa-1;
		yy = (int)((alturaMapa-1)-(alturaMapa/3));
		while(numMedicos > 0) {
			if(vetorPecasMoveis[yy][xx] == null 
					&& vetorBaus[yy][xx] == null
					&& !vetorCelulas[yy][xx].colisao
					&& Jogo.rand.nextInt(100) < 1) {
				PecasMoveis med;
				if(Jogo.rand.nextInt(100) < 50) {
					med = new PecaMedico(xx*DC, yy*DC, PecaMedico.PECA_MEDICO_B);
				} else {
					med = new PecaMedico(xx*DC, yy*DC, PecaMedico.PECA_MEDICO_P);
				}
				vetorPecasMoveis[yy][xx] = med;					
				entidadesMedicos.add(med);
				numMedicos--;
			}
			xx--;
			if(xx < 0) {
				xx = larguraMapa-1;
				yy++;
				if(yy > alturaMapa-1) {
					yy = (int)((alturaMapa-1)-(alturaMapa/3));
				}
			}
		}
	}

	@Override
	public boolean movimento(int x_final, int y_final, PecasMoveis peca, Tabuleiro tab) throws NaoVazio, ForaDeAlcance, MuitoDistante {
		if((int)(y_final/DC) < 0 || (int)(y_final/DC) >= alturaMapa
				|| (int)(x_final/DC) < 0 || (int)(x_final/DC) >= larguraMapa)
		{
			if(Jogo.multiplayerRemoto) {
				return false;
			} else {
				peca.movendo = false;
				throw new ForaDeAlcance("Movimento para fora do Tabuleiro");
			}
		}
			
		if(vetorPecasMoveis[(int)(y_final/DC)][(int)(x_final/DC)] != null 
				|| vetorBaus[(int)(y_final/DC)][(int)(x_final/DC)] != null
				|| vetorCelulas[(int)(y_final/DC)][(int)(x_final/DC)].colisao) 
		{
			if(Jogo.multiplayerRemoto) {
				return false;
			} else {
				peca.movendo = false;
				throw new NaoVazio("Célula Ocupada");
			}
		}
		
		return true;
	}
	
	public static void trocarVez() throws ChaNaoColetado {
		passarParaProximaRodada();
		realizarTurnoDeAtaque();
		Jogo.tabuleiro.verificarVitoria();
	}
	
	public static void passarParaProximaRodada() {
		if(vezJogador == 1)
			vezJogador = 2;		// infectados
		else if(vezJogador == 2)
			vezJogador = 1;		// médicos
		
		rodada++;
	}
	
	public static void realizarTurnoDeAtaque() {
		for(int i = 0; i < entidadesMedicos.size(); i++) {
			entidadesMedicos.get(i).turnoDeAtaque = true;
		}
		for(int i = 0; i < entidadesInfectados.size(); i++) {
			entidadesInfectados.get(i).turnoDeAtaque = true;
			
		}
	}

	@Override
	public PecaCha criaCha() {
		if(Tabuleiro.rodada > Tabuleiro.rodadaCriaCha) {
			pecaCha = pecaCha.criaCha();
			chaCriado = true;
		}
		return null;
	}
	
	public void atacar(PecasMoveis inimigo, Tabuleiro tab) {

		if(inimigo instanceof PecaMedico) {
				if(inimigo.mascaras > 0) {
					inimigo.mascaras--;
					return;
				}

				int xMed = (int)(inimigo.pos[1]/Tabuleiro.DC);
				int yMed = (int)(inimigo.pos[0]/Tabuleiro.DC);
				removerInimigoDoTabuleiro(inimigo, 2);
				
				if(inimigo.cha == true) {
					tab.pecaCha.medicoPortadorDoCha = null;
				}
				
				PecasMoveis infectado = new PecaInfectado(xMed*Tabuleiro.DC, yMed*Tabuleiro.DC,PecaInfectado.PECA_INFECTADO);
				tab.vetorPecasMoveis[yMed][xMed] = infectado;
				Tabuleiro.entidadesInfectados.add(infectado);
		}

		else if(inimigo instanceof PecaInfectado) {		// médicos atacam
			if(inimigo.algemas > 0) {

				int xInf = (int)(inimigo.pos[1]/Tabuleiro.DC);
				int yInf = (int)(inimigo.pos[0]/Tabuleiro.DC);
				removerInimigoDoTabuleiro(inimigo, 1);
				tab.vetorPecasMoveis[yInf][xInf] = null;
			}
		}
	}
	
	private void removerInimigoDoTabuleiro(PecasMoveis inimigo, int timeAtacante) {
		if(timeAtacante == 1) {				// médicos atacando
			Tabuleiro.entidadesInfectados.remove(inimigo);
			if(Tabuleiro.entidadesInfectados.size() > 0) {
				PecasMoveis.indexInfectado = 0;
				PecasMoveis.infectadoAtual = Tabuleiro.entidadesInfectados.get(0);
			}
		} else if(timeAtacante == 2) {		// infectados atacando
			entidadesMedicos.remove(inimigo);
			if(Tabuleiro.entidadesMedicos.size() > 0) {
				PecasMoveis.indexMedico = 0;
				PecasMoveis.medicoAtual = Tabuleiro.entidadesMedicos.get(0);					
			}
		}
	}
	
	public void verificarVitoria() throws ChaNaoColetado{
		if(entidadesMedicos.size() <= 0) {
			System.out.println("VITÓRIA DOS INFECTADOS");
			System.out.println("Genocídio: O vírus venceu a humanidade!");
			return;
		}
		else if(entidadesInfectados.size() <= 0) {
			System.out.println("VITÓRIA DOS MÉDICOS");
			System.out.println("Lockdown: Todos os infectados foram colocados em quarentena!");
		}
		else {
			if((vetorPecasMoveis[15][8] instanceof PecaMedico && vetorPecasMoveis[15][8].cha == true) ||
				(vetorPecasMoveis[15][9] instanceof PecaMedico && vetorPecasMoveis[15][9].cha == true)){
				System.out.println("VITÓRIA DOS MÉDICOS");
				System.out.println("Cura encontrada: Os cientistas descobriram que a cura para a doença era um pouco de repouso e um chazinho de boldo!");
			}
			else if(((vetorPecasMoveis[15][8] instanceof PecaMedico && vetorPecasMoveis[15][8].cha == false) ||
					(vetorPecasMoveis[15][9] instanceof PecaMedico && vetorPecasMoveis[15][9].cha == false))
					&& !Jogo.multiplayerRemoto){
				throw new ChaNaoColetado("O medico chegou ao hospital sem a cura");
			}
		}
	}
}