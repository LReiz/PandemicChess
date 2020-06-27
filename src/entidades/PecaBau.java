package entidades;
import java.awt.image.BufferedImage;

import interfaces.*;
import main.Jogo;
import tabuleiro.Tabuleiro;
public class PecaBau implements IGuardaCha, ITransferir {
	public int pos[];
	int mascaras;
	int algemas;
	boolean cha;
	
	public BufferedImage PECA_BAU = Jogo.spritesheet.getSprite(5*Tabuleiro.DC, 8*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	public BufferedImage PECA_BAU_ABERTO = Jogo.spritesheet.getSprite(6*Tabuleiro.DC, 8*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	
	public PecaBau(int x, int y, int mascaras, int algemas) {
		this.pos = new int[2];
		this.pos[1] = x;
		this.pos[0] = y;
		this.mascaras = mascaras;
		this.algemas = algemas;
	}
	
	public void transferirItens(PecaMedico med, Tabuleiro tab) {
		med.mascaras = this.mascaras;
		med.algemas = this.algemas;
		if(cha == true) {
			med.cha = true;
		}
		this.algemas = 0;
		this.mascaras = 0;
		
	}
	
	public void verificarBau(Tabuleiro tab) {
		this.cha = true;
	}
}
