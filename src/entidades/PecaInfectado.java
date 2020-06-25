package entidades;

import java.awt.image.BufferedImage;

import main.Jogo;
import tabuleiro.Tabuleiro;

public class PecaInfectado extends PecasMoveis {

	public static BufferedImage PECA_INFECTADO = Jogo.spritesheet.getSprite(0*Tabuleiro.DC, 4*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	
	public PecaInfectado(int x, int y, BufferedImage sprite) {
		super(x, y, sprite);

	}

}
