package database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entidades.PecaBau;

public class ConjuntoBau {
public Map<String, DocInstanciaBau> vetor;
	
	public ConjuntoBau(List<PecaBau> instancias) {
		
		this.vetor = new HashMap<>();
		for(int i = 0; i < instancias.size(); i++) {
			vetor.put(String.valueOf(i), new DocInstanciaBau(instancias, i));
		}
	}
}
