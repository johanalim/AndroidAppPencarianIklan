package com.project.iklanjohan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.os.Handler;

import cn.refactor.lib.colordialog.PromptDialog;

public class Splashscreen extends AppCompatActivity {

    private PromptDialog mPromptDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // menghilangkan title

        mPromptDialog = new PromptDialog(this);
        mPromptDialog.setDialogType(PromptDialog.DIALOG_TYPE_INFO);
        mPromptDialog.setAnimationEnable(true);
        mPromptDialog.setTitleText("Info");
        mPromptDialog.setCancelable(false);
        mPromptDialog.setContentText("Aplikasi ini membutuhkan koneksi internet");
        mPromptDialog.setPositiveListener("OK", new PromptDialog.OnPositiveListener() {
            @Override
            public void onClick(PromptDialog promptDialog) {
                promptDialog.dismiss();
                setContentView(R.layout.activity_splashscreen);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(Splashscreen.this, MainActivity.class));
                    }
                }, 2000);
            }
        });
        mPromptDialog.show();
    }
}
