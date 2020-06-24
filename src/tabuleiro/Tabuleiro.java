package tabuleiro;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import entidades.PecasMoveis;
import interfaces.*;
import main.Jogo;

public class Tabuleiro implements IMovimento, ICriaCha {
	
	public static int DC = 16;		// dimensões de cada célula (16 x 16)
	
	private PecasMoveis vetorPecasMoveis[][];
	private Celulas vetorCelulas[][];
	private int vezJogador;
	
	public Tabuleiro(String endereco) {
		BufferedImage MapaDePixels = null;
		try {
			MapaDePixels = ImageIO.read(getClass().getResource(endereco));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int[] pixelsHexCelulas = new int[Jogo.ALTURA*Jogo.LARGURA];
		MapaDePixels.getRGB(0, 0, Jogo.LARGURA, Jogo.ALTURA, pixelsHexCelulas, 0, Jogo.LARGURA);
		
		for(int yy = 0; yy < Jogo.ALTURA; yy++) {
			for(int xx = 0; xx < Jogo.LARGURA; xx++) {
				if(pixelsHexCelulas[xx + yy*Jogo.LARGURA] == 0x635F89FF) {
					this.vetorCelulas[yy][xx] = new ChaoRoxo(xx*DC, yy*DC, Celulas.CELULA_CHAO_ROXO);
				} else if(pixelsHexCelulas[xx + yy*Jogo.LARGURA] == 0xC15A26FF) {
					this.vetorCelulas[yy][xx] = new ParedeTijolos(xx*DC, yy*DC, Celulas.CELULA_PAREDE_TIJOLOS);
				} else if(pixelsHexCelulas[xx + yy*Jogo.LARGURA] == 0xE8E8EAFF) {
					this.vetorCelulas[yy][xx] = new ParedeHospital(xx*DC, yy*DC, Celulas.CELULA_PAREDE_HOSPITAL1);
				} else if(pixelsHexCelulas[xx + yy*Jogo.LARGURA] == 0xDEDEE0FF) {
					this.vetorCelulas[yy][xx] = new ParedeHospital(xx*DC, yy*DC, Celulas.CELULA_PAREDE_HOSPITAL2);
				} else if(pixelsHexCelulas[xx + yy*Jogo.LARGURA] == 0xD4D4D6FF) {
					this.vetorCelulas[yy][xx] = new ParedeHospital(xx*DC, yy*DC, Celulas.CELULA_PAREDE_HOSPITAL3);
				} else if(pixelsHexCelulas[xx + yy*Jogo.LARGURA] == 0xC9C9CCFF) {
					this.vetorCelulas[yy][xx] = new ParedeHospital(xx*DC, yy*DC, Celulas.CELULA_PAREDE_HOSPITAL4);
				} else {
					this.vetorCelulas[yy][xx] = new ChaoRoxo(xx*DC, yy*DC, Celulas.CELULA_CHAO_ROXO);
				}
			}
		}
	}
}
