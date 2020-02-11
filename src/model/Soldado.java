package model;


public class Soldado {

	private Exercito exercito;
	private Continente continente;
	private String dono;
	boolean imigrante = false;
	
	public Soldado(Exercito e){
		
		exercito = e;
		
	}
	
	public Soldado(String dono)
	{
		this.dono = dono;
	}
	
	public Soldado(Exercito e, Continente c) {
		exercito = e;
		continente = c;
	}
	
	public void setImigrante() {
		if(imigrante == false) {
			imigrante = true;
		} else {
			imigrante = false;
		}
	}
	
	public boolean isImigrante() {
		return imigrante;
	}
	
	public Continente getContinente() {
		return continente;
	}
	
	public Exercito getExercito(){
		
		return exercito;
		
	}
	
	public String getDono()
	{
		return dono;
	}
	
	public void ChangeDono(Exercito e)
	{
		exercito = e;
	}
	
	
	
}
