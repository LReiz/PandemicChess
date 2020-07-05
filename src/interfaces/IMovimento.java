package interfaces;

import entidades.PecasMoveis;
import erros.ForaDeAlcance;
import erros.MuitoDistante;
import erros.NaoVazio;
import tabuleiro.Tabuleiro;

public interface IMovimento {

	public boolean movimento(int x_final, int y_final, PecasMoveis peca, Tabuleiro tab) throws NaoVazio, ForaDeAlcance, MuitoDistante;
}
