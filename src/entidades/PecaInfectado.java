package entidades;

import java.awt.image.BufferedImage;

import erros.BauVazio;
import erros.ForaDeAlcance;
import erros.MuitoDistante;
import erros.NaoVazio;
import main.Jogo;
import tabuleiro.Tabuleiro;

public class PecaInfectado extends PecasMoveis {

	// sprites dos infectados
	public static BufferedImage PECA_INFECTADO = Jogo.spritesheet.getSprite(0*Tabuleiro.DC, 4*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	
	public PecaInfectado(int x, int y, BufferedImage sprite) {
		super(x, y, sprite);
		animacaoEsquerda = new BufferedImage[3];
		for(int i = 0; i < 3; i++)
			animacaoEsquerda[i] = Jogo.spritesheet.getSprite(i*Tabuleiro.DC, 7*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
		
		animacaoDireita = new BufferedImage[3];
		for(int i = 0; i < 3; i++)
			animacaoDireita[i] = Jogo.spritesheet.getSprite(i*Tabuleiro.DC, 6*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
		
		animacaoCima = new BufferedImage[3];
		for(int i = 0; i < 3; i++)
			animacaoCima[i] = Jogo.spritesheet.getSprite(i*Tabuleiro.DC, 5*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
		
		animacaoBaixo = new BufferedImage[3];
		for(int i = 0; i < 3; i++)
			animacaoBaixo[i] = Jogo.spritesheet.getSprite(i*Tabuleiro.DC, 4*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);

	}
	
	public void att() throws NaoVazio, ForaDeAlcance, MuitoDistante, BauVazio {
		if(!Jogo.multiplayer) {
			if(movendo) {
				movendo = movimento(PecasMoveis.proxPosicaoInfectadoX, PecasMoveis.proxPosicaoInfectadoY, this, Jogo.tabuleiro);
				if(!movendo) {
					atualizarVetorBau(pos[1] - (Tabuleiro.DC*PecasMoveis.infectadoAtualDirX), pos[0] - (Tabuleiro.DC*PecasMoveis.infectadoAtualDirY));
					PecasMoveis.infectadoAtualDirX = 0;
					PecasMoveis.infectadoAtualDirY = 0;
					Tabuleiro.trocarVez();
					PecasMoveis.infectadoSelecionado = false;
				}
			}
		} else if(Jogo.multiplayer) {
			if(Jogo.infDoc.vetor.get(String.valueOf(indexNoVetor)).movendo == "true") {
				if(movimento(Integer.parseInt(Jogo.infDoc.proxX), Integer.parseInt(Jogo.infDoc.proxY), this, Jogo.tabuleiro)) {
					movendo = true;
				} else {
					movendo = false;
				}
				if(!movendo) {
					atualizarVetorBau(pos[1] - (Tabuleiro.DC*PecasMoveis.infectadoAtualDirX), pos[0] - (Tabuleiro.DC*PecasMoveis.infectadoAtualDirY));
					PecasMoveis.infectadoAtualDirX = 0;
					PecasMoveis.infectadoAtualDirY = 0;
					Tabuleiro.trocarVez();
					PecasMoveis.infectadoSelecionado = false;
				}
			}
		}
		
		super.att();
	}
	
	public PecasMoveis encontrarInimigo(Tabuleiro tab) {
		int xInfec = (int)(this.pos[1]/Tabuleiro.DC);
		int yInfec = (int)(this.pos[0]/Tabuleiro.DC);
		// yParede e xParede são as coordenadas de uma possível parede entre os dois
		int xParede = 0;
		int yParede = 0;
		for(int i = -2;i<3;i++) {
			if(yInfec+i < 0 || yInfec+i > 16) continue;
			for(int j = -2;j<3;j++) {
				if(xInfec+j < 0 || xInfec+j > 16) continue;
				if(tab.vetorPecasMoveis[yInfec+i][xInfec+j]instanceof PecaMedico) {
					
					// CASO EM QUE OS DOIS ESTÃO A MENOS DE DUAS CASA DE DISTANCIA
					
					if(Math.abs(i) <= 1 && Math.abs(j) <= 1) return tab.vetorPecasMoveis[yInfec+i][xInfec+j];
					
					// CASO EM QUE HÁ UMA PAREDE ENTRE OS DOIS
					
					else if(i % 2 == 0 && j % 2 == 0) {
						xParede = (xInfec+(xInfec+j))/2;
						yParede = (yInfec+(yInfec+i))/2;
					}
					else if((Math.abs(i) == 2 && Math.abs(j) == 1)
							||Math.abs(i) == 1 && Math.abs(j) == 2) {
						if(Math.abs(i)>Math.abs(j)) {
							if(i>0) yParede = yInfec + i - 1;
							else yParede = yInfec + i + 1;
							xParede = yInfec + j;
						}
						else {
							yParede = yInfec + i;
							if(j>0) xParede = yInfec + j - 1;
							else xParede = yInfec + j + 1;
						}
					}
					if(tab.vetorPecasMoveis[yParede][xParede]!= null && 
						!(tab.vetorPecasMoveis[yParede][xParede] instanceof PecasMoveis)
						&&!(tab.vetorBaus[yParede][xParede] instanceof PecaBau)) {
						return tab.vetorPecasMoveis[yInfec+i][xInfec+j];
					}
				}
			}

		}
		
		return null;
	}
	
	public void atacar(PecasMoveis inimigo,Tabuleiro tab) {
		if (inimigo == null) return;
		
		if((inimigo).mascaras > 0) 
			inimigo.mascaras -= 1;
		else
			tab.atacar(inimigo, tab);		
	}

}
