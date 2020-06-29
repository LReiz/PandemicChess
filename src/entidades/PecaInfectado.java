package entidades;

import java.awt.image.BufferedImage;

import erros.ForaDeAlcance;
import erros.MuitoDistante;
import erros.NaoVazio;
import main.Jogo;
import tabuleiro.Tabuleiro;

public class PecaInfectado extends PecasMoveis {

	public static BufferedImage PECA_INFECTADO = Jogo.spritesheet.getSprite(0*Tabuleiro.DC, 4*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	
	
	
	public PecaInfectado(int x, int y, BufferedImage sprite) {
		super(x, y, sprite);

	}
	
	public void att() throws NaoVazio, ForaDeAlcance, MuitoDistante {
		if(movendo) {
			movendo = movimento(PecasMoveis.proxPosicaoInfectadoX, PecasMoveis.proxPosicaoInfectadoY, this, Jogo.tabuleiro);
			if(!movendo) {
				PecasMoveis.infectadoAtualDirX = 0;
				PecasMoveis.infectadoAtualDirY = 0;
				Tabuleiro.trocarVez();
				PecasMoveis.infectadoSelecionado = false;
			}
		}
	}
	
	public PecasMoveis encontrarInimigo(Tabuleiro tab) {
		int yInfec = this.pos[0];
		int xInfec = this.pos[1];
		int yParede;
		int xParede;
		for(int i = -2;i<3;i++) {
			for(int j = -2;j<3;j++) {
				if(xInfec+j < 0 || xInfec+j > 16) continue;
				if(tab.vetorPecasMoveis[yInfec+i][xInfec+j]instanceof PecaMedico) {
					yParede = (yInfec+(yInfec+i))/2;
					xParede = (xInfec+(xInfec+j))/2;
					if(tab.vetorPecasMoveis[yParede][xParede]!= null && 
						!(tab.vetorPecasMoveis[yParede][xParede] instanceof PecasMoveis)
						&&!(tab.vetorBaus[yParede][xParede] instanceof PecaBau)) {
						return tab.vetorPecasMoveis[yInfec+i][xInfec+j];
					}
				}
			}
			if(yInfec+i < 0 || yInfec+i > 16) continue;
		}
		
		
		return null;
	}
	public void atacar(PecasMoveis inimigo,Tabuleiro tab) {
		if (inimigo == null) return;
		
		if((inimigo).mascaras > 0) inimigo.mascaras -= 1;
		
		else tab.atacar(inimigo, tab);		
	}
}
