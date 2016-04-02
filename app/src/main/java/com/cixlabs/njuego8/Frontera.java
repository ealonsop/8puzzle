package com.cixlabs.njuego8;

public class Frontera {
	public Nodo lista[];
	int max;
	int cantidad;
	
	public Frontera()
	{
		lista = null;
		max = cantidad = 0;
	}
	
	private void crecer()
	{
		Nodo aux[];
		max += 500;
		aux = new Nodo[max];
		for ( int i = 0; i < cantidad; i++ )
			aux[i] = lista[i];
		lista = null;
		lista = aux;
	}
	
	public void inserta( Nodo n )
	{
		if ( cantidad == max )
			crecer();
		lista[cantidad++] = n;
	}
	
	public Nodo elimina()
	{
		int menor, posm;
		
		menor = 1000000;
		posm=-1;
		for (int i=0; i<cantidad; i++)
			if ( !lista[i].expandido && lista[i].prof+lista[i].heu < menor )
			{
				menor = lista[i].prof+lista[i].heu;
				posm = i;
			}
		if ( posm >= 0 )
			return lista[posm];
		else
			return null;
	}
	
	public boolean contiene( Estado e )
	{
		for (int i=0; i < cantidad; i++ )
			if ( lista[i].e.equals(e) )
				return true;
		return false;
	}
	
	public void limpiar()
	{
		for (int i=0; i<cantidad;i++)
			lista[i] = null;
		cantidad = 0;
	}

}
