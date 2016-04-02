package com.cixlabs.njuego8;

import android.util.Log;

public class Problema {
	public Estado estado;
	public Estado objetivo;
	public int _pa[], _pr[];
	public Frontera CP;
	public Estado auxR[];
	public Nodo raiz;
	public int solution[];
	public int nn;
	public Nodo nodes[];
	public int  maxnodes, cnodes;
	
	
	
	public Problema( String e, String o )
	{
		estado = new Estado(e);
		objetivo = new Estado(o);
		_pa = new int[4];
		_pr = new int[2];
		CP = new Frontera();
		auxR = new Estado[4];
		auxR[0] = new Estado();
		auxR[1] = new Estado();
		auxR[2] = new Estado();
		auxR[3] = new Estado();
		raiz = new Nodo();
		solution = new int[500];
		
		maxnodes = 0;
		cnodes = 0;
	}
	
	public void crecer()
	{
		int i;
		Nodo aux[];
		maxnodes += 4000;
		aux = new Nodo[maxnodes];
		for (i=0; i < cnodes; i++)
			aux[i] = nodes[i];
		for (i=cnodes; i < maxnodes; i++ )
			aux[i] = new Nodo();
		nodes = aux;
	}
	
	public Nodo new_node()
	{
		if ( cnodes == maxnodes )
			crecer();
		return nodes[cnodes++];
	}
	
	public void clear_nodes()
	{
		cnodes  = 0;
	}
	
	public void set_objetivo( String s )
	{
		objetivo.set(s);
	}
	
	void get_actions( Estado s1, int pa[] )
	{
		int pb;
		pb = s1.indexOf('0');
		
		switch ( pb ) {
		 // 1ra fila
			 //  IZQUIERDA    ARRIBA     DERECHA    ABAJO        
		 case 0: pa[0] =  0;  pa[1] = 1; pa[2] = 1; pa[3] = 0; break;
		 case 1: pa[0] = -1;  pa[1] = 1; pa[2] = 1; pa[3] = 0; break;
		 case 2: pa[0] = -1;  pa[1] = 1; pa[2] = 0; pa[3] = 0; break;
		 // 2da fila
		 case 3: pa[0] =  0;  pa[1] = 1; pa[2] = 1; pa[3] = -1; break;
		 case 4: pa[0] = -1;  pa[1] = 1; pa[2] = 1; pa[3] = -1; break;
		 case 5: pa[0] = -1;  pa[1] = 1; pa[2] = 0; pa[3] = -1; break;
		 // 3ra fila
		 case 6: pa[0] =  0;  pa[1] = 0; pa[2] = 1; pa[3] = -1; break;
		 case 7: pa[0] = -1;  pa[1] = 0; pa[2] = 1; pa[3] = -1; break;
		 case 8: pa[0] = -1;  pa[1] = 0; pa[2] = 0; pa[3] = -1; break;
		}
	}

	public int make_move( Estado s1, int a, Estado r, int prr[] )
	{
		int pb, ia, pr;

		
		//Log.d(this.getClass().getName(),"makemove " + s1.toString() );
		get_actions( s1, _pa );

		ia = a;

		if ( _pa[ia] == 0 )
			return -1;

		pr = pb = s1.indexOf('0');;

		switch ( a ) {
			case 0: pr = pb - 1; break;
			case 2: pr = pb + 1; break;
			case 1: pr = pb + 3; break;
			case 3: pr = pb - 3; break;
		}
		prr[0] = pr;
		prr[1] = pb;
		r.set(s1);
		r.swap(pb, pr);
		return a;
	}
	

	public int find_action( Estado s1, char cell_val )
	{
		int a;
		int ar;
		ar = -1;
		for ( a = 0; a <= 3; a++ )
		{
			if ( make_move( s1, a, auxR[a], _pr ) != -1 )
			{
				if ( cell_val == s1.data[_pr[0] ] )
				{
					ar = a;
					break;
				}
			}
		}
		return ar;
	}	
	
	public boolean do_action(char cell_val)
	{
	  int a;
	  a = find_action( estado, cell_val );
	  if ( a != -1 ) {
		  make_move( estado, a, estado, _pr );
		  return true;
	  }
	  else
		  return false;
	}
	
	public boolean do_action(int a)
	{
		return make_move( estado, a, estado, _pr ) >= 0 ? true : false;
	}
	
	
	public boolean has_solution( Estado e, Estado o )
	{
		int v1, v2;

		v1 = e.value();
		v2 = o.value();
		return (v1+v2) % 2 == 0;
	}

	public boolean find_solution()
	{
		Nodo primero, aux;
		int a;
		CP.limpiar();
		
		if ( !has_solution( estado, objetivo ) )
			return false;
		
		clear_nodes();
		raiz = new_node();
		raiz.e.set(estado);
		raiz.expandido = false;
		raiz.padre = null;
		raiz.accion = -1;
		raiz.prof = 0;
		raiz.heu = 0;

		nn = 0;
		
		CP.inserta(raiz);
		while ( true )
		{
			primero = CP.elimina();
			if ( primero == null )
				return false;
			if ( primero.e.equals(objetivo ) )
			{
			//	Log.d(this.getClass().getName(),"solucion encontrada...");
				build_solution(primero);
				return true;
			}
			primero.expandido = true;
		  //  Log.d(this.getClass().getName(), " " + primero.prof  + " " + nn);						
			for ( a = 0; a <= 3; a++ )
			{
				if ( make_move( primero.e, a, auxR[a], _pr ) != -1 ) {
					if ( !CP.contiene(auxR[a]) ) {
						nn++;
						aux = new_node();
//						aux.e = new Estado();
						aux.e.set(auxR[a]);
						aux.prof = primero.prof+1;
						aux.padre = primero;
						aux.heu = aux.e.distance(objetivo);
						aux.accion = a;
						aux.expandido = false;
						CP.inserta(aux);
					}
				}
			}
		}
	}
	
	public void build_solution(Nodo n)
	{
		int t;
		t = n.prof-1;
		solution[t+1] = -1;
		if ( t < 0 ) {
			return;
		}
		else
		do {
			//Log.d(this.getClass().getName(), n.e + " acc: " + n.accion);
			solution[t] = n.accion;
			t--;
			n = n.padre;
		}
		while ( t >= 0 );
		
	}
	
	public void scramble(int n)
	{
		boolean ok;
		int la;
		la = -1;
		int aidx;
		
		estado.set(objetivo);
		for (int i = 0; i < n; i++ )
		{
			get_actions(estado,_pa);
			ok = false;
			while ( !ok )
			{
				aidx = (int)((Math.random()*1000))%4;
				if ( _pa[aidx] != 0 && aidx != la )
				{
					do_action(aidx);
					switch ( aidx ) {
						case 0: la = 2; break;
						case 1: la = 3; break;
						case 2: la = 0; break;
						case 3: la = 1; break;
					}
					ok = true;
				}
			}
		}
	}
	
}
