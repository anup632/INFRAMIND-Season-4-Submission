package com.example.v2_splashscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInPage extends AppCompatActivity {

    private TextView back ;
    private Button logbut;
    private EditText usrid;
    private EditText passid;
    private static String empref = LoginPage.getEmpRef();

    FirebaseDatabase database = FirebaseDatabase.getInstance();   //added


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_page);

        usrid = (EditText)findViewById(R.id.userid);
        passid = (EditText)findViewById(R.id.passwordid);
        Toast.makeText(SignInPage.this,"Userid "+empref,Toast.LENGTH_SHORT).show();
        getSupportActionBar().hide();// hide appbar

        //Back to Login page
        back = (TextView)findViewById(R.id.backbut);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SignInPage.this,"Create an account !!!!!!!",Toast.LENGTH_SHORT).show();
                Intent LoginPage = new Intent(SignInPage.this, LoginPage.class);
                startActivity(LoginPage);
                finish();
            }
        });

        //Login Button to First Page
        logbut = (Button)findViewById(R.id.loginbut);
        logbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    String user = usrid.getText().toString();
                    String pass = passid.getText().toString();
                    empref = user;
                    Toast.makeText(SignInPage.this, "Userid " + empref, Toast.LENGTH_SHORT).show();
                    //DatabaseReference ref = database.getReference(empref);     // added


                    Intent FirstIntent = new Intent(SignInPage.this, TabbedPage.class);
                    startActivity(FirstIntent);
                    finish();

                }// try block
                catch (Exception e){
                    Toast.makeText(SignInPage.this,"#Exception :"+e,Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    // This is the path to the employee account who logged in
    public  static String getEmpRef()
    {
        return empref;
    }
}