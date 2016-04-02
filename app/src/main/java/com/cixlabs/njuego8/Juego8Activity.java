package com.cixlabs.njuego8;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class Juego8Activity extends Activity {


    private Juego8View mGLSurfaceView;
    private float mValues[];
    private SensorManager mSensorManager;

    public int objetivo;
    public Problema jugada;

    public Juego8Activity()
    {
        super();
//        Log.d(this.getClass().getName(),"constructor de la actividad.." );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_juego8);
            // Hide the Title Bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

            // Hide the Status Bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setWindowAnimations(android.R.style.Animation_Toast);

        if ( savedInstanceState == null ) {
            objetivo = -1;
            jugada = null;
        } else {
            objetivo = savedInstanceState.getInt("objetivo");
            jugada = new Problema(savedInstanceState.getString("jugada"),"012345678" );
        }


        mGLSurfaceView = new Juego8View(this);
        mGLSurfaceView.setRenderer(new Juego8Renderer(this));
        setContentView(mGLSurfaceView);
        }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("objetivo", objetivo);
        outState.putString("jugada", jugada.estado.toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume()
    {
        // The activity must call the GL surface view's onResume() on activity onResume().
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause()
    {
        // The activity must call the GL surface view's onPause() on activity onPause().
        super.onPause();
        mGLSurfaceView.onPause();
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        mGLSurfaceView.onBackPressed();
    }

    private class loadMyResources extends AsyncTask<String, Void, Object>
    {
        protected Object doInBackground( String ...args )
        {
            mGLSurfaceView.mRenderer.loadModel();
            return mGLSurfaceView.mRenderer;
        }

        protected void onPostExecute(Object result)
        {
            mGLSurfaceView.mRenderer.dataloaded = true;

        }

    }


/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_juego8, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
}
