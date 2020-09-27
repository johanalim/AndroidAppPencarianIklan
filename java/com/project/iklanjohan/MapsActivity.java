package com.project.iklanjohan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.project.iklanjohan.Pojo.data_tempat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.refactor.lib.colordialog.ColorDialog;
import cn.refactor.lib.colordialog.PromptDialog;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private data_tempat mData_tempat;
    private List<data_tempat> listTempat = new ArrayList<data_tempat>();  //untuk memasukkan data_tempat ke array
    private Bundle bundle;
    int nomorData;
    LatLng mLatLng[], lokasi;
    LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getSupportActionBar().setSubtitle("Iklan Online");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        bundle = getIntent().getExtras();

        getLokasi();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setMyLocationButtonEnabled(true); // untuk menampilkan button Current Location
        mMap.getUiSettings().setZoomControlsEnabled(true); // menampilkan button zoom in & zoom out
        mMap.getUiSettings().setMapToolbarEnabled(true); // menampilkan button map & navigasi
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN); // setting type map

        // permission untuk dapat mengaktifkan button Current Location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                //jika Location Service/layanan lokasi(GPS) tidak aktif
                if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false){
                    ColorDialog dialog = new ColorDialog(MapsActivity.this);
                    dialog.setTitle("Layanan lokasi tidak aktif");
                    dialog.setContentText("Map ini membutuhkan layanan lokasi untuk akses ke lokasi Anda.");
                    dialog.setPositiveListener("Aktifkan", new ColorDialog.OnPositiveListener() {
                        @Override
                        public void onClick(ColorDialog colorDialog) {
                            Intent intentSetting = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intentSetting);
                            colorDialog.dismiss();
                        }
                    });
                    dialog.setNegativeListener("Abaikan", new ColorDialog.OnNegativeListener() {
                        @Override
                        public void onClick(ColorDialog colorDialog) {
                            colorDialog.dismiss();
                        }
                    });
                    dialog.setAnimationEnable(true);
                    dialog.setColor(getResources().getColor(R.color.colorPrimary));
                    dialog.setCancelable(false);
                    dialog.show();
                }else{
                    Toast.makeText(MapsActivity.this, "Mencari Lokasi ...", Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(MapsActivity.this, "Tekan judul untuk lihat detail", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void getLokasi(){
        String url = Server.URL + "read_data.php"; // baca data ke server

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                nomorData = response.length();
                // Untuk menampilkan JSON dari databasenya(tampil pada Android Monitor saat aplikasi dirunning)
                Log.d(MapsActivity.class.getSimpleName(), response.toString());

                // menentukan indeks arraynya
                mLatLng = new LatLng[nomorData];

                for (int i = 0; i < nomorData; i++) {
                    try {
                        JSONObject data = response.getJSONObject(i);
                        mData_tempat = new data_tempat();

                        // menyimpan data tiap field ke variabel id nama alamat dll
                        mData_tempat.setNo(data.getString("no_tempat"));
                        mData_tempat.setNama_tok(data.getString("nama_iklan"));
                        mData_tempat.setAlamat(data.getString("alamat"));
                        /*mData_tempat.setSpd(data.getInt("spd"));*/
                        mData_tempat.setGambar(data.getString("gambar"));
                        mData_tempat.setLatitude(data.getDouble("latitude"));
                        mData_tempat.setLongitude(data.getDouble("longitude"));

                        mLatLng[i] = new LatLng(mData_tempat.getLatitude(), mData_tempat.getLongitude());

                        listTempat.add(mData_tempat); //memasukkan mData_tempat ke array listTempat
                        mMap.addMarker(new MarkerOptions()
                                .position(mLatLng[i]) //posisi latitude & longitude
                                .title(listTempat.get(i).getNama_tok())
                                .snippet(listTempat.get(i).getAlamat()) //subtitle marker
                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.utama))); // icon pada marker map
                    } catch (JSONException e) {
                        Toast.makeText(MapsActivity.this, "Kesalahan dalam akses data", Toast.LENGTH_SHORT).show();
                    }

                    //untuk menyeleksi apakah ada Bundle atau tidak
                    //Bundle akan ada jika lokasi diakses menggunakan tombol Search
                    if (bundle == null){
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng[i], 15));  // agar ketika map dibuka, kamera langsung tertuju pada posisi yang dituju
                    }else {
                        lokasi = new LatLng(bundle.getDouble("latitude"), bundle.getDouble("longitude"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasi, 20));
                    }

                }

                // klik pada tittle marker
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Log.d("DEBUG_", "Lihat Detail");
                        for (int i = 0; i < nomorData; i++){
                            if (marker.getTitle().equals(listTempat.get(i).getNama_tok())){
                                DetailTempat.No = listTempat.get(i).getNo();
                                DetailTempat.namaiklan = listTempat.get(i).getNama_tok();
                                DetailTempat.alamat = listTempat.get(i).getAlamat();
                                /*DetailTempat.spd = String.valueOf(listTempat.get(i).getSpd());*/
                                DetailTempat.gambar = listTempat.get(i).getGambar();

                                startActivity(new Intent(MapsActivity.this, DetailTempat.class));
                            }
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //menampilkan alert ketika tidak terhubung ke internet
                new PromptDialog(MapsActivity.this)
                        .setDialogType(PromptDialog.DIALOG_TYPE_WRONG)
                        .setAnimationEnable(true)
                        .setTitleText("Kesalahan")
                        .setContentText("Gagal Terhubung ke Server, Mohon Periksa Koneksi Internet Anda")
                        .setPositiveListener("Mulai Lagi", new PromptDialog.OnPositiveListener() {
                            @Override
                            public void onClick(PromptDialog promptDialog) {
                                getLokasi();
                                promptDialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        Volley.newRequestQueue(this).add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        tampilanAwal();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            tampilanAwal();
        }else if (id == R.id.cariLokasi){
            Server.Status_Proses = "SearchLoc";
            startActivity(new Intent(MapsActivity.this, DataTempat.class));
        }else if (id == R.id.refreshLokasi){
            bundle = null;
            getLokasi();
        }

        return super.onOptionsItemSelected(item);
    }

    // method untuk kembali ke tampilan awal
    private void tampilanAwal() {
        Intent intent = new Intent(MapsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
