package com.engineer.multikino;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

/**
 * Created by Kostek on 2016-02-01.
 */
public class TimetableActivity extends AppCompatActivity {
    String data;
    String cinema;
    Integer cinemaId;
    ArrayList<Film> arrayOfFilms = new ArrayList<>();
    String[] array = {"", "Warszawa Złote Tarasy", "Elbląg", "Bydgoszcz", "Gdańsk", "Gdynia", "Koszalin", "Kraków", "Poznań Malta", "Poznań Multikino 51", "Poznań Stary Browar", "Rybnik", "Sopot", "Szczecin", "Warszawa Targówek", "Warszawa Wola Park", "Włocławek", "Wrocław Arkady", "Wrocław Pasaż Grunwaldzki", "Warszawa Ursynów", "Zabrze", "Łódź Silver Screen", "", "", "", "Rumia", "Radom", "", "", "", "Rzeszów", "", "Słupsk", "Kielce", "Zgorzelec", "Tychy", "", "Czechowice-Dziedzice", "Lublin", "Katowice", "Olsztyn", "Jaworzno"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        data = getIntent().getExtras().getString("data");
        cinema = getIntent().getExtras().getString("cinema");
        cinemaId = Arrays.asList(array).indexOf(cinema);

        new AsyncDownload().execute();

    }

    public class AsyncDownload extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!isOnline()){
                Toast.makeText(TimetableActivity.this, "Bral polaczenia z internetem", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                return download(cinemaId, data);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONObject responseJson = new JSONObject(result);
                    JSONArray responseArray = responseJson.getJSONArray("results");
                    if(arrayOfFilms != null) arrayOfFilms.clear();
                    arrayOfFilms = getFilmsFromJson(responseArray);
                    show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private ArrayList<Film> getFilmsFromJson(JSONArray jArray) throws JSONException {
        ArrayList<Film> filmsList = new ArrayList<>();
        filmsList.clear();

        for (int i = 0; i < jArray.length(); i++) {
            JSONObject jsonData = jArray.getJSONObject(i);
            Film film = getFilms(jsonData);
            filmsList.add(film);
        }
        return filmsList;
    }

    private Film getFilms(JSONObject jObject) {
        ArrayList<String> seancesList = new ArrayList<>(); // Lista godzin seansów
        seancesList.clear();
        JSONArray seancesArray = null;  // parsowanie objektu
        try {
            seancesArray = jObject.getJSONArray("seances");

            for (int i = 0; i < seancesArray.length(); i++) {
                String seanceDate = seancesArray.getJSONObject(i).optString("beginning_date"); //otrzymanie daty
                seancesList.add(seanceDate);  // dodanie daty do listy
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Film(jObject.optString("title"),
                seancesList);
    }

    public void show (){
        ListAdapter adapter = new ListAdapter(TimetableActivity.this, arrayOfFilms);
        ListView filmsView = (ListView) findViewById(R.id.filmsView);
        filmsView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public class ListAdapter extends ArrayAdapter<Film> {
        String hours = "";

        public ListAdapter(Context context, ArrayList<Film> items) {
            super(context, 0, items);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final Film film = getItem(position);
            Log.i("TEST FILM", "TEST FILM " + film.getSeances().toString());

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.films_list, parent, false);
            }

            final TextView filmName = (TextView) convertView.findViewById(R.id.filmName);
            final TextView filmHours = (TextView) convertView.findViewById(R.id.filmHours);

            filmName.setText(film.getTitle());
            ArrayList<String> subString = new ArrayList<>();
            subString.addAll(film.getSeances());
            for (String i : subString){
                hours = hours + i.substring(11,16) + "  ";
            }
            filmHours.setText(hours);
            hours = "";
            subString.clear();
            return convertView;
        }
    }

    public String download(Integer cinId, String date) throws IOException {
        HttpURLConnection conn = null;
        Integer responseCode;

        try {
            // create HttpURLConnection
            URL url = new URL("https://www.multikino.pl/pl/repertoire/cinema/seances?id=" + cinId + "&from=" + date);
            conn = (HttpURLConnection) url.openConnection();

            // make POST request to the given URL
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Host", "multikino.pl");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.28 Safari/537.36");
            conn.setRequestProperty("Referer", "https://multikino.pl/pl/filmy");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
            conn.setRequestProperty("Accept-Language", "en-GB,en;q=0.8,en-US;q=0.6,pl;q=0.4");
            conn.setRequestProperty("Cookie", "prod-multikino-pl=9uvg7bqssn7h3jlsrep66ct8u7; SERVERID=web-1; cookies-accepted=true; isender_am_uuid=0a3c9dac-5dba-48ac-ae25-e28293958903; _dc_gtm_UA-3364491-1=1; _gat_UA-3364491-1=1; _ga=GA1.2.1022076116.1453714645; default_cinema=9");

            conn.setDoOutput(true);
            conn.setDoInput(true);

            StringBuilder response  = new StringBuilder();

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.flush();
            StringBuilder sb = new StringBuilder();
            //Send request
            BufferedReader br = new BufferedReader(new InputStreamReader((new GZIPInputStream(conn.getInputStream()))));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }

            br.close();

            responseCode = conn.getResponseCode();
            Log.i("ResponseFromServer", "code: " + responseCode);
            Log.i("ResponseFromServer", "response: " + sb.toString());

            return sb.toString();
        }

        catch (IOException e) {
            Log.i("ExceptionError", "Exception appeared");
            e.printStackTrace();
        } finally {

            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}

