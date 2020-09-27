package com.project.iklanjohan.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.project.iklanjohan.Pojo.data_tempat;
import com.project.iklanjohan.R;

import java.util.List;


public class ListViewAdapter extends BaseAdapter{

    private Activity mActivity;
    private LayoutInflater mInflater;
    private List<data_tempat> mTempats;
    private TextView namaiklan, alamat;

    public ListViewAdapter(Activity activity, List<data_tempat> tempats) {
        mActivity = activity;
        mTempats = tempats;
    }

    //getCount untuk menampilkan banyaknya isi data
    @Override
    public int getCount() {
        return mTempats.size();
    }

    @Override
    public Object getItem(int position) {
        return mTempats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mInflater == null){
            mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null){
            convertView = mInflater.inflate(R.layout.item_list_data_tempat, null);
        }

        namaiklan = (TextView) convertView.findViewById(R.id.tvNamaTokLV);
        alamat = (TextView) convertView.findViewById(R.id.tvAlamatLV);

        data_tempat mData_tempat = mTempats.get(position);

        namaiklan.setText(mData_tempat.getNama_tok());
        alamat.setText(mData_tempat.getAlamat());

        return convertView;
    }
}
