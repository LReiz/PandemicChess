package entidades;
import interfaces.*;
import tabuleiro.Tabuleiro;
public class PecaCha implements ICriaCha, IGuardaCha{
	public int pos[];
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
}
