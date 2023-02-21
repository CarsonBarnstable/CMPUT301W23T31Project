package com.example.cmput301w23t31project;


import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

// implements onClickListener for the onclick behaviour of button
// https://www.youtube.com/watch?v=UIIpCt2S5Ls
public class MainActivity extends AppCompatActivity implements ScanResultsFragment.OnFragmentInteractionListener {
    Button scanBtn,playerInfoBtn,exploreBtn,myScanBtn;
    TextView messageText, messageFormat;

    FirebaseFirestore QRdb;

    CollectionReference collectionReference;
    public String[] QRNames = new String[100];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home_screen);
        Intent intent = getIntent();

        QRdb = FirebaseFirestore.getInstance();
        collectionReference = QRdb.collection("QRCodes");

        // referencing and initializing
        // the button and textviews
        scanBtn = findViewById(R.id.home_screen_scan_code_button);
        //messageText = findViewById(R.id.textContent);
        //messageFormat = findViewById(R.id.textFormat);

        // adding listener to the button

        //referencing and initializing the Player Info Button
        playerInfoBtn = findViewById(R.id.home_screen_player_info_button);

        //referencing and initializing the Explore Button
        exploreBtn = findViewById(R.id.home_screen_explore_button);

        QRNames = Utilities.retrieveFileData(this.getResources(), 100);


        //referencing and initializing the My Scans Button
        myScanBtn = findViewById(R.id.home_screen_my_scans_button);


        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // we need to create the object
                // of IntentIntegrator class
                // which is the class of QR library
                /*
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.setPrompt("Scan a barcode or QR Code");
                intentIntegrator.setOrientationLocked(false);
                intentIntegrator.initiateScan();

                 */
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setPrompt("Scan a barcode");
                integrator.setCameraId(0); // Use a specific camera of the device
                integrator.setOrientationLocked(true);
                integrator.setBeepEnabled(true);
                integrator.setCaptureActivity(CaptureActivityPortrait.class);
                integrator.initiateScan();
            }
        });

        playerInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,PlayerInfoScreenActivity.class);
                startActivity(intent);
            }
        });

        exploreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ExploreScreenActivity.class);
                startActivity(intent);
            }
        });

        myScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MyScansScreenActivity.class);
                startActivity(intent);
            }
        });

    }
    public void onClickAppInfo(View view){
        Intent intent = new Intent(this, AppInfoScreenActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.hamburger_menu,menu);
        return true;
    }
    public void onClickLeaderboard(View view){
        Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.item2: {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            }
            /*
            case R.id.item3: {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            }


             */
            case R.id.item5: {
                Intent intent = new Intent(this, LeaderboardActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.item4: {
                Intent intent = new Intent(this, ExploreScreenActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.item6: {
                Intent intent = new Intent(this, PlayerInfoScreenActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.item7: {
                Intent intent = new Intent(this, MyAccountScreenActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.item8: {
                Intent intent = new Intent(this, AppInfoScreenActivity.class);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // if the intentResult is null then
        // toast a message as "cancelled"
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                // if the intentResult is not null we'll set
                // the content and format of scan message
                String hash = "";
                try {
                    hash = Utilities.hashQRCode(intentResult.getContents());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                int score = Utilities.getQRScore(hash);
                String n = ""+hash;

                String name = Utilities.getQRCodeName(hash, QRNames);

                HashMap<String, QRCode> QRData = new HashMap<>();
                QRData.put("Code Info", new QRCode(name, score));
                collectionReference.document(name).set(QRData);
                new ScanResultsFragment(name, score).show(getSupportFragmentManager(), "SCAN RESULTS");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            String n = "";
            new ScanResultsFragment(n, 0).show(getSupportFragmentManager(), "SCAN RESULTS");
        }
    }

    @Override
    public void onOkPressed(){
    }


}
