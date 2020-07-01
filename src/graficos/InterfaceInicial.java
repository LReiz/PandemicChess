package graficos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import main.Jogo;

public class InterfaceInicial {

	public static String vetorModosDeJogo[] = {"local", "remoto"};
	public static int modoDeJogo;
	
	private static int maxRGB = 255;
	private static int R;
	private static int G = maxRGB;
	private static int B;
	
	public static void att() {		
		if(modoDeJogo >= vetorModosDeJogo.length) {
			modoDeJogo = 0;
		} else if(modoDeJogo < 0) {
			modoDeJogo = vetorModosDeJogo.length-1;
		}
		
		if(vetorModosDeJogo[modoDeJogo] == "local") {
			Jogo.multiplayer = false;
		} else if(vetorModosDeJogo[modoDeJogo] == "remoto") {
			Jogo.multiplayer = true;			
		}
	}
	
	public static void renderizar(Graphics g) {
		R++;
		G--;
		B++;
		if(R > maxRGB) {
			R = 0;
		} 
		if(G < 0) {
			G = maxRGB;
		} 
		if(B > maxRGB) {
			B = 0;
		}
		
		g.setColor(new Color(R, G, B));
		g.setFont(new Font("arial", 1, 15));
		g.drawString("Pandemic CHESS!!!", (Jogo.LARGURA-140)/2, 50);
	
		g.setFont(new Font("arial", 1, 10));
		
		g.drawString("Multiplayer (local)", (Jogo.LARGURA-140)/2, 150);
		g.drawString("Multiplayer (remoto)", (Jogo.LARGURA-140)/2, 170);
		
		g.setColor(new Color(0x0055EE));
		
		if(vetorModosDeJogo[modoDeJogo] == "local")
			g.drawString(">>", (Jogo.LARGURA-180)/2, 150);
		else if(vetorModosDeJogo[modoDeJogo] == "remoto")
			g.drawString(">>", (Jogo.LARGURA-180)/2, 170);
	}
}
