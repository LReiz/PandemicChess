package entidades;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;

import erros.ForaDeAlcance;
import erros.MuitoDistante;
import erros.NaoVazio;
import interfaces.*;
import main.Jogo;
import tabuleiro.Tabuleiro;

public abstract class PecasMoveis implements IAtaque, IMovimento {

	public int pos[];
	public double speed = 1;
	public boolean movendo = false;
	
	public static boolean medicoSelecionado = false;
	public static PecasMoveis medicoAtual = null;
	public static int medicoAtualDirX = 0;
	public static int medicoAtualDirY = 0;
	public static int proxPosicaoMedicoX = 0;
	public static int proxPosicaoMedicoY = 0;
	public static int indexMedico = 0;
	
	public static boolean infectadoSelecionado = false;
	public static PecasMoveis infectadoAtual = null;
	public static int infectadoAtualDirX = 0;
	public static int infectadoAtualDirY = 0;
	public static int proxPosicaoInfectadoX = 0;
	public static int proxPosicaoInfectadoY = 0;
	public static int indexInfectado = 0;	
	
	private BufferedImage sprite;
	
	public PecasMoveis(int x, int y, BufferedImage sprite) {
		this.pos = new int[2];
		pos[0] = y;
		pos[1] = x;
		this.sprite = sprite;
	}
	
	public void att() throws NaoVazio, ForaDeAlcance, MuitoDistante {

	}
	
	public void renderizar(Graphics g) {
		g.drawImage(this.sprite, pos[1], pos[0], Tabuleiro.DC, Tabuleiro.DC, null);
	}
	
	public static PecasMoveis mudarSelecaoDePeca(List<PecasMoveis> vetorPecas, int jogador, int dir) {
		if(jogador == 1) {				// médico
			indexMedico += dir;
			if(indexMedico < 0)
				indexMedico = vetorPecas.size() - 1;
			if(indexMedico >= vetorPecas.size())
				indexMedico = 0;
			
			return vetorPecas.get(indexMedico);
		} else if(jogador == 2) {		// infectado
			indexInfectado += dir;
			if(indexInfectado < 0)
				indexInfectado = vetorPecas.size() - 1;
			if(indexInfectado >= vetorPecas.size())
				indexInfectado = 0;
			
			return vetorPecas.get(indexInfectado);
		}
		return null;
	}
	
	@Override
	public boolean movimento(int x_final, int y_final, PecasMoveis peca, Tabuleiro tab) throws NaoVazio, ForaDeAlcance, MuitoDistante {
		int x_dir = x_final - pos[1];
		int y_dir = y_final - pos[0];

		if(Math.abs(x_dir) > Tabuleiro.DC || Math.abs(y_dir) > Tabuleiro.DC 
				|| (Math.abs(x_dir) > 0 && Math.abs(y_dir) > 0)) 
		{
			peca.movendo = false;
			throw new MuitoDistante("Movimento muito longo");
		}
		
		if(x_final == pos[1] && y_final == pos[0])
			return false;
		
		System.out.println(PecasMoveis.proxPosicaoMedicoX);
		System.out.println(PecasMoveis.proxPosicaoMedicoY);
		if(tab.movimento(x_final, y_final, peca, tab)) {
			// execução do movimento
			if(x_dir < 0) {
				pos[1] -= speed;
				if(pos[1] <= x_final) {
					pos[1] = x_final;
					return false;
				}
				return true;
			} else if(x_dir > 0) {
				pos[1] += speed;
				System.out.println(pos[1] + "g" + speed);
				if(pos[1] >= x_final) {
					pos[1] = x_final;
					return false;
				}
				return true;
			} else if(y_dir < 0) {
				pos[0] -= speed;
				if(pos[0] <= y_final) {
					pos[0] = y_final;
					return false;
				}
				return true;
			} else if(y_dir > 0) {
				pos[0] += speed;
				if(pos[0] >= y_final) {
					pos[0] = y_final;
					return false;
				}
				return true;
			}
		}
		return false;
	}
}
