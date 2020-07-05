package entidades;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;

import erros.BauVazio;
import erros.ChaNaoColetado;
import erros.ForaDeAlcance;
import erros.MuitoDistante;
import erros.NaoVazio;
import interfaces.*;
import main.Jogo;
import tabuleiro.Tabuleiro;

public abstract class PecasMoveis extends Peca implements IAtaque, IMovimento {

	// Atributos individuais de peças
	public int mascaras;
	public int algemas;
	public boolean cha;
	public int dir;					// 0: esquerda; 1: direita; 2: cima; 3: baixo
	public double speed = 1;
	public boolean movendo = false;
	public boolean finalizouMovimento;
	private BufferedImage sprite;
	
	// Atributos para Multiplayer
	public int indexNoVetor;
	
	// Animação das Peças Móveis
	private int maxAnimacoes = 2;
	private int atualAnimacao = 0;
	private int maxFramesAnimacao = 4;			// determina a velocidade da animação
	private int atualFramesAnimacao = 0;
	protected BufferedImage animacaoEsquerda[];
	protected BufferedImage animacaoDireita[];
	protected BufferedImage animacaoCima[];
	protected BufferedImage animacaoBaixo[];
	
	// Atributos para movimentação do time dos médicos
	public static boolean medicoSelecionado = false;
	public static PecasMoveis medicoAtual = null;
	public static int medicoAtualDirX = 0;
	public static int medicoAtualDirY = 0;
	public static int proxPosicaoMedicoX = 0;
	public static int proxPosicaoMedicoY = 0;
	public static int indexMedico = 0;
	
	// Atributos para movimentação do time dos infectados
	public static boolean infectadoSelecionado = false;
	public static PecasMoveis infectadoAtual = null;
	public static int infectadoAtualDirX = 0;
	public static int infectadoAtualDirY = 0;
	public static int proxPosicaoInfectadoX = 0;
	public static int proxPosicaoInfectadoY = 0;
	public static int indexInfectado = 0;	
	
	
	public PecasMoveis(int x, int y, BufferedImage sprite) {
		this.pos = new int[2];
		pos[0] = y;
		pos[1] = x;
		this.sprite = sprite;
	}
	
	public void att() throws NaoVazio, ForaDeAlcance, MuitoDistante, BauVazio, ChaNaoColetado {

	}
	
	public void renderizar(Graphics g) {
		if(movendo) {
			atualFramesAnimacao++;
			if(atualFramesAnimacao >= maxFramesAnimacao) {
				atualAnimacao++;
				atualFramesAnimacao = 0;
				if(atualAnimacao >= maxAnimacoes)
					atualAnimacao = 0;
			}
			
			if(dir == 0) {
				g.drawImage(this.animacaoEsquerda[atualAnimacao+1], pos[1], pos[0], Tabuleiro.DC, Tabuleiro.DC, null);
				sprite = animacaoEsquerda[0];
			} else if(dir == 1) {
				g.drawImage(this.animacaoDireita[atualAnimacao+1], pos[1], pos[0], Tabuleiro.DC, Tabuleiro.DC, null);
				sprite = animacaoDireita[0];
			} else if(dir == 2) {
				g.drawImage(this.animacaoCima[atualAnimacao+1], pos[1], pos[0], Tabuleiro.DC, Tabuleiro.DC, null);
				sprite = animacaoCima[0];
			} else if(dir == 3) {
				g.drawImage(this.animacaoBaixo[atualAnimacao+1], pos[1], pos[0], Tabuleiro.DC, Tabuleiro.DC, null);
				sprite = animacaoBaixo[0];
			}
			
		} else {
			g.drawImage(this.sprite, pos[1], pos[0], Tabuleiro.DC, Tabuleiro.DC, null);			
		}
	}
	
	public void renderizarSemPixelizar(Graphics g) {
		
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
			if(Jogo.multiplayerRemoto) {
				return false;
			} else {
				peca.movendo = false;
				throw new MuitoDistante("Movimento muito longo");
			}
		}
		
		if(x_final == pos[1] && y_final == pos[0])
			return false;
		
		if(tab.movimento(x_final, y_final, peca, tab)) {
			// execução do movimento
			if(x_dir < 0) {
				pos[1] -= speed;
				dir = 0;
				if(pos[1] <= x_final) {
					pos[1] = x_final;
					peca.finalizouMovimento = true;
					return false;
				}
				return true;
			} else if(x_dir > 0) {
				pos[1] += speed;
				dir = 1;
				if(pos[1] >= x_final) {
					pos[1] = x_final;
					peca.finalizouMovimento = true;
					return false;
				}
				return true;
			} else if(y_dir < 0) {
				pos[0] -= speed;
				dir = 2;
				if(pos[0] <= y_final) {
					pos[0] = y_final;
					peca.finalizouMovimento = true;
					return false;
				}
				return true;
			} else if(y_dir > 0) {
				pos[0] += speed;
				dir = 3;
				if(pos[0] >= y_final) {
					pos[0] = y_final;
					peca.finalizouMovimento = true;
					return false;
				}
				return true;
			}
		}
		return false;
	}
	
	public abstract void encontrarInimigo(Tabuleiro tab);
	
	public abstract void atacar(PecasMoveis inimigo,Tabuleiro tab);

	
	protected void atualizarVetorBau(int x_inicial, int y_inicial) {
		Jogo.tabuleiro.vetorPecasMoveis[(int)(y_inicial/Tabuleiro.DC)][(int)(x_inicial/Tabuleiro.DC)] = null;
		Jogo.tabuleiro.vetorPecasMoveis[(int)(pos[0]/Tabuleiro.DC)][(int)(pos[1]/Tabuleiro.DC)] = this;
	}
}
