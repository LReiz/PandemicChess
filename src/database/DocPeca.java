package database;

public class DocPeca {

	public String movendo;
	public boolean movendoBool;
	
	public DocPeca(boolean movendo) {
		if(movendo)
			this.movendo = "1";
		else
			this.movendo = "0";
		this.movendoBool = movendo;
	}
}
