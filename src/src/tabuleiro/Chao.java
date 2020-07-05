package tabuleiro;

import java.awt.image.BufferedImage;

public class Chao extends Celulas {
	
	public Chao(int x, int y, BufferedImage sprite) {
		super(x, y, sprite);
		this.colisao = false;
	}
}
