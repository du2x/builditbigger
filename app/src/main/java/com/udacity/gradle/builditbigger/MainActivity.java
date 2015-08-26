package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.androidjokescreen.JokeActivity;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udacity.gradle.Joker;
import com.udacity.gradle.gaejokes.myApi.MyApi;

import java.io.IOException;


public class MainActivity extends ActionBarActivity {
    private Joker joker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        joker = new Joker();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void tellJokeLocal(View view){
        Intent intent = new Intent(this, JokeActivity.class);
        String joke = joker.TellJoke();

        //String joke = new EndpointsJokeAsyncTask().doInBackground();
        intent.putExtra("joke", joke);

        startActivity(intent);
    }

    public void tellJoke(View view) {
        new EndpointsJokeAsyncTask().execute();
    }


    public void tellJokeRemote(String joke){
        Intent intent = new Intent(this, JokeActivity.class);
        intent.putExtra("joke", joke);
        startActivity(intent);
    }


    class EndpointsJokeAsyncTask extends AsyncTask<Void, String, String> {
        private MyApi myApiService = null;

        @Override
        protected String doInBackground(Void... unused) {
            if(myApiService == null) {
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // options for running against local devappserver
                        // - 10.0.2.2 is localhost's IP address in Android emulator
                        // - turn off compression when running against local devappserver
                        .setRootUrl("https://builditbigger-1048.appspot.com/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end options for devappserver
                myApiService = builder.build();
            }

            try {
                return myApiService.getJoke().execute().getData();
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            tellJokeRemote(result);
        }



    }
}
