package com.example.mostafa.attender.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mostafa.attender.AboutAppTask;
import com.example.mostafa.attender.R;

import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    String aboutAppData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progresBarID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about_appId) {
            boolean checkConnection = isNetworkConnected();
            if (checkConnection == true) {
                try {
                    aboutAppData = new AboutAppTask(progressBar).execute(getResources().getString(R.string.about_app_apiKey)).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getApplicationContext(), AboutApp.class);
                intent.putExtra("data", aboutAppData);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected());
    }


}
