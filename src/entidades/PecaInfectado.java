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

}
