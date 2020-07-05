package erros;

public class ErroMovimento extends Exception {
    public ErroMovimento() {
      super();
   }

   public ErroMovimento(String msg) {
      super(msg);

   }
}