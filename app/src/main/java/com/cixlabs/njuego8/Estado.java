package com.cixlabs.njuego8;

public class Estado {
	public char data[];
	
	public Estado( String e )
	{
		data = new char[9];
		set(e);		
	}
	
	public Estado()
	{
		data  = new char[9];
		set("123456780");
	}
	
	public void set( String e )
	{
		for (int i = 0; i < 9; i++ )
			  data[i] = e.charAt(i);
	}
	
	public void set( Estado e )
	{
		for (int i = 0; i < 9; i++ )
			  data[i] = e.data[i];
	}
	
	public boolean equals( Object oe )
	{
		for ( int i = 0; i < 9; i++ )
			if ( data[i] != ((Estado)oe).data[i] )
				return false;
		return true;
	}
	
	public void swap( int i1, int i2 )
	{
		char tmp;
		tmp = data[i1];
		data[i1] = data[i2];
		data[i2] = tmp;
	}
	
	public int indexOf( char c )
	{
	   for(int i=0; i<9; i++ )
		   if ( data[i] == c )
			   return i;
	   return -1;
	}
	
	public String toString()
	{
		return String.valueOf(data);
	}
	
	public int value()
	{
		int i, j, v, pb, x1, y1;
		v = 0;
		for ( i = 0; i < 9; i++ )
			for ( j = i+1; j < 9; j++ )
				if ( data[i] == '0' )
					v++;
				else
	                if ( data[j] != '0' )
						if ( data[i] > data[j] )
							v++;
		pb = indexOf('0');
		x1 = pb % 3;
		y1 = pb / 3;
		if ( (x1+y1) % 2 == 1 )
			v++;
		return v;		
	}
	/*
	// Distancia Manhattan
	int	   he_h2( TEstado s, TEstado so )
	{
		int d, x1, y1, x2, y2, i, c, p2;

		d = 0;
		for ( i = 0; i < 9; i++ )
			if ( s[i] != '0' )
			{
				c = s[i];
				x1 = i % 3;
				y1 = i / 3;
				p2 = strchr(so,c) - (char*)so;
				x2 = p2 % 3;
				y2 = p2 / 3;
				d += ABS(x2-x1) + ABS(y2-y1);
			}
		return d;
	}
	*/
	
	public int distance( Estado e )
	{
		// Distancia Manhattan
		int d, x1, y1, x2, y2, i, p2;
		char c;

		d = 0;
		for ( i = 0; i < 9; i++ )
			if ( data[i] != '0' )
				{
					c = data[i];
					x1 = i % 3;
					y1 = i / 3;
					p2 = e.indexOf(c);
					x2 = p2 % 3;
					y2 = p2 / 3;
					d += Math.abs(x2-x1) + Math.abs(y2-y1);
				}
		return d;
		
	}
	
	public void scramble()
	{
		int i, proxi;
		char prox;
		
		for (i=0; i < 9; i++ )
			data[i] = 'X';
		
		for (i=0; i < 9; i++ ) {
			do { 
			  proxi = (int)(Math.random()*1000 ) % 9;
			  prox = (char)(proxi+48);
			  if ( indexOf(prox) == -1 )
			  {
				  data[i] = prox;
				  break;
			  }
			}
			while (true);
		}
	}
}

