package com.example.m8recuuf2;

import static com.example.m8recuuf2.settings.DefaultStart.appid;
import static com.example.m8recuuf2.settings.DefaultStart.retrofit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.m8recuuf2.databinding.ActivityMapsBinding;
import com.example.m8recuuf2.settings.ApiCall;
import com.example.m8recuuf2.Models.ModelApi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private int RecordAudioRequestCode;
    private EditText txtcity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AskPermissions();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Get Elements From View
        txtcity = findViewById(R.id.txtCity);
        Button search = findViewById(R.id.btnSearch);
        TextView data = findViewById(R.id.txtdata);
        Button vr = findViewById(R.id.VoiceRecord);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = txtcity.getText().toString();
                ApiCall apiCall = retrofit.create(ApiCall.class);
                Call<ModelApi> call = apiCall.getCity(city, appid, "ca");
                call.enqueue(new Callback<ModelApi>(){
                    @Override
                    public void onResponse(Call<ModelApi> call, Response<ModelApi> response) {
                        if(response.code()!=200){
                            data.setText("No S'ha trobat res");
                            Log.i("testApi", response.message() + " - - " + response.errorBody());
                            Log.i("testApi", "checkConnection    " + city);
                            return;
                        }
                        LatLng latLng = new LatLng(response.body().getCoord().lat, response.body().getCoord().lon);
                        mMap.addMarker(new MarkerOptions().position(latLng).title(response.body().getName())).showInfoWindow();
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        data.setText(NiceFormat(response));
                        Log.i("testApi", response.body().getName() + " - " + response.body().getWeather().get(0).getMain() + " - " + response.body().getWeather().get(0).getDescription() + " - " + response.body().getCoord().getLat() + " - " + response.body().getCoord().getLon() + " - " + response.body().getMain().getTemp());
                    }
                    @Override
                    public void onFailure(Call<ModelApi> call, Throwable t) {

                    }
                });
            }
        });
        vr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create intent with required data
                Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hola, digues una ciutat");
                startActivityForResult(speechRecognizerIntent, 1);
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        TextView data = findViewById(R.id.txtdata);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                Log.d("Soy el chivato ", latLng.toString());
                ApiCall apiCall = retrofit.create(ApiCall.class);
                Call<ModelApi> call = apiCall.getLoc(latLng.latitude, latLng.longitude, appid, "ca");
                call.enqueue(new Callback<ModelApi>(){
                    @Override
                    public void onResponse(Call<ModelApi> call, Response<ModelApi> response) {
                        if(response.code()!=200){
                            data.setText("No S'ha trobat res");
                            Log.i("testApi", response.message() + " - - " + response.errorBody());
                            Log.i("testApi", "checkConnection");
                            return;
                        }
                        mMap.addMarker(new MarkerOptions().position(latLng).title(response.body().getName())).showInfoWindow();
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        data.setText(NiceFormat(response));
                        Log.i("testApi", response.body().getName() + " - " + response.body().getWeather().get(0).getMain() + " - " + response.body().getWeather().get(0).getDescription() + " - " + response.body().getCoord().getLat() + " - " + response.body().getCoord().getLon() + " - " + response.body().getMain().getTemp());
                    }
                    @Override
                    public void onFailure(Call<ModelApi> call, Throwable t) {

                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            txtcity.setText(result.get(0));
            Log.i("testApi", result.get(0));
        }
    }

    public void AskPermissions(){
        // Ask for permissions for recorder
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
            }
        }
    }

    public String NiceFormat(Response<ModelApi> response){
        String formated = "Nom: " + response.body().getName()
                + "\nMain: " + response.body().getWeather().get(0).getMain()
                + "\nDescripció: " + response.body().getWeather().get(0).getDescription()
                + "\nLatitud: " + response.body().getCoord().getLat()
                + "\nLongitud: " + response.body().getCoord().getLon()
                + "\nTemperatura: " + response.body().getMain().getTemp()
                + "\nTemperatura Minima: " + response.body().getMain().getTemp_min()
                + "\nTemperatura Maxima: " + response.body().getMain().getTemp_max();
        return formated;
    }
}