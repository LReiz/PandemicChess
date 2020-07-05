package database;

import java.util.List;

import entidades.PecasMoveis;

public class DocInstanciaPeca {

	public String x;
	public String y;
	
	public DocInstanciaPeca(List<PecasMoveis> vetorPecas, int index) {
		this.x = String.valueOf(vetorPecas.get(index).pos[1]);
		this.y = String.valueOf(vetorPecas.get(index).pos[0]);
		
	}
}
