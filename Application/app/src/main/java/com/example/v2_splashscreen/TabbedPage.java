package com.example.v2_splashscreen;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Vibrator;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.v2_splashscreen.ui.main.SectionsPagerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class TabbedPage extends AppCompatActivity {

    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList barEntries;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static String empref = SignInPage.getEmpRef();

    //Flashlight
    private static final int CAMERA_REQUEST = 123;
    boolean hasCameraFlash = false;
    //end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_page);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = findViewById(R.id.fab);
        barChart = findViewById(R.id.graphid);

        final int SEND_SMS_PERMISSION_REQUEST_CODE = 0;
        //added

        DatabaseReference ref = database.getReference(empref);

        ref.child("Sensor Data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    String bp = snapshot.child("bp").getValue().toString();
                    int numbp = Integer.parseInt(bp);
                    String hr = snapshot.child("hr").getValue().toString();
                    int numhr = Integer.parseInt(hr);
                    String bdtemp = snapshot.child("btemp").getValue().toString();
                    int numbdtemp = Integer.parseInt(bdtemp);
                    String oxyg = snapshot.child("oxy").getValue().toString();
                    int numoxyg = Integer.parseInt(oxyg);
                    //Graph
                    getEntries(numbp, numhr, numbdtemp, numoxyg);
                    barDataSet = new BarDataSet(barEntries,"Data Set");
                    barData = new BarData(barDataSet);
                    barChart.setData(barData);
                    barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    barDataSet.setValueTextColor(Color.WHITE);
                    barDataSet.setValueTextSize(16f);
                }catch (Exception e){
                    Toast.makeText(TabbedPage.this,"# Exception :"+e, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }); //reference child: Sensor Data

        //end
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "      Alert Message Sent!!!", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                String phoneNumber = "9561921710";
                String message = "Alert !!! \nThe user with Unique id: " +
                        empref+" is having a medical emergency.";
                if(checkPermission(Manifest.permission.SEND_SMS)){
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber,null, message, null,null);
                }else{
                    ActivityCompat.requestPermissions(TabbedPage.this,
                            new String[]{Manifest.permission.SEND_SMS},
                            SEND_SMS_PERMISSION_REQUEST_CODE);
                    Toast.makeText(TabbedPage.this,
                            "Permission Denied", Toast.LENGTH_LONG).show();
                }
            }
        }); // fab function ::Floating point

        ref.child("Stress").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //VIBRATION Resource : YT: https://www.youtube.com/watch?v=_6DlOsSNuik
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                final long[] pattern = {1000, 1000}; //sleep: 2000 milisecond vibrate: 1000 milisecond

                //Flashlight blinking Resource : YT : https://www.youtube.com/watch?v=smincTI_Yfg


                String vstress = snapshot.child("stressval").getValue().toString();
                if(vstress.equals("YES") || vstress.equals("yes") || vstress.equals("Yes"))
                {
                    Snackbar.make(findViewById(android.R.id.content),"You are STRESSED !!!",Snackbar.LENGTH_LONG).show();
                    vibrator.vibrate(pattern, 0); // 0 means repeat forever -1 means not repeat
                    //Flashlight
                    flashLightOn();
                    blinkFlash();
                }
                else{
                    vibrator.cancel();
                    //Flashlight
                    flashLightOff();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    } //onCreate function

    private void getEntries(int bp, int hr, int bdtemp, int oxyg){
        barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(1f,bp));
        barEntries.add(new BarEntry(2f,hr));
        barEntries.add(new BarEntry(3f,bdtemp));
        barEntries.add(new BarEntry(4f,oxyg));
    }
    public boolean checkPermission(String permission){
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void flashLightOn() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //added
                int temp = 0,i;
                //end
                cameraManager.setTorchMode(cameraId, true);
            }
        } catch (CameraAccessException e) {
        }
    } //Flash ON

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void flashLightOff() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
        }
    } //Flash OFF


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void blinkFlash() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String myString = "010101010101010101010101010101010";
        long blinkDelay = 50; //Delay in ms
        for (int i = 0; i < myString.length(); i++) {

            if (myString.charAt(i) == '0') {
                try {
                    String cameraId = cameraManager.getCameraIdList()[0];
                    cameraManager.setTorchMode(cameraId, true);
                } catch (CameraAccessException e) {
                }
            } else {
                try {
                    String cameraId = cameraManager.getCameraIdList()[0];
                    cameraManager.setTorchMode(cameraId, false);
                } catch (CameraAccessException e) {
                }
            }
            try {
                Thread.sleep(blinkDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    } // Blink function

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasCameraFlash = getPackageManager().
                            hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                } else {

                    Toast.makeText(TabbedPage.this, "Permission Denied for the Camera", Toast.LENGTH_SHORT).show();
                }
                break;
        }//Switch statement
        //Flashlight blinking Resource : YT : https://www.youtube.com/watch?v=smincTI_Yfg
    }//onRequestPermission function
} //AppCompatActivity