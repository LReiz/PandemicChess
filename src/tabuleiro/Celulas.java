package tabuleiro;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Jogo;

public class Celulas {

	public int pos[];
	public boolean colisao;
	public BufferedImage sprite;
	
	public static BufferedImage CELULA_CHAO_ROXO = Jogo.spritesheet.getSprite(0*Tabuleiro.DC, 8*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	public static BufferedImage CELULA_PAREDE_TIJOLOS = Jogo.spritesheet.getSprite(0*Tabuleiro.DC, 9*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	
	public static BufferedImage CELULA_PAREDE_HOSPITAL1 = Jogo.spritesheet.getSprite(1*Tabuleiro.DC, 9*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	public static BufferedImage CELULA_PAREDE_HOSPITAL2 = Jogo.spritesheet.getSprite(2*Tabuleiro.DC, 9*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	public static BufferedImage CELULA_PAREDE_HOSPITAL3 = Jogo.spritesheet.getSprite(3*Tabuleiro.DC, 9*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	public static BufferedImage CELULA_PAREDE_HOSPITAL4 = Jogo.spritesheet.getSprite(4*Tabuleiro.DC, 9*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	
	public Celulas(int x, int y, BufferedImage sprite) {
		this.pos = new int[2];
		this.pos[0] = y;
		this.pos[1] = x;
		this.sprite = sprite;
	}
	
	public void att() {
		
	}
	
	public void renderizar(Graphics g) {
		g.drawImage(this.sprite, this.pos[1], this.pos[0], Tabuleiro.DC, Tabuleiro.DC, null);
	}
}
