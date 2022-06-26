package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	private List<String> cities;
	private YelpDao dao;
	private Graph<Business, DefaultWeightedEdge> grafo;
	private Map<String, Business> idMap;
	
	private List<Business> best;
	
	public Model() {
		this.dao = new YelpDao();
	}
	
	public List<String> getCities() {
		if(this.cities==null)
			this.cities = dao.getCities();
		
		return this.cities;
	}
	
	public String creaGrafo(String city) {
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		this.idMap = new HashMap<>();
		dao.getBusinessByCity(city, idMap);
		Graphs.addAllVertices(this.grafo, idMap.values());
		
		for(Adiacenza a : dao.getAdiacenze(city, idMap)) {
			Graphs.addEdge(this.grafo, a.getB1(), a.getB2(), a.getPeso());
		}
		
		return "Grafo creato con "+grafo.vertexSet().size()+" vertici e "+grafo.edgeSet().size()+" archi";
	}
	
	public List<Business> getVertici() {
		List<Business> vertici = new ArrayList<>(idMap.values());
		Collections.sort(vertici, new Comparator<Business>() {
			@Override
			public int compare(Business b1, Business b2) {
				return b1.getBusinessName().compareTo(b2.getBusinessName());
			}
		});
		return vertici;
	}
	
	public LocaleDistanza getLocaleDistante(Business b) {
		LocaleDistanza locale = null;
		double max = 0;
		for(DefaultWeightedEdge e : this.grafo.edgesOf(b)) {
			if(this.grafo.getEdgeWeight(e) > max) {
				max = this.grafo.getEdgeWeight(e);
				locale = new LocaleDistanza(Graphs.getOppositeVertex(grafo, e, b), this.grafo.getEdgeWeight(e));
			}
		}
		return locale;
	}
	
	public List<Business> calcolaPercorso(Business bStart, Business bFinish, int soglia) {
		this.best = new ArrayList<>();
		List<Business> parziale = new ArrayList<>();
		parziale.add(bStart);
		cerca(parziale, bFinish, soglia);
		return best;
	}

	private void cerca(List<Business> parziale, Business bFinish, int soglia) {
		if(parziale.get(parziale.size()-1).equals(bFinish)) {
			if(parziale.size() > best.size()) {
				this.best = new ArrayList<>(parziale);
			}
			return;
		}
		
		for(Business vicino : Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))) {	
			if(!parziale.contains(vicino)) {
				if(vicino.equals(bFinish)) {
					parziale.add(vicino);
					cerca(parziale, bFinish, soglia);
					parziale.remove(parziale.size()-1);
				} else if(vicino.getStars() > soglia) {
					parziale.add(vicino);
					cerca(parziale, bFinish, soglia);
					parziale.remove(parziale.size()-1);
				}
			}	
		}
		
	}

//	private boolean isAggiuntaValida(Business vicino, int soglia) {
//		List<Business> localiValidi = dao.getLocaliByStars(soglia, this.idMap);
//		if(localiValidi.contains(vicino))
//			return true;
//		else
//			return false;
//	}
	
	public double calcolaKmPercorsi(List<Business> percorso) {
		double kmPercorsi = 0.0;
		for(Business b1 : percorso) {
			for(Business b2 : percorso) {
				if(this.grafo.getEdge(b1, b2)!=null && b1.compareTo(b2)>0) {
					kmPercorsi += this.grafo.getEdgeWeight(this.grafo.getEdge(b1, b2));
				}
			}
		}
		return kmPercorsi;
	}
	
}
