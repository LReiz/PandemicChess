package database;

import java.util.HashMap;
import java.util.Map;

import entidades.PecasMoveis;
import main.Jogo;
import tabuleiro.Tabuleiro;

public class Time {

	public String selecionado = "0";
	public String atual = "0";
	public String dirX = "0";
	public String dirY = "0";
	public String proxX = "0";
	public String proxY = "0";
	public String index = "0";
	public Map<String, DocPeca> vetor;
	
	public Time(int size) {
		
		this.vetor = new HashMap<>();
		for(int i = 0; i < size; i++) {
			vetor.put(String.valueOf(i), new DocPeca(false));
		}
	}
	
	public void att() {
		
		if(Jogo.timeDoMultiplayerRemoto == 1) {			// atualiza banco de dados dos médicos
			this.selecionado = String.valueOf(PecasMoveis.medicoSelecionado);
			this.atual = String.valueOf(PecasMoveis.medicoAtual);
			this.dirX = String.valueOf(PecasMoveis.medicoAtualDirX);
			this.dirY = String.valueOf(PecasMoveis.medicoAtualDirY);
			this.proxX = String.valueOf(PecasMoveis.proxPosicaoMedicoX);
			this.proxY = String.valueOf(PecasMoveis.proxPosicaoMedicoY);
			this.index = String.valueOf(PecasMoveis.indexMedico);
			for(int i = 0; i < Tabuleiro.entidadesMedicos.size(); i++) {
				if(Tabuleiro.entidadesMedicos.get(i).movendo) {
					vetor.get(String.valueOf(i)).movendo = "1";
				} else {
					vetor.get(String.valueOf(i)).movendo = "0";
				}
			}
			
		} else if(Jogo.timeDoMultiplayerRemoto == 2) {	// atualiza banco de dados dos infectados
			this.selecionado = String.valueOf(PecasMoveis.medicoSelecionado);
			this.atual = String.valueOf(PecasMoveis.infectadoAtual);
			this.dirX = String.valueOf(PecasMoveis.infectadoAtualDirX);
			this.dirY = String.valueOf(PecasMoveis.infectadoAtualDirY);
			this.proxX = String.valueOf(PecasMoveis.proxPosicaoInfectadoX);
			this.proxY = String.valueOf(PecasMoveis.proxPosicaoInfectadoY);
			this.index = String.valueOf(PecasMoveis.indexInfectado);
			for(int i = 0; i < Tabuleiro.entidadesInfectados.size(); i++) {
				if(Tabuleiro.entidadesInfectados.get(i).movendo) {
					vetor.get(String.valueOf(i)).movendo = "1";
				} else {
					vetor.get(String.valueOf(i)).movendo = "0";
				}
			}
		}
		
	}
	
}
