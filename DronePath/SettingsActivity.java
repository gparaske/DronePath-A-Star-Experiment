package com.example.dronepath;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    public static TextView txtSignalStrengthValue;
    public static SeekBar seekBar;
    public static TextView txtSignalRadiusValue;
    public static SeekBar seekBarRadius;
    public static TextView txtSquareDimensionsValue;
    public static SeekBar seekBarSqareDimension;
    public static int spinnerFirstTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Reference to TextViews
        txtSignalStrengthValue = (TextView)findViewById(R.id.signalstrengthvalue);
        txtSignalRadiusValue = (TextView)findViewById(R.id.signalradiusvalue);
        txtSquareDimensionsValue = (TextView)findViewById(R.id.squaredimensionsvalue);

        // Add the Back Arrow on Action Bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Add Slider with the Default Signal Strength
        seekBar=(SeekBar) findViewById(R.id.simpleSeekBar);
        seekBar.setProgress(MapsActivity.DefaultSignalStrength);
        txtSignalStrengthValue.setText(String.valueOf(MapsActivity.DefaultSignalStrength));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MapsActivity.DefaultSignalStrength = progress;
                txtSignalStrengthValue.setText(String.valueOf(MapsActivity.DefaultSignalStrength));
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                //
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                //
            }
        });

        // Add Slider with the Default Signal Radius
        seekBarRadius=(SeekBar) findViewById(R.id.seekbarradius);
        seekBarRadius.setProgress(MapsActivity.DefaultRadius);
        txtSignalRadiusValue.setText(String.valueOf(MapsActivity.DefaultRadius));
        seekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MapsActivity.DefaultRadius = progress;
                txtSignalRadiusValue.setText(String.valueOf(MapsActivity.DefaultRadius));
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                //
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                //
            }
        });

        // Add Slider with the Square Dimension
        seekBarSqareDimension=(SeekBar) findViewById(R.id.seekbarsquaredimension);
        seekBarSqareDimension.setProgress(MapsActivity.DefaultSqareDimension);
        txtSignalRadiusValue.setText(String.valueOf(MapsActivity.DefaultSqareDimension));
        seekBarSqareDimension.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MapsActivity.DefaultSqareDimension = progress;
                txtSquareDimensionsValue.setText(String.valueOf(MapsActivity.DefaultSqareDimension));
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                //
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                //
            }
        });

        // Add Combo Box with the Default Cell Types
        Spinner spinner = findViewById(R.id.spinner1);
        String[] items = new String[]{"Femtocell", "Picocell", "Microcell", "Macrocell"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
        spinnerFirstTime = 0;
        spinner.setSelection(adapter.getPosition(MapsActivity.DefaultCell));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinnerFirstTime > 0) {
                    MapsActivity.DefaultCell = adapterView.getItemAtPosition(i).toString();
                    switch (MapsActivity.DefaultCell){
                        case "Femtocell":
                            MapsActivity.DefaultSignalStrength = 90;
                            MapsActivity.DefaultRadius = 10;
                            break;
                        case "Picocell":
                            MapsActivity.DefaultSignalStrength = 90;
                            MapsActivity.DefaultRadius = 200;
                            break;
                        case "Microcell":
                            MapsActivity.DefaultSignalStrength = 90;
                            MapsActivity.DefaultRadius = 2000;
                            break;
                        case "Macrocell":
                            MapsActivity.DefaultSignalStrength = 90;
                            MapsActivity.DefaultRadius = 35000;
                            break;
                    }
                    txtSignalStrengthValue.setText(String.valueOf(MapsActivity.DefaultSignalStrength));
                    SettingsActivity.seekBar.setProgress(MapsActivity.DefaultSignalStrength);
                    txtSignalRadiusValue.setText(String.valueOf(MapsActivity.DefaultRadius));
                    SettingsActivity.seekBarRadius.setProgress(MapsActivity.DefaultRadius);
                }
                spinnerFirstTime ++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        this.overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                this.overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
