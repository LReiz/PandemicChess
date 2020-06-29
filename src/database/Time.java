package database;

import java.util.HashMap;
import java.util.Map;

import entidades.PecasMoveis;
import tabuleiro.Tabuleiro;

public class Time {

	public String time = "";
	public String selecionado = "";
	public String atual = "";
	public String dirX = "";
	public String dirY = "";
	public String proxX = "";
	public String proxY = "";
	public String index = "";
	public Map<String, DocPeca> vetor;
	
	public Time(String time, int size) {
		this.time = time;
		
		this.vetor = new HashMap<>();
		for(int i = 0; i < size; i++) {
			vetor.put(String.valueOf(i), new DocPeca(false));
		}
	}
	
	public void att() {
		
		if(time == "1") {			// atualiza banco de dados dos médicos
			this.selecionado = String.valueOf(PecasMoveis.medicoSelecionado);
			this.atual = String.valueOf(PecasMoveis.medicoAtual);
			this.dirX = String.valueOf(PecasMoveis.medicoAtualDirX);
			this.dirY = String.valueOf(PecasMoveis.medicoAtualDirY);
			this.proxX = String.valueOf(PecasMoveis.proxPosicaoMedicoX);
			this.proxY = String.valueOf(PecasMoveis.proxPosicaoMedicoY);
			this.index = String.valueOf(PecasMoveis.indexMedico);
			for(int i = 0; i < Tabuleiro.entidadesMedicos.size(); i++) {
				vetor.get(String.valueOf(i)).movendo = String.valueOf(Tabuleiro.entidadesMedicos.get(i).movendo);
			}
			
		} else if(time == "2") {	// atualiza banco de dados dos infectados
			this.selecionado = String.valueOf(PecasMoveis.medicoSelecionado);
			this.atual = String.valueOf(PecasMoveis.infectadoAtual);
			this.dirX = String.valueOf(PecasMoveis.infectadoAtualDirX);
			this.dirY = String.valueOf(PecasMoveis.infectadoAtualDirY);
			this.proxX = String.valueOf(PecasMoveis.proxPosicaoInfectadoX);
			this.proxY = String.valueOf(PecasMoveis.proxPosicaoInfectadoY);
			this.index = String.valueOf(PecasMoveis.indexInfectado);
			for(int i = 0; i < Tabuleiro.entidadesInfectados.size(); i++) {
				vetor.get(String.valueOf(i)).movendo = String.valueOf(Tabuleiro.entidadesInfectados.get(i).movendo);
			}
		}
	}
}
