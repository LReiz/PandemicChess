package tabuleiro;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import entidades.PecaInfectado;
import entidades.PecaMedico;
import entidades.PecasMoveis;
import interfaces.*;
import main.Jogo;

public class Tabuleiro implements IMovimento, ICriaCha {
	
	// Constantes de Configuração
	public static int DC = 16;		// dimensões de cada célula (16 x 16)
	public int larguraMapa;
	public int alturaMapa;
	public int vezJogador;
	
	// Vetores dos elementos do mapa
	public PecasMoveis vetorPecasMoveis[][];
	public Celulas vetorCelulas[][];
	
	// Controle da quantidade de Peças no mapa
	private int maxMedicos = 8;
	private int numMedicos = maxMedicos;
	private int maxInfectados = 5;
	private int numInfectados = maxInfectados;
	
	public Tabuleiro(String endereco) {
		inicializarMapa(endereco);
		inicializarPecasMoveis();
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
				if(pixelsHexCelulas[xx + yy*larguraMapa] == 0xFF635F89) {
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
				vetorPecasMoveis[yy][xx] = new PecaInfectado(xx*DC, yy*DC, PecaInfectado.PECA_INFECTADO);
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
				if(Jogo.rand.nextInt(100) < 50) {
					vetorPecasMoveis[yy][xx] = new PecaMedico(xx*DC, yy*DC, PecaMedico.PECA_MEDICO_B);					
				} else {
					vetorPecasMoveis[yy][xx] = new PecaMedico(xx*DC, yy*DC, PecaMedico.PECA_MEDICO_P);
				}
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
}
