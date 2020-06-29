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
	public PecasMoveis encontrarInimigo(Tabuleiro tab) {
		int yMed = this.pos[0];
		int xMed = this.pos[1];
		for(int i = -1;i<2;i++) {
			for(int j = -1;j<2;j++) {
				if(xMed+j < 0 || xMed+j > 16) continue;
				if(tab.vetorPecasMoveis[yMed+i][xMed+j]instanceof PecaInfectado) {
					return tab.vetorPecasMoveis[yMed+i][xMed+j];
				}
			}
			if(yMed+i < 0 || yMed+i > 16) continue;
		}
		return null;
	}
	public void transferirItens(PecaMedico med, Tabuleiro tab) {
		int yMed = med.pos[0];
		int xMed = med.pos[1];
		int yBau = -1; int xBau = -1;
		
		for(int i = -1; i<2; i++) {
			for(int j = -1; j<2; j++) {
				if(xMed+j < 0 || xMed+j > 16) continue;
				if	(tab.vetorBaus[yMed+i][xMed+j]!= null) {
					tab.vetorBaus[yBau][xBau].transferirItens(med, tab);
				}	
			}
			if(yMed+i < 0 || yMed+i > 16) continue;
		}
	}
	
	public void atacar(PecasMoveis inimigo, Tabuleiro tab) {
		if (inimigo == null) return;
		if(this.algemas == 0) return;
			
		tab.atacar(inimigo, tab);
	}
	
}
