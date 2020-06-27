package entidades;

import java.awt.image.BufferedImage;

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
	
	public void att() throws NaoVazio, ForaDeAlcance, MuitoDistante {
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
	}
	

}
