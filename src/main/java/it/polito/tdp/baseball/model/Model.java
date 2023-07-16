package it.polito.tdp.baseball.model;

import java.util.*;


import org.jgrapht.Graphs;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import org.jgrapht.alg.connectivity.ConnectivityInspector;
import it.polito.tdp.baseball.db.BaseballDAO;

public class Model {
	BaseballDAO dao;
	Map<String, People> idMap;
	Graph<People,DefaultEdge> grafo;
	
	// ricorsione
	List<People> dreamTeam;
	double salarioDreamTeam;
	// altre mappe per ricorsione
	private Map<People, Double> salariesIDMap;
	private Map<People, List<Team>> playerTeamsMap;
	
	
	public Model() {
		dao=new BaseballDAO();
		idMap=new HashMap<>();
		dao.readAllPlayers(idMap);
		
		//List<People> players = dao.readAllPlayers();
		//for(People p : players) {
			//idMap.put(p.getPlayerID(), p);
		//}
	}
	
	public void creaGrafo(int anno, double salario) {
		grafo = new SimpleGraph<People,DefaultEdge>(DefaultEdge.class);
		
		List<People> listaVertici=dao.getVerticiGrafo(anno,salario,idMap);
		Graphs.addAllVertices(grafo, listaVertici);
		
		List<Adiacenza> listaArchi = dao.getArchiGrafo(anno,salario,salario,idMap);
		for(Adiacenza a : listaArchi) {
			Graphs.addEdgeWithVertices(grafo, a.getP1(), a.getP2());
		}	
	}
	
	
	public ArrayList<GradoVertice> getVerticeGradoMassimo() {
		
		ArrayList<GradoVertice> verticiGradoMax=new ArrayList<>();
		int contoGrado=0;
		
		Set<People> listaVertexSet = grafo.vertexSet();
		
		for(People e : listaVertexSet) {
			if(grafo.edgesOf(e).size()>contoGrado) {
				verticiGradoMax.clear();
				contoGrado = grafo.edgesOf(e).size();
				verticiGradoMax.add(new GradoVertice(e,contoGrado));
			}
			if(grafo.edgesOf(e).size()==contoGrado) {
				verticiGradoMax.add(new GradoVertice(e,contoGrado));
			}
		}
		return verticiGradoMax;
	}
	
	
	public int getComponentiConnesse() {
		ConnectivityInspector<People,DefaultEdge> inspector = new ConnectivityInspector<People,DefaultEdge>(grafo);
		return inspector.connectedSets().size();
	}
	
	
	
	
	
