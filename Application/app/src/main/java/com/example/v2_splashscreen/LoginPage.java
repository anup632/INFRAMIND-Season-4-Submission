                                                                                              package com.example.v2_splashscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class LoginPage extends AppCompatActivity {

     private TextView loginbut;
     private Button regbut;
     private EditText nameid;
     private EditText empid;
     private EditText passid;
     private EditText confpassid;
     private EditText brancode;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //DatabaseReference myRef = database.getReference("anup");

    private static String EmpRef;

    //encryption
    String outputString;
    String AES = "AES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        getSupportActionBar().hide();//Remove action bar

        nameid = (EditText) findViewById(R.id.nameid);
        empid = (EditText) findViewById(R.id.empid);
        passid = (EditText) findViewById(R.id.passid);
        confpassid = (EditText) findViewById(R.id.confid);
        brancode = (EditText)findViewById(R.id.branchcodeid);

        //Already have an account login
        loginbut = (TextView) findViewById(R.id.alreadylogin);
        loginbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginPage.this,"Already have an account !!!!!!!",Toast.LENGTH_SHORT).show();
                Intent SigningIntent = new Intent(LoginPage.this, SignInPage.class);
                startActivity(SigningIntent);
                finish();
            }
        });

        //Register button
        regbut = (Button)findViewById(R.id.regbut);
        regbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Send to Firebase
                String nm = nameid.getText().toString();
                String emp = empid.getText().toString();
                String pass = passid.getText().toString();
                String conpass = confpassid.getText().toString();
                String branch = brancode.getText().toString();

                if(pass.equals(conpass) && !nm.isEmpty() && !emp.isEmpty() && !pass.isEmpty() && !branch.isEmpty()){
                    String FirePath = nm.concat("-").concat(branch).concat(emp);
                    DatabaseReference ref = database.getReference(FirePath);

                    EmpRef = FirePath;

                    //Encryption
                    try {
                        outputString = encrypt(passid.getText().toString(), pass);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ref.child("Personal Details").child("name").setValue(nm);
                    ref.child("Personal Details").child("id").setValue(emp);
                    ref.child("Personal Details").child("password").setValue(outputString);
                    //Sent
                    Snackbar.make(findViewById(android.R.id.content),"                      User Registered",Snackbar.LENGTH_LONG).show();
                    /*
                    Intent SigninIntent = new Intent(LoginPage.this, SignInPage.class);
                    startActivity(SigninIntent);
                    finish();
                    */
                }else
                {
                    Toast.makeText(LoginPage.this,"Please insert valid input",Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private String encrypt(String data, String password) throws Exception {
        SecretKeySpec key = generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] enVal = c.doFinal(data.getBytes());
        String encryptedValue = android.util.Base64.encodeToString(enVal, Base64.DEFAULT);   // Diff here
        return encryptedValue;
    }

    private SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }


    public static String getEmpRef()
    {
        return EmpRef;
    }

    public void HistoryFun(View view) {

        Intent HistoryIntent = new Intent(LoginPage.this, HistoryPage.class);
        startActivity(HistoryIntent);
        finish();
    }
}
