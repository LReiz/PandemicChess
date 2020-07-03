package graficos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import main.Jogo;

public class InterfaceFinal {

	private static int tamanhoDaFonteTitulo = 5;
	private static int maxTamanhoDaFonteTitulo = 12;
	private static int tamanhoDaFonteSubTitulo = 3;
	private static int maxTamanhoDaFonteSubTitulo = 10;

		
	public static void att() {
		
	}
	
	public static void renderizar(Graphics g) {
		if(tamanhoDaFonteTitulo < maxTamanhoDaFonteTitulo) {
			tamanhoDaFonteTitulo++;
		}
		if(tamanhoDaFonteSubTitulo < maxTamanhoDaFonteSubTitulo) {
			tamanhoDaFonteSubTitulo++;
		}
		
		g.setColor(new Color(0, 0, 0, 180));
		g.fillRect(0, 0, Jogo.LARGURA*Jogo.ESCALA, Jogo.ALTURA*Jogo.ESCALA);
		g.setFont(new Font("arial", 1, tamanhoDaFonteTitulo));
		if(Jogo.estadoDoJogo == "vitoriaInfectados") {
			g.setColor(new Color(210, 40, 40));
			g.drawString("VITÓRIA DOS INFECTADOS", (Jogo.LARGURA-150)/2, Jogo.ALTURA/2);			
			g.setFont(new Font("arial", 1, tamanhoDaFonteSubTitulo));
			g.drawString("Genocídio: O vírus venceu a humanidade!", (Jogo.LARGURA-210)/2, (Jogo.ALTURA+35)/2);			
		} else if(Jogo.estadoDoJogo == "vitoriaMedicosLock") {
			g.setColor(new Color(40, 210, 40));
			g.drawString("VITÓRIA DOS MÉDICOS", (Jogo.LARGURA-140)/2, Jogo.ALTURA/2);
			g.setFont(new Font("arial", 1, tamanhoDaFonteSubTitulo));
			g.drawString("Lockdown: Todos os infectados foram", (Jogo.LARGURA-190)/2, (Jogo.ALTURA+35)/2);			
			g.drawString("colocados em quarentena!", (Jogo.LARGURA-140)/2, (Jogo.ALTURA+55)/2);			
		} else if(Jogo.estadoDoJogo == "vitoriaMedicosCura") {
			g.setColor(new Color(40, 210, 40));
			g.drawString("VITÓRIA DOS MÉDICOS", (Jogo.LARGURA-140)/2, Jogo.ALTURA/2);
			g.setFont(new Font("arial", 1, tamanhoDaFonteSubTitulo));
			g.drawString("Cura encontrada: Os cientistas descobriram", (Jogo.LARGURA-220)/2, (Jogo.ALTURA+35)/2);			
			g.drawString(" que a cura para a doença que a cura para a doença", (Jogo.LARGURA-260)/2, (Jogo.ALTURA+55)/2);			
			g.drawString("era um pouco de repouso e um chazinho de boldo!", (Jogo.LARGURA-245)/2, (Jogo.ALTURA+75)/2);			
		}
	}
}
