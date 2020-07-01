package database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Conjunto {

	public Map<String, DocInstancia> vetor;
	
	public Conjunto(List instancias) {
		
		this.vetor = new HashMap<>();
		for(int i = 0; i < instancias.size(); i++) {
			vetor.put(String.valueOf(i), new DocInstancia(instancias, i));
		}
	}
}
