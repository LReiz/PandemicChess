package entidades;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import erros.BauVazio;
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
	public static BufferedImage ITENS_MEDICOS_SEM_CHA = Jogo.spritesheet.getSprite(8*Tabuleiro.DC, 8*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC-7);
	public static BufferedImage ITENS_MEDICOS_COM_CHA = Jogo.spritesheet.getSprite(8*Tabuleiro.DC, 8*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);

	public PecaMedico(int x, int y, BufferedImage sprite) {
		super(x, y, sprite);
		this.mascaras = 3;
	
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
		// atualiza��o da pe�a no modo multiplayer local
		if(!Jogo.multiplayerRemoto) {
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
			
		// atualiza��o da pe�a no modo multiplayer remoto
		} else 
			if(Jogo.multiplayerRemoto) {
			if(Jogo.medDoc.vetor.get(String.valueOf(indexNoVetor)).movendo == "true") {
				if(Jogo.medDoc.proxX != "null" && Jogo.medDoc.proxY != "null") {
					if(movimento(Integer.parseInt(Jogo.medDoc.proxX), Integer.parseInt(Jogo.medDoc.proxY), this, Jogo.tabuleiro)) {
						movendo = true;
					} else {
						movendo = false;
					}					
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
		
		super.att();
		transferirItens(this, Jogo.tabuleiro);
	}
	
	public void renderizar(Graphics g) {
		super.renderizar(g);

		if(!cha)
			g.drawImage(ITENS_MEDICOS_SEM_CHA, pos[1]+14, pos[0], Tabuleiro.DC, Tabuleiro.DC-7, null);
		else if(cha)
			g.drawImage(ITENS_MEDICOS_COM_CHA, pos[1]+14, pos[0], Tabuleiro.DC, Tabuleiro.DC, null);
			
	}
	
	public void renderizarSemPixelizar(Graphics g) {
		g.setColor(new Color(0xFFFFFF));
		g.setFont(new Font("arial", 1, 12));
		g.drawString(String.valueOf(mascaras), (pos[1]+24)*Jogo.ESCALA, (pos[0]+2)*Jogo.ESCALA);
		g.drawString(String.valueOf(algemas), (pos[1]+24)*Jogo.ESCALA, (pos[0]+8)*Jogo.ESCALA);

	}
	
	public PecasMoveis encontrarInimigo(Tabuleiro tab) {
		int yMed = (int)(this.pos[0]/Tabuleiro.DC);
		int xMed = (int)(this.pos[1]/Tabuleiro.DC);
	
		for(int yy = -1;yy<2;yy++) {
			if(yMed+yy < 0 || yMed+yy >= Tabuleiro.DC) continue;
			for(int xx = -1;xx<2;xx++) {
				if(xMed+xx < 0 || xMed+xx >= Tabuleiro.DC) continue;
				if(tab.vetorPecasMoveis[yMed+yy][xMed+xx] instanceof PecaInfectado) {
					return tab.vetorPecasMoveis[yMed+yy][xMed+xx];
				}
			}
		}
		return null;
	}

	public void transferirItens(PecaMedico med, Tabuleiro tab) throws BauVazio {
		int xMed = (int)(med.pos[1]/Tabuleiro.DC);
		int yMed = (int)(med.pos[0]/Tabuleiro.DC); 

		
		for(int yy = -1; yy<2; yy++) {
			if(yMed+yy < 0 || yMed+yy >= Tabuleiro.DC) continue;
			
			for(int xx = -1; xx<2; xx++) {
				if(xMed+xx < 0 || xMed+xx >= Tabuleiro.DC) continue;
				if(tab.vetorBaus[yMed+yy][xMed+xx] != null) {
					tab.vetorBaus[yMed+yy][xMed+xx].transferirItens(med, tab);
				}	
			}
		}
	}
	
	public void atacar(PecasMoveis inimigo, Tabuleiro tab) {
		if(inimigo == null) return;
		if(this.algemas == 0) return;

		tab.atacar(inimigo, tab);
	}
	
}
