package tabuleiro;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import entidades.PecaBau;
import entidades.PecaInfectado;
import entidades.PecaMedico;
import entidades.PecasMoveis;
import erros.ForaDeAlcance;
import erros.MuitoDistante;
import erros.NaoVazio;
import interfaces.ICriaCha;
import interfaces.IMovimento;
import main.Jogo;

public class Tabuleiro implements IMovimento, ICriaCha {
	
	// Constantes de Configuração
	public static int DC = 16;		// dimensões de cada célula (16 x 16)
	public int larguraMapa;
	public int alturaMapa;
	public static int vezJogador = 1;			// medicos 1; infectados: 2
	
	// Vetores dos elementos do mapa
	public PecasMoveis vetorPecasMoveis[][];
	public PecaBau vetorBaus[][];
	public Celulas vetorCelulas[][];
	
	// Controle da quantidade de Peças no mapa
	private int maxMedicos = 8;
	private int numMedicos = 0;
	private int maxInfectados = 5;
	private int numInfectados = 0;
	
	public Tabuleiro(String endereco) {
		numMedicos = maxMedicos;
		numInfectados = maxInfectados;
		
		inicializarMapa(endereco);
		inicializarPecasMoveis();
		vetorBaus = new PecaBau[Jogo.ALTURA][Jogo.ALTURA];
	}
	
	private void inicializarMapa(String endereco) {
		vetorCelulas = new Celulas[Jogo.ALTURA][Jogo.ALTURA];
		
		BufferedImage MapaDePixels = null;
		try {
			MapaDePixels = ImageIO.read(getClass().getResource(endereco));
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
					System.out.println(xx+yy*larguraMapa + " " + pixelsHexCelulas[xx + yy*larguraMapa] + " " + 0x635F89FF);
					this.vetorCelulas[yy][xx] = new ChaoRoxo(xx*DC, yy*DC, Celulas.CELULA_CHAO_ROXO);
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
			if(vetorPecasMoveis[yy][xx] == null && Jogo.rand.nextInt(100) < 1) {
				 PecasMoveis inf = new PecaInfectado(xx*DC, yy*DC, PecaInfectado.PECA_INFECTADO);
				
				 vetorPecasMoveis[yy][xx] = inf;
				 Jogo.entidadesInfectados.add(inf);
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
			if(vetorPecasMoveis[yy][xx] == null && Jogo.rand.nextInt(100) < 1) {
				PecasMoveis med;
				if(Jogo.rand.nextInt(100) < 50) {
					med = new PecaMedico(xx*DC, yy*DC, PecaMedico.PECA_MEDICO_B);
				} else {
					med = new PecaMedico(xx*DC, yy*DC, PecaMedico.PECA_MEDICO_P);
				}
				vetorPecasMoveis[yy][xx] = med;					
				Jogo.entidadesMedicos.add(med);
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
		if((int)(y_final/DC) < 0 || (int)(y_final/DC) >= Jogo.ALTURA
				|| (int)(x_final/DC) < 0 || (int)(x_final/DC) >= Jogo.LARGURA)
		{
			peca.movendo = false;
			throw new ForaDeAlcance("Movimento para fora do Tabuleiro");
		}
			
		if(vetorPecasMoveis[(int)(y_final/DC)][(int)(x_final/DC)] != null 
				|| vetorBaus[(int)(y_final/DC)][(int)(x_final/DC)] != null
				|| vetorCelulas[(int)(y_final/DC)][(int)(x_final/DC)].colisao) 
		{
			peca.movendo = false;
			throw new NaoVazio("Célula Ocupada");
		}

		return true;
	}
	
	public static void trocarVez() {
		System.out.println(vezJogador);
		if(vezJogador == 1)
			vezJogador = 2;		// infectados
		else if(vezJogador == 2)
			vezJogador = 1;		// médicos
	}
}
