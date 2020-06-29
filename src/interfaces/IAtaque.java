package interfaces;

import entidades.PecasMoveis;
import tabuleiro.Tabuleiro;

public interface IAtaque {
	void atacar(PecasMoveis inimigo,Tabuleiro tab);
}
