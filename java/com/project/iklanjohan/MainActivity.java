package com.project.iklanjohan;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.project.iklanjohan.Fragment.AboutFragment;
import com.project.iklanjohan.Fragment.HomeFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import cn.refactor.lib.colordialog.ColorDialog;

public class MainActivity extends AppCompatActivity {

    //private agar hanya dapat digunakan oleh main activity saja
    private BottomBar mBottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //action pada Bottom Bar
        mBottomBar = (BottomBar) findViewById(R.id.bottomBar);
        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            Fragment mFragment = null;
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tabHome){
                    mFragment = new HomeFragment();
                }else if (tabId == R.id.tabAbout){
                    mFragment = new AboutFragment();
                }else if (tabId == R.id.tabAdmin){
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content, mFragment)
                        .commit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        ColorDialog dialog = new ColorDialog(MainActivity.this);
        dialog.setTitle("PERHATIAN");
        dialog.setContentText("Apakah Anda ingin keluar ?");
        dialog.setPositiveListener("Ya", new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog colorDialog) {
                Server.STATUS_LOGIN = false;
                finish();
            }
        });
        dialog.setNegativeListener("Tidak", new ColorDialog.OnNegativeListener() {
            @Override
            public void onClick(ColorDialog colorDialog) {
                colorDialog.dismiss();
            }
        });
        dialog.setAnimationEnable(true);
        dialog.setColor(getResources().getColor(R.color.colorPrimary));
        dialog.setCancelable(false);
        dialog.show();
    }

}
