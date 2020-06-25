package entidades;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import interfaces.*;
import main.Jogo;
import tabuleiro.Tabuleiro;

public abstract class PecasMoveis implements IAtaque, IMovimento{

	public int pos[];
	private BufferedImage sprite;
	
	public PecasMoveis(int x, int y, BufferedImage sprite) {
		this.pos = new int[2];
		pos[0] = y;
		pos[1] = x;
		this.sprite = sprite;
	}
	
	public void att() {
		
	}
	
	public void renderizar(Graphics g) {
		g.drawImage(this.sprite, pos[1], pos[0], Tabuleiro.DC, Tabuleiro.DC, null);
	}
}
