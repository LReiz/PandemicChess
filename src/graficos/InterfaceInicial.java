package graficos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import main.Jogo;

public class InterfaceInicial {

	public static String vetorModosDeJogo[] = {"local", "remoto"};
	public static String vetorTimes[] = {"medicos", "infectados"};
	public static int modoDeJogo;
	public static int time;
	
	private static int maxRGB = 240;
	private static int R;
	private static int G = maxRGB;
	private static int B;
	
	private static boolean VerdeParaRosa = true;
	
	public static void att() {
		// modo de jogo
		if(modoDeJogo >= vetorModosDeJogo.length) {
			modoDeJogo = 0;
		} else if(modoDeJogo < 0) {
			modoDeJogo = vetorModosDeJogo.length-1;
		}
		
		if(vetorModosDeJogo[modoDeJogo] == "local") {
			Jogo.multiplayerRemoto = false;
		} else if(vetorModosDeJogo[modoDeJogo] == "remoto") {
			Jogo.multiplayerRemoto = true;
		}
		
		// escolher time para multiplayer remoto
		if(time >= vetorTimes.length) {
			time = 0;
		} else if(time < 0) {
			time = vetorTimes.length-1;
		}
		
		if(vetorTimes[time] == "medicos") {
			Jogo.timeDoMultiplayerRemoto = 1;
		} else if(vetorTimes[time] == "infectados") {
			Jogo.timeDoMultiplayerRemoto = 2;
		}
	}
	
	public static void renderizar(Graphics g) {
		if(VerdeParaRosa) {
			R++;
			G--;
			if(R >= maxRGB) {
				VerdeParaRosa = false;
			}
		} else {
			R--;
			G++;
			if(R <= 0) {
				VerdeParaRosa = true;
			}
		}
		
		if(Jogo.estadoDoJogo == "telaInicial") {
			
			g.setColor(new Color(R, G, B));
			g.setFont(new Font("arial", 1, 15));
			g.drawString("PANDEMIC CHESS", (Jogo.LARGURA-140)/2, 50);
		
			g.setFont(new Font("arial", 1, 10));
			
			g.drawString("Multiplayer (local)", (Jogo.LARGURA-140)/2, 150);
			g.drawString("Multiplayer (remoto)", (Jogo.LARGURA-140)/2, 170);
			
			g.setColor(new Color(0xAAEEAA));
			
			if(!(modoDeJogo < 0) && !(modoDeJogo >=vetorModosDeJogo.length)) {			
				if(vetorModosDeJogo[modoDeJogo] == "local")
					g.drawString(">>", (Jogo.LARGURA-180)/2, 150);
				else if(vetorModosDeJogo[modoDeJogo] == "remoto")
					g.drawString(">>", (Jogo.LARGURA-180)/2, 170);
			}
		} else if(Jogo.estadoDoJogo == "escolherTime") {
			g.setColor(new Color(R, G, B));
			g.setFont(new Font("arial", 1, 15));
			g.drawString("Escolha seu time", (Jogo.LARGURA-140)/2, 50);
		
			g.setFont(new Font("arial", 1, 10));
			
			g.drawString("Medicos", (Jogo.LARGURA-140)/2, 150);
			g.drawString("Infectados", (Jogo.LARGURA-140)/2, 170);
			
			g.setColor(new Color(0xAAEEAA));
			
			if(!(time < 0) && !(time >=vetorTimes.length)) {			
				if(vetorTimes[time] == "medicos")
					g.drawString(">>", (Jogo.LARGURA-180)/2, 150);
				
				else if(vetorTimes[time] == "infectados")
					g.drawString(">>", (Jogo.LARGURA-180)/2, 170);
			}
		}
	}
}
