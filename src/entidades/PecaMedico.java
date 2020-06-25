package entidades;
import java.awt.image.BufferedImage;


import interfaces.*;
import main.Jogo;
import tabuleiro.Tabuleiro;

public class PecaMedico extends PecasMoveis implements ITransferir{

	public static BufferedImage PECA_MEDICO_B = Jogo.spritesheet.getSprite(0*Tabuleiro.DC, 1*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	public static BufferedImage PECA_MEDICO_P = Jogo.spritesheet.getSprite(3*Tabuleiro.DC, 1*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	
	public PecaMedico(int x, int y, BufferedImage sprite) {
		super(x, y, sprite);

	}

	public int mascaras;
	public int algemas;
	public boolean cha;
	
	
	public void transferirItens(PecaMedico med, Tabuleiro tab) {
		int yMed = med.pos[0];
		int xMed = med.pos[1];
		int yBau = -1; int xBau = -1;
		if(tab.vetorBaus[yMed+1][xMed-1]!= null){
			yBau = yMed+1;
			xBau = xMed-1;
		}
		else if	(tab.vetorBaus[yMed+1][xMed]!= null) {
			yBau = yMed+1;
			xBau = xMed;
		}
		else if	(tab.vetorBaus[yMed+1][xMed+1]!= null) {
			yBau = yMed+1;
			xBau = xMed+1;
		}
		else if	(tab.vetorBaus[yMed][xMed-1]!= null) {
			yBau = yMed;
			xBau = xMed-1;
		}
		else if	(tab.vetorBaus[yMed][xMed+1]!= null) {
			yBau = yMed;
			xBau = xMed+1;
		}
		else if	(tab.vetorBaus[yMed-1][xMed-1]!= null) {
			yBau = yMed-1;
			xBau = xMed-1;
		}
		else if	(tab.vetorBaus[yMed-1][xMed]!= null) {
			yBau = yMed-1;
			xBau = xMed;
		}
		else if	(tab.vetorBaus[yMed-1][xMed+1]!= null) {
			yBau = yMed-1;
			xBau = xMed+1;
		}
		if((yBau >= 0)&&(xBau>=0)&&(Math.abs(yMed-yBau) <2 && Math.abs(xMed-xBau) < 2)) {
			tab.vetorBaus[yBau][xBau].transferirItens(med, tab);
			
		}
	}
}
