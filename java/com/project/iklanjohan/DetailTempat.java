package com.project.iklanjohan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class DetailTempat extends AppCompatActivity {

    private TextView tvId, tvNo, tvNamaTok, tvAlamat;
    private ImageView imgPerusahaan, baliho;

    public static String No, namaiklan, alamat, gambar, spd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tempat);

        getSupportActionBar().setTitle("Detail Toko");
        getSupportActionBar().setSubtitle("Iklan Online");

        //menampilkan tombol back di action bar activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        tvId = (TextView) findViewById(R.id.tvId);
        tvNamaTok = (TextView) findViewById(R.id.tvNamaTok);
        tvAlamat = (TextView) findViewById(R.id.tvAlamat);
        /*tvSpd = (TextView) findViewById(R.id.tvSpd);*/

        imgPerusahaan = (ImageView) findViewById(R.id.baliho);

        /*setText(idPel);*/
        tvId.setText(No);
        tvNamaTok.setText(namaiklan);
        tvAlamat.setText(alamat);
        /*tvSpd.setText(spd + " Mbps");*/
        // picasso : image library yang digunakan untuk mengambil gambar dari server/internet
        if (gambar.isEmpty() == true){
            imgPerusahaan.setBackgroundResource(R.drawable.kosong);
        }else {
            Picasso.with(DetailTempat.this)
                    .load(gambar)
                    .resize(1080, 540)
                    .into(imgPerusahaan)
                    ;
        }
    }

    // untuk tombol back pada hp
    @Override
    public void onBackPressed() {
        backToMaps();
        finish();
    }

    // jika button back pada action bar di klik maka akan kembali ke MapsActivity
    // flag digunakan agar tidak akan kembali ke activity detail tempat(lagi) setelah button back pada action bar diklik
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            backToMaps();
        }

        return super.onOptionsItemSelected(item);
    }

    private void backToMaps() {
        Intent intent = new Intent(DetailTempat.this, MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
