package entidades;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import interfaces.*;
import main.Jogo;
import tabuleiro.Tabuleiro;
public class PecaCha implements ICriaCha, IGuardaCha{
	
	// Atributos do cha
	public int pos[];
	public PecaMedico med;
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
		if(!Tabuleiro.chaCriado)
			criaCha();
		verificarBau(Jogo.tabuleiro);
		if(med!= null) {
			this.pos[0] = med.pos[0];
			this.pos[1] = med.pos[1];
		}
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
		boolean criado = false;
		int xCha = 0;
		int yCha = 0;
		
		// determinar uma posição do chá dentro de um baú
		boolean existeBauFechado = false;
		for(int i = 0; i < Tabuleiro.entidadesBau.size(); i++) {
			if(!Tabuleiro.entidadesBau.get(i).aberto)
				existeBauFechado = true;
		}
		// se todos os baús já foram abertos
		if(!existeBauFechado) {
			for(int i = 0; i < Tabuleiro.entidadesBau.size(); i++) {
				Tabuleiro.entidadesBau.get(i).aberto = false;		// fecha todos os baús
				existeBauFechado = true;
			}
		}
		// se existem baús fechados ainda
		if(existeBauFechado) {
			while(!criado) {
				int i = Jogo.rand.nextInt(Tabuleiro.entidadesBau.size());
				if(!Tabuleiro.entidadesBau.get(i).aberto) {				// escolhe um baú fechado para colocar o cha
					xCha = Tabuleiro.entidadesBau.get(i).pos[1];
					yCha = Tabuleiro.entidadesBau.get(i).pos[0];
					criado = true;
				}				
			}
		}

		return new PecaCha(xCha, yCha, null);
	}
}
