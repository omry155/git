package com.biu.ap2.winder.ex4;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * ImagesFragment- for swife the images
 */
public class ImagesFragment extends Fragment {

    ImageView imgMain;
    int counter;

    public ImagesFragment() {
        counter = 0;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_images, container, false);

        imgMain = (ImageView) view.findViewById(R.id.images_imgMain);
        imgMain.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeLeft() {
                counter++;
                setImage();
            }

            @Override
            public void onSwipeRight() {
                counter--;

                if (counter == 0)
                    counter = 3;

                setImage();
            }
        });

        return view;
    }

    private void setImage() {
        switch (counter % 3)
        {
            /*
            case 1:
                imgMain.setImageResource(R.drawable.img1_1);
                break;
            case 2:
                imgMain.setImageResource(R.drawable.img1_2);
                break;
            default:
                imgMain.setImageResource(R.drawable.img2);
                break;
                */
        }
    }


}