package database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import entidades.PecasMoveis;
import main.Jogo;
import tabuleiro.Tabuleiro;

public class Firebase {

	public FirebaseDatabase db;
	public DatabaseReference ref;
	public DatabaseReference medRef;
	public DatabaseReference infRef;
	public DatabaseReference pecaMedRef;
	public DatabaseReference pecaInfRef;
	public DatabaseReference pecaBauRef;
	public DatabaseReference pecaChaRef;
	public Time medDoc;
	public Time infDoc;
	public ConjuntoPeca medConj;
	public ConjuntoPeca infConj;
	public ConjuntoBau bauConj;
	public DocCha chaConj;
	
	public Firebase() {
		
	}
	
	public void iniciarFireBase() {
		FileInputStream serviceAccount = null;
		try {
			serviceAccount = new FileInputStream("./serviceAccountKey.json");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		FirebaseOptions options = null;
		try {
			options = new FirebaseOptions.Builder()
			  .setCredentials(GoogleCredentials.fromStream(serviceAccount))
			  .setDatabaseUrl("https://pandemicchess-16070.firebaseio.com")
			  .build();
		} catch (IOException e) {
			e.printStackTrace();
		}

		FirebaseApp.initializeApp(options);
		
		iniciarInstanciasFireBase();
		observadorFirebase();
	}
	
	private void iniciarInstanciasFireBase() {
		db = FirebaseDatabase.getInstance();
		ref = db.getReference();
		
		// Banco de Dados usado durante o jogo
		medRef = ref.child("medicos");
		infRef = ref.child("infectados");
		medDoc = new Time(Tabuleiro.entidadesMedicos.size());
		infDoc = new Time(Tabuleiro.entidadesInfectados.size() + Tabuleiro.entidadesMedicos.size());
		medRef.setValueAsync(medDoc);
		infRef.setValueAsync(infDoc);
		
		// Banco de Dados para inicializar as peças no tabuleiro
		pecaMedRef = ref.child("conjuntos/pecasmedicos");
		pecaInfRef = ref.child("conjuntos/pecasinfectados");
		pecaBauRef = ref.child("conjuntos/pecasbaus");
		medConj = new ConjuntoPeca(Tabuleiro.entidadesMedicos);
		infConj = new ConjuntoPeca(Tabuleiro.entidadesInfectados);
		bauConj = new ConjuntoBau(Tabuleiro.entidadesBau);
		pecaMedRef.setValueAsync(medConj);
		pecaInfRef.setValueAsync(infConj);
		pecaBauRef.setValueAsync(bauConj);

	}
	
	public void iniciarChaFireBase() {
		pecaChaRef = ref.child("cha");
		pecaChaRef.setValueAsync(chaConj);
	}
	
	private void observadorFirebase() {
		ref.child("conjuntos").addValueEventListener(new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				Jogo.tabuleiro.reiniciarTabuleiro(snapshot);
				
			}
			
			@Override
			public void onCancelled(DatabaseError error) {
				
				
			}
			
		});
		
		if(Jogo.timeDoMultiplayerRemoto == 1) {
			ref.child("infectados").addValueEventListener(new ValueEventListener() {
				
				@Override
				public void onDataChange(DataSnapshot snapshot) {
					PecasMoveis.infectadoAtualDirX = Integer.parseInt(String.valueOf(snapshot.child("dirX").getValue()));
					PecasMoveis.infectadoAtualDirY = Integer.parseInt(String.valueOf(snapshot.child("dirY").getValue()));
					PecasMoveis.proxPosicaoInfectadoX = Integer.parseInt(String.valueOf(snapshot.child("proxX").getValue()));
					PecasMoveis.proxPosicaoInfectadoY = Integer.parseInt(String.valueOf(snapshot.child("proxY").getValue()));
					PecasMoveis.indexInfectado = Integer.parseInt(String.valueOf(snapshot.child("index").getValue()));
					for(int i = 0; i < Tabuleiro.entidadesInfectados.size(); i++) {
						infDoc.vetor.get(String.valueOf(i)).movendo = String.valueOf(snapshot.child("vetor").child(String.valueOf(i)).child("movendo").getValue());
						if(Integer.parseInt(infDoc.vetor.get(String.valueOf(i)).movendo) == 1) {
							Tabuleiro.entidadesInfectados.get(i).movendo = true;
						}
					}
				}
				
				@Override
				public void onCancelled(DatabaseError error) {
					
					
				}
				
			});
		}
		
		if(Jogo.timeDoMultiplayerRemoto == 2) {
			ref.child("medicos").addValueEventListener(new ValueEventListener() {
				
				@Override
				public void onDataChange(DataSnapshot snapshot) {
					PecasMoveis.medicoAtualDirX = Integer.parseInt(String.valueOf(snapshot.child("dirX").getValue()));
					PecasMoveis.medicoAtualDirY = Integer.parseInt(String.valueOf(snapshot.child("dirY").getValue()));
					PecasMoveis.proxPosicaoMedicoX = Integer.parseInt(String.valueOf(snapshot.child("proxX").getValue()));
					PecasMoveis.proxPosicaoMedicoY = Integer.parseInt(String.valueOf(snapshot.child("proxY").getValue()));
					PecasMoveis.indexMedico = Integer.parseInt(String.valueOf(snapshot.child("index").getValue()));
					for(int i = 0; i < Tabuleiro.entidadesMedicos.size(); i++) {
						medDoc.vetor.get(String.valueOf(i)).movendo = String.valueOf(snapshot.child("vetor").child(String.valueOf(i)).child("movendo").getValue());
						if(Integer.parseInt(medDoc.vetor.get(String.valueOf(i)).movendo) == 1) {
							Tabuleiro.entidadesMedicos.get(i).movendo = true;
						}
					}
				}
				
				@Override
				public void onCancelled(DatabaseError error) {
					
					
				}
				
			});
			
			ref.child("cha").addValueEventListener(new ValueEventListener() {
				
				@Override
				public void onDataChange(DataSnapshot snapshot) {
					Jogo.tabuleiro.vetorBaus[(int)(Jogo.tabuleiro.pecaCha.pos[0]/Tabuleiro.DC)][(int)(Jogo.tabuleiro.pecaCha.pos[1]/Tabuleiro.DC)].cha = false;
					Jogo.tabuleiro.pecaCha.pos[1] = Integer.parseInt(String.valueOf(snapshot.child("x").getValue()));
					Jogo.tabuleiro.pecaCha.pos[0] = Integer.parseInt(String.valueOf(snapshot.child("y").getValue()));
					Jogo.tabuleiro.vetorBaus[(int)(Jogo.tabuleiro.pecaCha.pos[0]/Tabuleiro.DC)][(int)(Jogo.tabuleiro.pecaCha.pos[1]/Tabuleiro.DC)].cha = true;
				}
				
				@Override
				public void onCancelled(DatabaseError error) {
					
					
				}
				
			});
		}
	}
	
	public void att() {
		if(Jogo.timeDoMultiplayerRemoto == 1) {
			medDoc.att();
			medRef.setValueAsync(medDoc);
		}
		if(Jogo.timeDoMultiplayerRemoto == 2) {
			infDoc.att();
			infRef.setValueAsync(infDoc);			
		}
	}
}
