package com.example.diningapp;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.diningapp.databinding.ActivityMainBinding;
import com.example.diningapp.ui.main.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    public static final boolean USE_REMOTE_DATA = true;

    private ActivityMainBinding binding;
    private AlertDialog dialog;
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
        //layout = binding.container;
        layout = findViewById(R.id.container);
        // buildDialog();
    }

//    private void buildDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        View view = getLayoutInflater().inflate(R.layout.dialog, null);
//
//        EditText name        = view.findViewById(R.id.nameEdit);
//        EditText label       = view.findViewById(R.id.idEdtLabel);
//        EditText description = view.findViewById(R.id.idEdtDescription);
//        EditText amount      = view.findViewById(R.id.idEdtAmount);
//        EditText type        = view.findViewById(R.id.idEditType);
//        EditText diningHall  = view.findViewById(R.id.idEditDiningHall);
//        EditText otherInfo   = view.findViewById(R.id.idEditOtherInfo);
//
//        builder.setView(view);
//        builder.setTitle("Enter Food Item")
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        addCard(name.getText().toString());
//                        // below line is to get data from all edit text fields.
//                        String newname          = name.getText().toString();
//                        String newlabel         = label.getText().toString();
//                        String newdescription   = description.getText().toString();
//                        String newamount        = amount.getText().toString();
//                        String newtype          = type.getText().toString();
//                        String newdiningHall    = diningHall.getText().toString();
//                        String newotherInfo     = otherInfo .getText().toString();
//
//                        // TODO: Not add food item anymore, this should be deleted
//
//                        finish();
//                        startActivity(getIntent());
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//        dialog = builder.create();
//        }
//
//    private void addCard(String toString) {
//        View view = getLayoutInflater().inflate(R.layout.card, null);
//        TextView nameView = view.findViewById(R.id.name);
//        Button delete = view.findViewById(R.id.delete);
//
//        nameView.setText("name");
//        delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                layout.removeView(view);
//            }
//        });
//        layout.addView(view);
//    }


}