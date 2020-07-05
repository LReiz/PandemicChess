package database;

import java.util.List;

import entidades.PecaBau;

public class DocInstanciaBau {
	public String x;
	public String y;
	public String mascaras;
	public String algemas;
	
	public DocInstanciaBau(List<PecaBau> vetorPecas, int index) {
		this.x = String.valueOf(vetorPecas.get(index).pos[1]);
		this.y = String.valueOf(vetorPecas.get(index).pos[0]);
		this.mascaras = String.valueOf(vetorPecas.get(index).mascaras);
		this.algemas = String.valueOf(vetorPecas.get(index).algemas);
		
	}
}
