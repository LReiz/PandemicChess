package interfaces;

import entidades .*;
import erros.BauVazio;
import tabuleiro .*;
public interface ITransferir {
	void transferirItens(PecaMedico medico, Tabuleiro tab) throws BauVazio;
}
