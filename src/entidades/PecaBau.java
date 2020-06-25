package entidades;
import interfaces.*;
import tabuleiro.Tabuleiro;
public class PecaBau implements IGuardaCha, ITransferir{
	public int pos[];
	int mascaras;
	int algemas;
	boolean cha;
	
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
