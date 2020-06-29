package entidades;
import java.awt.image.BufferedImage;

import erros.ForaDeAlcance;
import erros.MuitoDistante;
import erros.NaoVazio;
import interfaces.*;
import main.Jogo;
import tabuleiro.Tabuleiro;

public class PecaMedico extends PecasMoveis implements ITransferir{

	// sprites dos m�dicos
	public static BufferedImage PECA_MEDICO_B = Jogo.spritesheet.getSprite(0*Tabuleiro.DC, 1*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	public static BufferedImage PECA_MEDICO_P = Jogo.spritesheet.getSprite(3*Tabuleiro.DC, 1*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	
	// itens do m�dico
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
	
	public void att() throws NaoVazio, ForaDeAlcance, MuitoDistante {
		// atualiza��o da pe�a no modo singleplayer
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
			
		// atualiza��o da pe�a no modo multiplayer
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
	
	public void transferirItens(PecaMedico med, Tabuleiro tab) {
		int yMed = (int)(med.pos[0]/Tabuleiro.DC);
		int xMed = (int)(med.pos[1]/Tabuleiro.DC);
		int yBau = -1; 
		int xBau = -1;
		if(yMed+1 >= 0 && xMed-1 >= 0 
				&& yMed+1 < tab.alturaMapa && xMed-1 < tab.larguraMapa
				&& tab.vetorBaus[yMed+1][xMed-1]!= null){
			yBau = yMed+1;
			xBau = xMed-1;
		}
		else if	(yMed+1 >= 0 && xMed >= 0 
					&& yMed+1 < tab.alturaMapa && xMed < tab.larguraMapa
					&& tab.vetorBaus[yMed+1][xMed]!= null) {
			yBau = yMed+1;
			xBau = xMed;
		}
		else if	(yMed+1 >= 0 && xMed+1 >= 0 
					&& yMed+1 < tab.alturaMapa && xMed+1 < tab.larguraMapa
					&& tab.vetorBaus[yMed+1][xMed+1]!= null) {
			yBau = yMed+1;
			xBau = xMed+1;
		}
		else if	(yMed >= 0 && xMed-1 >= 0 
					&& yMed < tab.alturaMapa && xMed-1 < tab.larguraMapa
					&& tab.vetorBaus[yMed][xMed-1]!= null) {
			yBau = yMed;
			xBau = xMed-1;
		}
		else if	(yMed >= 0 && xMed+1 >= 0 
					&& yMed < tab.alturaMapa && xMed+1 < tab.larguraMapa
					&& tab.vetorBaus[yMed][xMed+1]!= null) {
			yBau = yMed;
			xBau = xMed+1;
		}
		else if	(yMed-1 >= 0 && xMed-1 >= 0 
				&& yMed-1 < tab.alturaMapa && xMed-1 < tab.larguraMapa
				&& tab.vetorBaus[yMed-1][xMed-1]!= null) {
			yBau = yMed-1;
			xBau = xMed-1;
		}
		else if	(yMed-1 >= 0 && xMed >= 0 
					&& yMed-1 < tab.alturaMapa && xMed < tab.larguraMapa
					&& tab.vetorBaus[yMed-1][xMed]!= null) {
			yBau = yMed-1;
			xBau = xMed;
		}
		else if	(yMed-1 >= 0 && xMed+1 >= 0 
				&& yMed-1 < tab.alturaMapa && xMed+1 < tab.larguraMapa
				&& tab.vetorBaus[yMed-1][xMed+1]!= null) {
			yBau = yMed-1;
			xBau = xMed+1;
		}
		if(yBau >= 0 && xBau >= 0 && xBau < tab.larguraMapa 
				&& yBau < tab.alturaMapa
				&& tab.vetorBaus[yBau][xBau] != null
				&& tab.vetorBaus[yBau][xBau].aberto == false) {
			tab.vetorBaus[yBau][xBau].transferirItens(med, tab);
			
		}
	}
}
