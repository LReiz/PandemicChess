package tabuleiro;

import java.awt.image.BufferedImage;

public class ChaoRoxo extends Celulas {
	
	public ChaoRoxo(int x, int y, BufferedImage sprite) {
		super(x, y, sprite);
		this.colisao = false;
	}
}
