package it.polito.tdp.yelp.model;

public class LocaleDistanza {
	
	private Business b;
	private Double distanza;
	
	public LocaleDistanza(Business b, Double distanza) {
		super();
		this.b = b;
		this.distanza = distanza;
	}

	public Business getB() {
		return b;
	}

	public void setB(Business b) {
		this.b = b;
	}

	public Double getDistanza() {
		return distanza;
	}

	public void setDistanza(Double distanza) {
		this.distanza = distanza;
	}

}
