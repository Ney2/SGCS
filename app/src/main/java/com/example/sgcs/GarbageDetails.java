package com.example.sgcs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class GarbageDetails extends AppCompatActivity {

    TextView tvid,tvlat,tvlong,tvlevel;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garbage_details);

        getSupportActionBar().setTitle("Garbage Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvid = findViewById(R.id.txtid);
        tvlat = findViewById(R.id.txtlat);
        tvlong = findViewById(R.id.txtlong);
        tvlevel = findViewById(R.id.txtlevel);

        Intent intent =getIntent();
        position = intent.getExtras().getInt("position");

        tvid.setText("ID: "+Info.garbageArrayList.get(position).getId());
        tvlat.setText("Latitude: "+Info.garbageArrayList.get(position).getLatitude());
        tvlong.setText("Longitude: "+Info.garbageArrayList.get(position).getLongitude());
        tvlevel.setText("Level: "+Info.garbageArrayList.get(position).getLevel());
    }
}