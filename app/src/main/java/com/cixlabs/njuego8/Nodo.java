package com.cixlabs.njuego8;

public class Nodo {
	public Estado e;
	public int prof;
	public int heu;
	public Nodo padre;
	public int accion;
	boolean expandido;
	
	public Nodo()
	{
		expandido = false;
		padre = null;
		accion = -1;
		e = new Estado();
	}
	
}
