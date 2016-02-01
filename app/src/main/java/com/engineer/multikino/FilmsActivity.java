package com.engineer.multikino;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FilmsActivity extends AppCompatActivity {
    public String m;
    public String y;
    public String d;
    public Integer da;
    public Integer mo;
    public Integer ye;
    public String helper;
    public String cinema;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_films);

        cinema = getIntent().getExtras().getString("cinema");

        final TextView date = (TextView) findViewById(R.id.textView);
        final TextView hour = (TextView) findViewById(R.id.textView2);

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        da = today.monthDay;
        mo = today.month;
        mo = mo + 1;
        ye = today.year;
        m = mo.toString();
        d = da.toString();
        y = ye.toString();
        if(m.length() == 1){
            m = "0" + m;
        }
        if(d.length() == 1){
            d = "0" + d;
        }
        String dat =  m + " - " + d + " - " + y;
        date.setText(dat);
        String hou = today.format("%k:%M");
        hour.setText(hou);

        final Button dzis = (Button) findViewById(R.id.button);
        final Button jutro = (Button) findViewById(R.id.button2);

        dzis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FilmsActivity.this, TimetableActivity.class);
                i.putExtra("data", y + "-" + m + "-" + d);
                Log.i("TEST DATY", "TEST DATY: DZIEN: " + d + " MIESIAC: " + m + "ROK: " + y);
                i.putExtra("cinema", cinema);
                startActivity(i);
            }
        });
        jutro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FilmsActivity.this, TimetableActivity.class);
                Integer de = da + 1;
                if(de.toString().length() == 1){
                    helper = "0" + de.toString();
                } else helper = de.toString();
                i.putExtra("data", y + "-" + helper + "-" + m);
                i.putExtra("cinema", cinema);
                startActivity(i);
            }
        });

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
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
