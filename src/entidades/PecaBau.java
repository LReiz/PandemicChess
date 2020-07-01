package entidades;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import erros.BauVazio;
import interfaces.*;
import main.Jogo;
import tabuleiro.Tabuleiro;
public class PecaBau implements IGuardaCha, ITransferir {
	
	// Atributos individuais dos baus
	public int pos[];
	public BufferedImage sprite;
	public int mascaras;
	public int algemas;
	public boolean cha;
	public boolean aberto;
	
	// sprites dos baus
	public static BufferedImage PECA_BAU = Jogo.spritesheet.getSprite(5*Tabuleiro.DC, 8*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	public static BufferedImage PECA_BAU_ABERTO = Jogo.spritesheet.getSprite(6*Tabuleiro.DC, 8*Tabuleiro.DC, Tabuleiro.DC, Tabuleiro.DC);
	
	public PecaBau(int x, int y, int mascaras, int algemas,  BufferedImage sprite) {
		this.pos = new int[2];
		this.pos[1] = x;
		this.pos[0] = y;
		this.sprite = sprite;
		this.mascaras = mascaras;
		this.algemas = algemas;
	}
	
	public void transferirItens(PecaMedico med, Tabuleiro tab) throws BauVazio {
		// acrescenta os itens no médico correspondente
		if(this.mascaras == 0 && this.algemas == 0 && this.cha == false) {
			throw new BauVazio("O Baú está vazio!");
		}
		med.mascaras += this.mascaras;
		med.algemas += this.algemas;
		if(this.cha == true) {
			med.cha = true;
			tab.pecaCha.med = med;
			this.cha = false;
		}
		
		// zera seus itens
		this.algemas = 0;
		this.mascaras = 0;
		aberto = true;
		
	}
	
	public void verificarBau(Tabuleiro tab) {
		this.cha = true;
	}
	
	public void att() {
		if(aberto) {
			sprite = PECA_BAU_ABERTO;
		} else {
			sprite = PECA_BAU;
		}
	}
	
	public void renderizar(Graphics g) {
		g.drawImage(this.sprite, pos[1], pos[0], Tabuleiro.DC, Tabuleiro.DC, null);
	}
}
