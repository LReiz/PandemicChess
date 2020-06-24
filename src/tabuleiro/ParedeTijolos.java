package tabuleiro;

import java.awt.image.BufferedImage;

public class ParedeTijolos extends Celulas {

	public ParedeTijolos(int x, int y, BufferedImage sprite) {
		super(x, y, sprite);
		this.colisao = true;
	}
}
