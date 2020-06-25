package tabuleiro;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import entidades.PecasMoveis;
import interfaces.*;
import main.Jogo;

public class Tabuleiro implements IMovimento, ICriaCha {
	
	public static int DC = 16;		// dimensões de cada célula (16 x 16)
	
	public int larguraMapa;
	public int alturaMapa;
	
	public PecasMoveis vetorPecasMoveis[][];
	public Celulas vetorCelulas[][];
	public int vezJogador;
	
	public Tabuleiro(String endereco) {
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
}
