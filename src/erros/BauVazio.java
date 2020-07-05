package erros;

public class BauVazio extends Exception{
	public BauVazio() {
		super();
	}
	public BauVazio(String msg) {
		super(msg);
	}
}
