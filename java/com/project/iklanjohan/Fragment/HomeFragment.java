package com.project.iklanjohan.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.project.iklanjohan.MapsActivity;
import com.project.iklanjohan.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private ImageView imgBukaMap;
    private LinearLayout llBukaPeta;
    private SliderLayout imgSlider;
    private TextSliderView mTextSliderView;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        llBukaPeta = (LinearLayout) view.findViewById(R.id.llBukaPeta);
        imgBukaMap = (ImageView) view.findViewById(R.id.imgBukaPeta);
        llBukaPeta.setBackgroundResource(android.R.color.transparent);

        //set image slider
        imgSlider = (SliderLayout) view.findViewById(R.id.imgSlider);
        int [] gambar = {R.drawable.iklan};
        for (int i = 0; i < gambar.length; i++){
            mTextSliderView = new TextSliderView(getContext());
            mTextSliderView.description("Iklan Tersedia").image(gambar[i]).setScaleType(BaseSliderView.ScaleType.Fit);
            imgSlider.addSlider(mTextSliderView);
            imgSlider.setPresetTransformer(SliderLayout.Transformer.Foreground2Background); // memberikan efek animasi saat ganti slide
            imgSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom); // mengatur posisi indicator gambar
            imgSlider.setCustomAnimation(new DescriptionAnimation());
            imgSlider.setDuration(4000);
        }

        imgBukaMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llBukaPeta.setBackgroundResource(R.color.colorBayang);
                startActivity(new Intent(getContext(), MapsActivity.class));
            }
        });

        return view;
    }

}
