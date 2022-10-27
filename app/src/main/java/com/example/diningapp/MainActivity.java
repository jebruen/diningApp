package com.example.diningapp;

import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.diningapp.ui.main.SectionsPagerAdapter;
import com.example.diningapp.databinding.ActivityMainBinding;

import java.text.CollationElementIterator;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    AlertDialog dialog;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton add = binding.add;
        //layout = binding.container;
        layout = findViewById(R.id.container);
        buildDialog();


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                dialog.show();
            }
        });
    }

    private void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog, null);

        EditText name = view.findViewById(R.id.nameEdit);

        builder.setView(view);
        builder.setTitle("Enter name")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addCard(name.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        dialog = builder.create();
        }

    private void addCard(String toString) {
        View view = getLayoutInflater().inflate(R.layout.card, null);
        TextView nameView = view.findViewById(R.id.name);
        Button delete = view.findViewById(R.id.delete);
        
        nameView.setText("name");
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.removeView(view);
            }
        });
        layout.addView(view);
    }
}