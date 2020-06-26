package entidades;
import java.awt.image.BufferedImage;

import erros.ForaDeAlcance;
import erros.MuitoDistante;
import erros.NaoVazio;
import interfaces.*;
import main.Jogo;
import tabuleiro.Tabuleiro;

public class PecaMedico extends PecasMoveis implements ITransferir{

	public static BufferedImage PECA_MEDICO_B = Jogo.spritesheet.getSprite(0*Tabuleiro.DC, 1*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	public static BufferedImage PECA_MEDICO_P = Jogo.spritesheet.getSprite(3*Tabuleiro.DC, 1*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	
	public int mascaras;
	public int algemas;
	public boolean cha;

	public PecaMedico(int x, int y, BufferedImage sprite) {
		super(x, y, sprite);
		if(sprite == PECA_MEDICO_B) {
			animacaoEsquerda = new BufferedImage[3];
			for(int i = 0; i < 3; i++)
				animacaoEsquerda[i] = Jogo.spritesheet.getSprite(i*Tabuleiro.DC, 3*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
			
			animacaoDireita = new BufferedImage[3];
			for(int i = 0; i < 3; i++)
				animacaoDireita[i] = Jogo.spritesheet.getSprite(i*Tabuleiro.DC, 2*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
			
			animacaoCima = new BufferedImage[3];
			for(int i = 0; i < 3; i++)
				animacaoCima[i] = Jogo.spritesheet.getSprite(i*Tabuleiro.DC, 1*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
			
			animacaoBaixo = new BufferedImage[3];
			for(int i = 0; i < 3; i++)
				animacaoBaixo[i] = Jogo.spritesheet.getSprite(i*Tabuleiro.DC, 0*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
		} else if(sprite == PECA_MEDICO_P) {
			animacaoEsquerda = new BufferedImage[3];
			for(int i = 0; i < 3; i++)
				animacaoEsquerda[i] = Jogo.spritesheet.getSprite((i+3)*Tabuleiro.DC, 3*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
			
			animacaoDireita = new BufferedImage[3];
			for(int i = 0; i < 3; i++)
				animacaoDireita[i] = Jogo.spritesheet.getSprite((i+3)*Tabuleiro.DC, 2*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
			
			animacaoCima = new BufferedImage[3];
			for(int i = 0; i < 3; i++)
				animacaoCima[i] = Jogo.spritesheet.getSprite((i+3)*Tabuleiro.DC, 1*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
			
			animacaoBaixo = new BufferedImage[3];
			for(int i = 0; i < 3; i++)
				animacaoBaixo[i] = Jogo.spritesheet.getSprite((i+3)*Tabuleiro.DC, 0*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
		}
	}
	
	public void att() throws NaoVazio, ForaDeAlcance, MuitoDistante {
		if(movendo) {
			movendo = movimento(PecasMoveis.proxPosicaoMedicoX, PecasMoveis.proxPosicaoMedicoY, this, Jogo.tabuleiro);
			if(!movendo) {
				PecasMoveis.infectadoAtualDirX = 0;
				PecasMoveis.infectadoAtualDirY = 0;
				Tabuleiro.trocarVez();
				PecasMoveis.medicoSelecionado = false;
			}
		}
	}
	
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
