package tabuleiro;

import java.awt.image.BufferedImage;

public class ParedeHospital extends Celulas {

	public ParedeHospital(int x, int y, BufferedImage sprite) {
		super(x, y, sprite);
		this.colisao = true;
	}

	
}
