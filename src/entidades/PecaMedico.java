package entidades;
import java.awt.image.BufferedImage;

import interfaces.*;
import main.Jogo;
import tabuleiro.Tabuleiro;

public class PecaMedico extends PecasMoveis implements ITransferir{

	public static BufferedImage PECA_MEDICO_B = Jogo.spritesheet.getSprite(0*Tabuleiro.DC, 1*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	public static BufferedImage PECA_MEDICO_P = Jogo.spritesheet.getSprite(3*Tabuleiro.DC, 1*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	
	public PecaMedico(int x, int y, BufferedImage sprite) {
		super(x, y, sprite);

	}

}
