package entidades;

import java.awt.image.BufferedImage;

import erros.BauVazio;
import erros.ChaNaoColetado;
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
	int f=0;
	public void att() throws NaoVazio, ForaDeAlcance, MuitoDistante, BauVazio, ChaNaoColetado {
		// atualização da peça no modo multiplayer local
		if(!Jogo.multiplayerRemoto) {
			if(movendo) {
				movendo = movimento(PecasMoveis.proxPosicaoInfectadoX, PecasMoveis.proxPosicaoInfectadoY, this, Jogo.tabuleiro);
				if(!movendo) {
					atualizarVetorBau(pos[1] - (Tabuleiro.DC*PecasMoveis.infectadoAtualDirX), pos[0] - (Tabuleiro.DC*PecasMoveis.infectadoAtualDirY));
					PecasMoveis.infectadoSelecionado = false;
					Jogo.tabuleiro.trocarVez = true;
				}
			}
		// atualização da peça no modo multiplayer remoto
		} else 
			if(Jogo.multiplayerRemoto) {
			if(movendo) {
				movendo = movimento(PecasMoveis.proxPosicaoInfectadoX, PecasMoveis.proxPosicaoInfectadoY, this, Jogo.tabuleiro);
				if(!movendo && finalizouMovimento) {
					atualizarVetorBau(pos[1] - (Tabuleiro.DC*PecasMoveis.infectadoAtualDirX), pos[0] - (Tabuleiro.DC*PecasMoveis.infectadoAtualDirY));
					PecasMoveis.infectadoSelecionado = false;
					finalizouMovimento = false;
					Jogo.tabuleiro.trocarVez = true;
				}
			}
		}
		
		super.att();
	}
	
	public void encontrarInimigo(Tabuleiro tab) {
		int xInf = (int)(this.pos[1]/Tabuleiro.DC);
		int yInf = (int)(this.pos[0]/Tabuleiro.DC);
		PecasMoveis[] vetorInimigos = new PecasMoveis[24];
		int len = 0;
		// yParede e xParede são as coordenadas de uma possível parede entre os dois
		int xParede = 0;
		int yParede = 0;
		for(int yy = -2;yy<3;yy++) {
			if(yInf+yy < 0 || yInf+yy >= Tabuleiro.DC) continue;
			for(int xx = -2;xx<3;xx++) {
				if(xInf+xx < 0 || xInf+xx >= Tabuleiro.DC) continue;
				if(tab.vetorPecasMoveis[yInf+yy][xInf+xx] instanceof PecaMedico) {	

					if(Math.abs(yy) % 2 == 0 && Math.abs(xx) % 2 == 0) {
						xParede = (xInf+(xInf+xx))/2;
						yParede = (yInf+(yInf+yy))/2;

					}
					else if((Math.abs(yy) == 2 && Math.abs(xx) == 1)
							||Math.abs(yy) == 1 && Math.abs(xx) == 2) {
						if(Math.abs(yy)>Math.abs(xx)) {
							if(yy>0) yParede = yInf + yy - 1;
							else yParede = xInf + yy + 1;
							xParede = xInf;
						}
						else {
							yParede = yInf;
							if(xx>0) xParede = xInf + xx - 1;
							else xParede = xInf + xx + 1;
						}
					}
					
					// CASO EM QUE OS DOIS ESTÃO A MENOS DE DUAS CASA DE DISTANCIA
					
					if(Math.abs(yy) <= 1 && Math.abs(xx) <= 1) {
						vetorInimigos[len] = (tab.vetorPecasMoveis[yInf+yy][xInf+xx]);
						len++;
					}
					
					// CASO EM QUE HÁ UMA PAREDE ENTRE OS DOIS
					else if(!(tab.vetorCelulas[yParede][xParede].colisao)) {
							vetorInimigos[len] = (tab.vetorPecasMoveis[yInf+yy][xInf+xx]);
							len++;

						}
					
				}
			}
		}
		
		for(int i = 0; i < len; i++) {
			atacar(vetorInimigos[i],tab);
		}
	}
	
	public void atacar(PecasMoveis inimigo,Tabuleiro tab) {
		if (inimigo == null) return;
		if(inimigo.mascaras > 0) {

			inimigo.mascaras -= 1;
			return;
		}
		else
			tab.atacar(inimigo, tab);		
	}

}
