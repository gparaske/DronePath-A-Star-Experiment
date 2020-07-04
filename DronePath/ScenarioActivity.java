package com.example.dronepath;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;

//import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class ScenarioActivity extends AppCompatActivity {

    public static int sp_scenario_index = 0;
    public static int sp_algorithm_index = 0;
    private static Button btnLogin;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scenario);

        btnLogin = (Button)findViewById(R.id.login);

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                OnLoginClicked();
            }
        });

        // Add Combo Box with the Default Scenarios
        Spinner sp_scenario = findViewById(R.id.sp_scenario);
        String[] items = new String[]{"1. Few obstacles", "2. Many obstacles", "3. Too many obstacles"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        sp_scenario.setAdapter(adapter);
        sp_scenario_index = 0;
        sp_scenario.setSelection(adapter.getPosition(MapsActivity.DefaultScenario));
        sp_scenario.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (sp_scenario_index > 0) {
                    MapsActivity.DefaultScenario = adapterView.getItemAtPosition(i).toString().substring(0,1);
                }
                sp_scenario_index ++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Add Combo Box with the Default Algorithms
        Spinner sp_algorithm = findViewById(R.id.sp_algorithm);
        String[] items_algorithm = new String[]{"1. Straight line", "2. Ignore cost of turns", "3. Include cost of turns"};
        ArrayAdapter<String> adapter_algorithm = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items_algorithm);
        sp_algorithm.setAdapter(adapter_algorithm);
        sp_algorithm_index = 0;
        sp_algorithm.setSelection(adapter_algorithm.getPosition(MapsActivity.DefaultAlgorithm));
        sp_algorithm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (sp_algorithm_index > 0) {
                    MapsActivity.DefaultAlgorithm = adapterView.getItemAtPosition(i).toString().substring(0,1);
                }
                sp_algorithm_index ++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void OnLoginClicked(){
        Intent intent = new Intent(this, MapsActivity.class);
        //finish();
        startActivity(intent);
    }

    private void OnBackClicked(){
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
    }

    private void MessageBox(String title, String text){
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage(text)
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setTitle(title)
                .show();
    }
}
