package entidades;
import java.awt.image.BufferedImage;

import erros.BauVazio;
import erros.ForaDeAlcance;
import erros.MuitoDistante;
import erros.NaoVazio;
import interfaces.*;
import main.Jogo;
import tabuleiro.Tabuleiro;

public class PecaMedico extends PecasMoveis implements ITransferir{

	// sprites dos médicos
	public static BufferedImage PECA_MEDICO_B = Jogo.spritesheet.getSprite(0*Tabuleiro.DC, 1*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	public static BufferedImage PECA_MEDICO_P = Jogo.spritesheet.getSprite(3*Tabuleiro.DC, 1*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	
	// itens do médico
	public int mascaras = 3;
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
	
	public void att() throws NaoVazio, ForaDeAlcance, MuitoDistante, BauVazio {
		// atualização da peça no modo singleplayer
		if(!Jogo.multiplayer) {
			if(movendo) {
				movendo = movimento(PecasMoveis.proxPosicaoMedicoX, PecasMoveis.proxPosicaoMedicoY, this, Jogo.tabuleiro);
				if(!movendo) {
					atualizarVetorBau(pos[1] - (Tabuleiro.DC*PecasMoveis.medicoAtualDirX), pos[0] - (Tabuleiro.DC*PecasMoveis.medicoAtualDirY));
					PecasMoveis.medicoAtualDirX = 0;
					PecasMoveis.medicoAtualDirY = 0;
					Tabuleiro.trocarVez();
					PecasMoveis.medicoSelecionado = false;
				}
			}
			
		// atualização da peça no modo multiplayer
		} else if(Jogo.multiplayer) {
			if(Jogo.medDoc.vetor.get(String.valueOf(indexNoVetor)).movendo == "true") {
				if(movimento(Integer.parseInt(Jogo.medDoc.proxX), Integer.parseInt(Jogo.medDoc.proxY), this, Jogo.tabuleiro)) {
					movendo = true;
				} else {
					movendo = false;
				}
				if(!movendo) {
					atualizarVetorBau(pos[1] - (Tabuleiro.DC*PecasMoveis.medicoAtualDirX), pos[0] - (Tabuleiro.DC*PecasMoveis.medicoAtualDirY));
					PecasMoveis.medicoAtualDirX = 0;
					PecasMoveis.medicoAtualDirY = 0;
					Tabuleiro.trocarVez();
					PecasMoveis.medicoSelecionado = false;
				}
			}
		}
		
		transferirItens(this, Jogo.tabuleiro);
	}
	public PecasMoveis encontrarInimigo(Tabuleiro tab) {
		int yMed = this.pos[0];
		int xMed = this.pos[1];
	
		for(int i = -1;i<2;i++) {
			if(yMed+i < 0 || yMed+i > 16) continue;
			for(int j = -1;j<2;j++) {
				if(xMed+j < 0 || xMed+j > 16) continue;
				if(tab.vetorPecasMoveis[yMed+i][xMed+j]instanceof PecaInfectado) {
					return tab.vetorPecasMoveis[yMed+i][xMed+j];
				}
			}
		}
		return null;
	}
	public void transferirItens(PecaMedico med, Tabuleiro tab) throws BauVazio {
		int yMed = med.pos[0];
		int xMed = med.pos[1];
		int yBau = -1; int xBau = -1;
		
		for(int i = -1; i<2; i++) {
			if(yMed+i < 0 || yMed+i > 16) continue;
			for(int j = -1; j<2; j++) {
				if(xMed+j < 0 || xMed+j > 16) continue;
				if	(tab.vetorBaus[yMed+i][xMed+j]!= null) {
					tab.vetorBaus[yBau][xBau].transferirItens(med, tab);
				}	
			}
		}
	}
	
	public void atacar(PecasMoveis inimigo, Tabuleiro tab) {
		if (inimigo == null) return;
		if(this.algemas == 0) return;

		tab.atacar(inimigo, tab);
	}
	
}
