package com.cixlabs.njuego8;

public class ODESolver
{
	 
	
   private ODE ode;
   private double dq1[];// = new double[numEqns];
   private double dq2[];//  = new double[numEqns];
   private double dq3[];//  = new double[numEqns];
   private double dq4[];//  = new double[numEqns];
   
   public ODESolver( ODE ode )
   {
       int numEqns = ode.getNumEqns();
	   this.ode = ode;
	   dq1= new double[numEqns];
	   dq2= new double[numEqns];
	   dq3= new double[numEqns];
	   dq4= new double[numEqns];
   }
  //  Fourth-order Runge-Kutta ODE solver.  
  public void rungeKutta4(double ds) {

    //  Define some convenience variables to make the
    //  code more readable
    int j;
    int numEqns = ode.getNumEqns();
    double s;
    double q[];
//    double dq1[];// = new double[numEqns];
//    double dq2[];//  = new double[numEqns];
//    double dq3[];//  = new double[numEqns];
//    double dq4[];//  = new double[numEqns];

    //  Retrieve the current values of the dependent
    //  and independent variables.
    s = ode.getS();
    q = ode.getAllQ();

    // Compute the four Runge-Kutta steps, The return 
    // value of getRightHandSide method is an array of 
    // delta-q values for each of the four steps.
    ode.getRightHandSide(s, q, q, ds, 0.0, dq1 );
    ode.getRightHandSide(s+0.5*ds, q, dq1, ds, 0.5, dq2 );
    ode.getRightHandSide(s+0.5*ds, q, dq2, ds, 0.5, dq3 );
    ode.getRightHandSide(s+ds, q, dq3, ds, 1.0, dq4 );

    //  Update the dependent and independent variable values
    //  at the new dependent variable location and store the
    //  values in the ODE object arrays.
    ode.setS(s+ds);

    for(j=0; j<numEqns; ++j) {
//System.out.println("j="+j+"  dq1="+dq1[j]+"  dq2="+dq2[j]+
//    "  dq3="+dq3[j]+"  dq4="+dq4[j]);
      q[j] = q[j] + (dq1[j] + 2.0*dq2[j] + 2.0*dq3[j] + dq4[j])/6.0;
      ode.setQ(q[j], j);
    }     

    return;
  }
}
