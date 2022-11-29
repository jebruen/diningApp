package com.example.diningapp.ui.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.diningapp.DietrickActivity;
import com.example.diningapp.JohnstonActivity;
import com.example.diningapp.MainActivity;
import com.example.diningapp.OwensActivity;
import com.example.diningapp.R;
import com.example.diningapp.SquiresActivity;
import com.example.diningapp.TurnerActivity;
import com.example.diningapp.databinding.ActivityMainBinding;


public class Food extends Fragment {
    public Food() {

    }
    private ActivityMainBinding binding;
    private PageViewModel         pageViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
        View view = inflater.inflate(R.layout.food_fragment, container, false);
        CardView dietrick = (CardView) view.findViewById(R.id.card_view4);
        dietrick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDietrickActivity();
            }
        });

        CardView turner = (CardView) view.findViewById(R.id.card_view0);
        turner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewPager viewPager = (ViewPager) view.getRootView().findViewById(R.id.view_pager);
                PlaceholderFragment.currentDiningHall = "Turner Place";
                viewPager.setCurrentItem(2);
            }
        });

        CardView squires = (CardView) view.findViewById(R.id.card_view1);
        squires.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewPager viewPager = (ViewPager) view.getRootView().findViewById(R.id.view_pager);
                PlaceholderFragment.currentDiningHall = "Squires";
                viewPager.setCurrentItem(2);
                // openSquiresActivity();
            }
        });

        CardView owens = (CardView) view.findViewById(R.id.card_view2);
        owens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ViewPager viewPager = (ViewPager) view.getRootView().findViewById(R.id.view_pager);
                PlaceholderFragment.currentDiningHall = "Owens";
                viewPager.setCurrentItem(2);
            }
        });

        CardView johnston = (CardView) view.findViewById(R.id.card_view3);
        johnston.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openJohnstonActivity();
            }
        });
        return view;
    }

    // WORKS
    public void openTurnerActivity() {
        Intent intent = new Intent(getActivity(), TurnerActivity.class);
        startActivity(intent);
    }

    public void openSquiresActivity() {
        Intent intent = new Intent(getActivity(), SquiresActivity.class);
        startActivity(intent);
    }

    public void openOwensActivity() {
        Intent intent = new Intent(getActivity(), OwensActivity.class);
        startActivity(intent);
    }

    public void openJohnstonActivity() {
        Intent intent = new Intent(getActivity(), JohnstonActivity.class);
        startActivity(intent);
    }

    public void openDietrickActivity() {
        Intent intent = new Intent(getActivity(), DietrickActivity.class);
        startActivity(intent);
    }

}
