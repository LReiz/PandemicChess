package database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entidades.PecasMoveis;

public class ConjuntoPeca {

	public Map<String, DocInstanciaPeca> vetor;
	
	public ConjuntoPeca(List<PecasMoveis> instancias) {
		
		this.vetor = new HashMap<>();
		for(int i = 0; i < instancias.size(); i++) {
			vetor.put(String.valueOf(i), new DocInstanciaPeca(instancias, i));
		}
	}
}