	public void  calcolaDreamTeam() {
		
		salarioDreamTeam = 0.0;
		dreamTeam = new ArrayList<People>();
		List<People> rimanenti = new ArrayList<People>(this.grafo.vertexSet());
		
		ricorsione(new ArrayList<People>(), rimanenti);
	}
	
	
	private void ricorsione(List<People> parziale, List<People> rimanenti){
		/*
		 * L'idea della ricorsione è di prendere un giocatore, metterlo nella lista parziale,
		 * e rimuovere tutti i suoi compagni di squadra (trovati come i suoi vicini) 
		 * dalla lista di giocatori rimanenti. Dopodichè, ripetiamo la ricorsione, 
		 * usando parziale ed il nuovo insieme ridotto di giocatori rimanenti,
		 * fino a che non li finiamo.
		 */
		
		// Condizione Terminale
		if (rimanenti.isEmpty()) {
			//calcolo costo
			double salario = getSalarioTeam(parziale);
			if (salario>this.salarioDreamTeam) {
				this.salarioDreamTeam = salario;
				this.dreamTeam = new ArrayList<People>(parziale);
			}
			return;
		}
		
		/*
		 * VERSIONE NON OTTIMIZZATA DELLA RICORSIONE
		 */
		/*
		 * Questa versione riguarda le stesse combinazioni di giocatori più volte, e richiede mooolto tempo.
		 * Riesce a terminare in tempi acettabili solo su grafi molto piccoli, con meno di 10 vertici. La versione 
		 * ottimizzata di sotto riesce a gestire velocemente anche grafi con 40-50 vertici.
		 */
//		for (People p : rimanenti) {
//			List<People> currentRimanenti = new ArrayList<>(rimanenti);
//				parziale.add(p);
//				currentRimanenti.removeAll(Graphs.neighborListOf(this.grafo, p));
//				currentRimanenti.remove(p);
//				ricorsione(parziale, currentRimanenti);
//				parziale.remove(parziale.size()-1);
//		}
		
		
		/*
		 * VERSIONE OTTIMIZZATA DELLA RICORSIONE
		 */
		/*
		 * Rispetto alla versione non ottimizzata, qui l'idea è di ciclare su una squadra alla volta
		 * piuttosto che su tutti i vertici del grafo, rimuovendo così molti casi.
		 * Per selezionare una squadra potremmo prendere un vertice, e poi prendere tutti i suoi vicini.
		 * Però alcuni di questi vertici (giocatori) potrebbero aver giocato per 2 squadre in un anno,
		 * perciò se selezionassimo i suoi vicini prenderemmo due squadre invece di una.
		 * Per evitare questo problema, andiamo prima a prendere un vertice qualsiasi, con tutti i suoi vicini.
		 * Poi, tra questi prendiamo un vertice di grado minimo, e andiamo a calcolare i suoi vicini.
		 * L'alternativa sarebbe di fare, nel metodo calcolaDreamTeam(), un sort dei vertici in 'rimanente' in ordine crescente del loro grado
		 * e poi selezionare sempre il primo.
		 */
		List<People> squadra =  Graphs.neighborListOf(this.grafo, rimanenti.get(0));
		squadra.add( rimanenti.get(0));
		People startP = minDegreeVertex(squadra);
		List<People> squadraMin =  Graphs.neighborListOf(this.grafo, rimanenti.get(0));
		squadraMin.add( rimanenti.get(0));
		
		for (People p : squadraMin) {
			List<People> currentRimanenti = new ArrayList<>(rimanenti);
			parziale.add(p);
			currentRimanenti.removeAll(squadraMin);
			ricorsione(parziale, currentRimanenti);
			parziale.remove(parziale.size()-1);
		}
	}
	
	
	/**
	 * Metodo per calcolare il vertice di grado minimo tra un insieme di vertici
	 * @param squadra
	 * @return
	 */
	private People minDegreeVertex(List<People> squadra) {
		People res = null;
		int gradoMin = -1;
		for (People p : squadra) {
			int grado = Graphs.neighborListOf(this.grafo, p).size();
			if (gradoMin==-1 || grado<gradoMin) {
				res = p;
			}
		}		
		return res;
	}
	
	
	
	/**
	 * Metodo che calcola il salario nell'anno di una lista di giocatori
	 * Usato nella ricorsione, per calcolare il salario del Dream Team
	 * @param team
	 * @return
	 */
	private double getSalarioTeam(List<People> team) {
		double result = 0.0;
		for (People p : team) {
			result += this.salariesIDMap.get(p);
		}
		return result;
	}
	
	
	
	public double getSalarioDreamTeam() {
		return this.salarioDreamTeam;
	}
	
	
	public List<People> getDreamTeam() {
		return this.dreamTeam;
	}
	
	public List<Team> getPeopleTeams(People player){
		return this.playerTeamsMap.get(player);

	}
	
	public double getSalarioPlayer(People p) {
		return this.salariesIDMap.get(p);
	}
	
	
	
	
	
	
	
	public int getNumeroVertici() {
		if(grafo!=null) {
			return grafo.vertexSet().size();
		}
		else {
			return 0;	
		}
	}
	
	public int getNumeroArchi() {
		if(grafo!=null) {
			return grafo.edgeSet().size();
		}
		else {
			return 0;	
		}
	}

	public boolean grafoCreato() {
		if(grafo!=null) {
			return true;
		}
		else
		return false;
	}
	
	
	
	
	
	
}