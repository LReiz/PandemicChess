package graficos;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Spritesheet {

	BufferedImage spritesheet;
	
	public Spritesheet(String endereco) {
	
			try {
				spritesheet = ImageIO.read(getClass().getResource(endereco));
			} catch (IOException e) {
				e.printStackTrace();
			}			
	}
	
	public BufferedImage getSprite(int x, int y, int largura, int altura) {
		return (spritesheet.getSubimage(x, y, largura, altura));
	}
	
}