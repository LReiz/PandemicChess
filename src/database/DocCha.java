package database;

public class DocCha {

	public String chaCriado = "0";
	public String x;
	public String y;
	
	public DocCha(int x, int y) {
		this.x = String.valueOf(x);
		this.y = String.valueOf(y);
		System.out.println("cha criado pos x: " + x + " " + y);
	}
}
