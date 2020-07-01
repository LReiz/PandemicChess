package database;

import java.util.List;

import entidades.Peca;

public class DocInstancia {

	public String x;
	public String y;
	
	public DocInstancia(List<Peca> vetorPecas, int index) {
		this.x = String.valueOf(vetorPecas.get(index).pos[1]);
		this.y = String.valueOf(vetorPecas.get(index).pos[0]);
	}
}
