package entidades;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import interfaces.*;
import main.Jogo;
import tabuleiro.Tabuleiro;
public class PecaCha implements ICriaCha, IGuardaCha{
	
	// Atributos do cha
	public int pos[];
	private BufferedImage sprite;
	
	// sprites do cha
	public static BufferedImage PECA_CHA = Jogo.spritesheet.getSprite(7*Tabuleiro.DC, 8*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	
	public PecaCha(int x, int y, BufferedImage sprite) {
		this.pos = new int[2];
		this.pos[1] = x;
		this.pos[0] = y;
		this.sprite = sprite;
	}
	
	public void att() {
		criaCha();
		verificarBau(Jogo.tabuleiro);
	}
	
	public void renderizar(Graphics g) {
		g.drawImage(this.sprite, pos[1], pos[0], Tabuleiro.DC, Tabuleiro.DC, null);
	}
	
	public void verificarBau(Tabuleiro tab) {
		
		int yCha = (int)(this.pos[0]/Tabuleiro.DC);
		int xCha = (int)(this.pos[1]/Tabuleiro.DC);
		
		if(tab.vetorBaus[yCha][xCha] != null) {
			tab.vetorBaus[yCha][xCha].verificarBau(tab);
		} else if(tab.vetorBaus[yCha][xCha] == null) {
			// RENDERIZAR O CHA NO CHÃO
			sprite = PECA_CHA;
		}
	}

	@Override
	public PecaCha criaCha() {
		if(Tabuleiro.rodada > Tabuleiro.rodadaCriaCha) {
			return new PecaCha(0, 0, null);
		}
		return null;
	}
}
