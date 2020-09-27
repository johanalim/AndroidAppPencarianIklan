package com.project.iklanjohan;

//class ini digunakan untuk proses Edit & Hapus data

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.iklanjohan.Adapter.ListViewAdapter;
import com.project.iklanjohan.Pojo.data_tempat;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.refactor.lib.colordialog.PromptDialog;

public class DataTempat extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private SearchView mSearchView;
    private ImageView imgPerusahaan, baliho;
    private TextView tvTotalLokasi, tvNo, tvNamaTok, tvAlamat, tvSpd, tvId;
    private ListView lvTempat;
    private SwipeRefreshLayout mSwipeRefresh;
    private ArrayList<data_tempat> listTempat, filterTempat;  //untuk memasukkan data_tempat ke array
    private ListViewAdapter adapterListView;
    private EditText etNo, etNamaTok, etAlamat, etLatitude, etLongitude, etGambar;
    private String No, namaiklan, alamat, gambar;
    private int spd;
    private Double latitude, longitude;

    private AlertDialog.Builder dialogForm;
    private LayoutInflater inflater;
    private View dialogView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_tempat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        tvTotalLokasi = (TextView) findViewById(R.id.tvTotalLokasi);
        lvTempat = (ListView) findViewById(R.id.lvDataTempat);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mSearchView = (SearchView) findViewById(R.id.kolomCari);
        //deklarasi ArrayList
        listTempat  = new ArrayList<data_tempat>();
        filterTempat = new ArrayList<data_tempat>(); // list untuk data setelah di filter

        if (Server.Status_Proses.equals("Edit")){
            getSupportActionBar().setTitle("Klik Untuk Edit Lokasi");

            //event saat item list diklik
            lvTempat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    No = filterTempat.get(position).getNo();
                    namaiklan = filterTempat.get(position).getNama_tok();
                    alamat = filterTempat.get(position).getAlamat();
                    /*spd = filterTempat.get(position).getSpd();*/
                    latitude = filterTempat.get(position).getLatitude();
                    longitude = filterTempat.get(position).getLongitude();
                    gambar = filterTempat.get(position).getGambar();

                    DialogFormEdit(No, namaiklan, alamat, spd, latitude, longitude, gambar);
                }
            });

        }else if (Server.Status_Proses.equals("Hapus")){
            getSupportActionBar().setTitle("Klik Untuk Hapus Lokasi");

            lvTempat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    No = filterTempat.get(position).getNo();
                    namaiklan = filterTempat.get(position).getNama_tok();
                    alamat = filterTempat.get(position).getAlamat();
                    /*spd = filterTempat.get(position).getSpd();*/
                    gambar = filterTempat.get(position).getGambar();

                    DialogFormHapus(No, namaiklan, alamat, spd, gambar);
                }
            });

        }else if (Server.Status_Proses.equals("SearchLoc")){  // SearchLoc = ketika menekan tombol cari lokasi di MapsActivity
            getSupportActionBar().setTitle("Cari Lokasi Pengguna");

            lvTempat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(DataTempat.this, MapsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putDouble("latitude", filterTempat.get(position).getLatitude());
                    bundle.putDouble("longitude", filterTempat.get(position).getLongitude());

                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }

        mSwipeRefresh.setOnRefreshListener(this);

        callData();

        // filter list view
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {  //newText adalah text yang user ketik
                filterTempat.clear();
                for (int i = 0; i < listTempat.size(); i++){
                    // jika pada nama pelanggan posisi ke-i terdapat/berisi(contains) huruf2 yang diketik(newText), maka filter dijalankan
                    if (listTempat.get(i).getNama_tok().toLowerCase().trim().contains(newText.toString().toLowerCase().trim())) {
                        filterTempat.add(listTempat.get(i));
                    }
                }

                tvTotalLokasi.setText(String.valueOf(filterTempat.size()));
                adapterListView = new ListViewAdapter(DataTempat.this, filterTempat);
                lvTempat.setAdapter(adapterListView);

                return false;
            }
        });
    }

    //Dialog Form Hapus Data
    private void DialogFormHapus(final String No, String namaiklan, String alamat, int spd, String gambarLink) {
        dialogForm = new AlertDialog.Builder(DataTempat.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.activity_detail_tempat, null);
        dialogForm.setView(dialogView);
        dialogForm.setCancelable(false);
        dialogForm.setTitle("Hapus Data");
        dialogForm.setIcon(R.mipmap.ic_delete);

        tvNamaTok = (TextView) dialogView.findViewById(R.id.tvNamaTok);
        tvAlamat = (TextView) dialogView.findViewById(R.id.tvAlamat);
        tvId= (TextView) dialogView.findViewById(R.id.tvId);
        /*tvSpd= (TextView) dialogView.findViewById(R.id.tvSpd);*/

        imgPerusahaan = (ImageView) dialogView.findViewById(R.id.baliho);


        tvNamaTok.setText(namaiklan);
        tvAlamat.setText(alamat);
        /*tvSpd.setText(String.valueOf(spd) + " Mbps");*/
        if (gambarLink.isEmpty() == true){
            imgPerusahaan.setBackgroundResource(R.drawable.kosong);
        }else {
            Picasso.with(DataTempat.this)
                    .load(gambarLink)
                    .resize(1080,540)
                    .into(imgPerusahaan);
        }

        dialogForm.setPositiveButton("Hapus Data", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                String url = Server.URL + "delete_lokasi.php"; // baca data ke server

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Hapus Data", response.toString());

                        try{
                            JSONObject objekData = new JSONObject(response);
                            String queryResult = objekData.getString("result");

                            if (queryResult.equals("berhasil")){
                                dialog.dismiss();
                                callData();
                                new PromptDialog(DataTempat.this)
                                        .setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS)
                                        .setAnimationEnable(true)
                                        .setTitleText("Berhasil")
                                        .setContentText("Data Telah Dihapus")
                                        .setPositiveListener("OK", new PromptDialog.OnPositiveListener() {
                                            @Override
                                            public void onClick(PromptDialog promptDialog) {
                                                promptDialog.dismiss();
                                            }
                                        })
                                        .show();
                                adapterListView.notifyDataSetChanged();
                            }else {
                                new PromptDialog(DataTempat.this)
                                        .setDialogType(PromptDialog.DIALOG_TYPE_WRONG)
                                        .setAnimationEnable(true)
                                        .setTitleText("Kesalahan")
                                        .setContentText("Gagal Menghapus Data")
                                        .setPositiveListener("OK", new PromptDialog.OnPositiveListener() {
                                            @Override
                                            public void onClick(PromptDialog promptDialog) {
                                                promptDialog.dismiss();
                                            }
                                        })
                                        .show();
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorKoneksi();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() {
                        // posting parameter ke delete_lokasi.php untuk dieksekusi
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("no_tempat", No);
                        return params;
                    }
                };

                Volley.newRequestQueue(DataTempat.this).add(stringRequest);
            }
        });

        dialogForm.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogForm.show();
    }

    //Dialog Form Edit Data
    private void DialogFormEdit(final String No, String namaiklan, String alamat, int spd, Double latitude, Double longitude, String gambarLink) {
        dialogForm = new AlertDialog.Builder(DataTempat.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_tambah_data, null);
        dialogForm.setView(dialogView);
        dialogForm.setCancelable(false);
        dialogForm.setTitle("Edit Data");
        dialogForm.setIcon(R.mipmap.ic_edit);

        //deklarasi objek yg ada didalam dialog form
        etNo = (EditText) dialogView.findViewById(R.id.etNo);
        etNamaTok = (EditText) dialogView.findViewById(R.id.etNamaTok);
        etAlamat = (EditText) dialogView.findViewById(R.id.etAlamat);
        /*etSpd = (EditText) dialogView.findViewById(R.id.etSpd);*/
        etLatitude = (EditText) dialogView.findViewById(R.id.etLatitude);
        etLongitude = (EditText) dialogView.findViewById(R.id.etLongitude);
        etGambar = (EditText) dialogView.findViewById(R.id.etGambar);

        etNo.setText(No);
        etNamaTok.setText(namaiklan);
        etAlamat.setText(alamat);
        /*etSpd.setText(String.valueOf(spd));*/
        etLatitude.setText(String.valueOf(latitude));
        etLongitude.setText(String.valueOf(longitude));
        etGambar.setText(gambarLink);

        etNo.setEnabled(false);
        etNo.setTextColor(getResources().getColor(android.R.color.darker_gray));
        etNamaTok.requestFocus();

        dialogForm.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                String url = Server.URL + "update_lokasi.php"; // baca data ke server

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Edit Data", response.toString());

                        try {
                            JSONObject objekData = new JSONObject(response);
                            String queryResult = objekData.getString("result");

                            //cek JSon result yang dihasilkan dari update_lokasi.php
                            if (queryResult.equals("berhasil")){
                                dialog.dismiss();
                                callData();
                                new PromptDialog(DataTempat.this)
                                        .setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS)
                                        .setAnimationEnable(true)
                                        .setTitleText("Berhasil")
                                        .setContentText("Data Telah Diperbarui")
                                        .setPositiveListener("OK", new PromptDialog.OnPositiveListener() {
                                            @Override
                                            public void onClick(PromptDialog promptDialog) {
                                                promptDialog.dismiss();
                                            }
                                        })
                                        .show();
                                adapterListView.notifyDataSetChanged();
                            }else {
                                new PromptDialog(DataTempat.this)
                                        .setDialogType(PromptDialog.DIALOG_TYPE_WRONG)
                                        .setAnimationEnable(true)
                                        .setTitleText("Kesalahan")
                                        .setContentText("Gagal Memperbarui Data")
                                        .setPositiveListener("OK", new PromptDialog.OnPositiveListener() {
                                            @Override
                                            public void onClick(PromptDialog promptDialog) {
                                                promptDialog.dismiss();
                                            }
                                        })
                                        .show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorKoneksi();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() {
                        // posting parameter ke update_lokasi.php untuk dieksekusi
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("no_tempat", etNo.getText().toString());
                        params.put("nama_iklan", etNamaTok.getText().toString());
                        params.put("alamat", etAlamat.getText().toString());
                        /*params.put("spd", etSpd.getText().toString());*/
                        params.put("latitude", etLatitude.getText().toString());
                        params.put("longitude", etLongitude.getText().toString());
                        params.put("gambar", etGambar.getText().toString());
                        return params;
                    }
                };

                Volley.newRequestQueue(DataTempat.this).add(stringRequest);

            }
        });

        dialogForm.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogForm.show();
    }

    //mengambil data dari server
    private void callData() {
        String url = Server.URL + "read_data.php"; // baca data ke server
        listTempat.clear();
        filterTempat.clear();
        mSwipeRefresh.setRefreshing(true);

        JsonArrayRequest arrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Data Lokasi", response.toString());

                tvTotalLokasi.setText(String.valueOf(response.length()));

                for (int i = 0; i < response.length(); i++){
                    try {
                        JSONObject objekData = response.getJSONObject(i);

                        data_tempat mData_tempat = new data_tempat();

                        mData_tempat.setNo(objekData.getString("no_tempat"));
                        mData_tempat.setNama_tok(objekData.getString("nama_iklan"));
                        mData_tempat.setAlamat(objekData.getString("alamat"));
                        /*mData_tempat.setSpd(objekData.getInt("spd"));*/
                        mData_tempat.setGambar(objekData.getString("gambar"));
                        mData_tempat.setLatitude(objekData.getDouble("latitude"));
                        mData_tempat.setLongitude(objekData.getDouble("longitude"));

                        listTempat.add(mData_tempat);
                        filterTempat.add(mData_tempat);
                        adapterListView = new ListViewAdapter(DataTempat.this, listTempat);
                        lvTempat.setAdapter(adapterListView);

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }

                adapterListView.notifyDataSetChanged();
                mSwipeRefresh.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errorKoneksi();
                tvTotalLokasi.setText("0");
                mSwipeRefresh.setRefreshing(false);
            }
        });
        Volley.newRequestQueue(this).add(arrayRequest);
    }

    //untuk refresh listview dengan cara di swipe layar
    @Override
    public void onRefresh() {
        listTempat.clear();
        filterTempat.clear();
        callData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;

        if (id == android.R.id.home){
            if (Server.Status_Proses.equals("SearchLoc")){
                intent = new Intent(DataTempat.this, MapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }else {
                intent = new Intent(DataTempat.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = null;

        if (Server.Status_Proses.equals("SearchLoc")){
            intent = new Intent(DataTempat.this, MapsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }else {
            intent = new Intent(DataTempat.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        startActivity(intent);

        super.onBackPressed();
    }

    private void errorKoneksi() {
        new PromptDialog(DataTempat.this)
                .setDialogType(PromptDialog.DIALOG_TYPE_WRONG)
                .setAnimationEnable(true)
                .setTitleText("Kesalahan")
                .setContentText("Gagal Terhubung ke Server, Mohon Periksa Koneksi Internet Anda")
                .setPositiveListener("OK", new PromptDialog.OnPositiveListener() {
                    @Override
                    public void onClick(PromptDialog promptDialog) {
                        promptDialog.dismiss();
                    }
                })
                .show();
    }
}
