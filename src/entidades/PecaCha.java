package entidades;
import java.awt.image.BufferedImage;

import interfaces.*;
import main.Jogo;
import tabuleiro.Tabuleiro;
public class PecaCha implements ICriaCha, IGuardaCha{
	
	public int pos[];
	
	public BufferedImage PECA_CHA = Jogo.spritesheet.getSprite(7*Tabuleiro.DC, 8*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	
	public void verificarBau(Tabuleiro tab) {
		int yCha = this.pos[0];
		int xCha = this.pos[1];
		if(tab.vetorBaus[yCha][xCha]!= null) {
			tab.vetorBaus[yCha][xCha].verificarBau(tab);
		}
		else if(tab.vetorPecasMoveis[yCha][xCha]== null) {
			
			// RENDERIZAR O CHA NO CHÃO
			
			
		}
	}

	@Override
	public void criaCha(Tabuleiro tab) {
		
		
	}
}
