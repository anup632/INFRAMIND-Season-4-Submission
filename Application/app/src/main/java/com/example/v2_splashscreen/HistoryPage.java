package com.example.v2_splashscreen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.UUID;

public class HistoryPage extends AppCompatActivity {
    private EditText bpresid;
    private EditText hrtid;
    private EditText respid;
    private EditText allerid;
    private EditText otherid;
    //Storage
    private ImageView uploadimg;
    public Uri imageurl;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    //end

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static String empref = LoginPage.getEmpRef();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_page);


       // getSupportActionBar().hide();//Remove action bar
        getSupportActionBar().setTitle("Medical History"); //appbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //back button next on manifest file set parent activity name end.


        //storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        bpresid = (EditText)findViewById(R.id.hisbpid);
        hrtid = (EditText)findViewById(R.id.hishrid);
        respid = (EditText)findViewById(R.id.hisoxyid);
        allerid = (EditText)findViewById(R.id.hisallerid);
        otherid = (EditText)findViewById(R.id.hisotherid);
    }


    public void SubmitFun(View view) {

        Intent SigninIntent = new Intent(HistoryPage.this, SignInPage.class);
        startActivity(SigninIntent);
        finish();
        try {
            String bp = bpresid.getText().toString();
            String hr = hrtid.getText().toString();
            String resp = respid.getText().toString();
            String allergy = allerid.getText().toString();
            String other = otherid.getText().toString();

            DatabaseReference ref = database.getReference(empref);

            ref.child("Medical History").child("bp").setValue(bp);
            ref.child("Medical History").child("hr").setValue(hr);
            ref.child("Medical History").child("respiratory").setValue(resp);
            ref.child("Medical History").child("allergy").setValue(allergy);
            ref.child("Medical History").child("other").setValue(other);

            //Intent SigninIntent = new Intent(HistoryPage.this, SignInPage.class);
            //startActivity(SigninIntent);
            //finish();
        }//try block
        catch (Exception e){
            Toast.makeText(HistoryPage.this,"# Exception :"+e,Toast.LENGTH_LONG).show();
        }

    }

    public void CheckFun1(View view) {
        bpresid.setEnabled(true);
        bpresid.setVisibility(view.VISIBLE);
        bpresid.setHint("Desc:BPlevel");
    }

    public void CheckFun2(View view) {
        hrtid.setEnabled(true);
        hrtid.setVisibility(view.VISIBLE);
        hrtid.setHint("Desc: HRlevel");
    }
    public void CheckFun3(View view) {
        respid.setEnabled(true);
        respid.setVisibility(view.VISIBLE);
        respid.setHint("Desc: Resplevel");
    }
    public void CheckFun4(View view) {
        allerid.setEnabled(true);
        allerid.setVisibility(view.VISIBLE);
        allerid.setHint("Desc: Allergy");
    }
    public void CheckFun5(View view) {
        otherid.setEnabled(true);
        otherid.setVisibility(view.VISIBLE);
        otherid.setHint("Desc: Disease");
    }

    public void UploadFun(View view) {
        uploadimg = (ImageView) findViewById(R.id.uploadimgview);
        uploadimg.setVisibility(view.VISIBLE);

        chooseImage();
    }

    private void chooseImage() {
        try {


            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 1);

        }catch (Exception e)
        {
            Snackbar.make(findViewById(android.R.id.content),"#2 Exception Occured: "+e,Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {


            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

                imageurl = data.getData();
                uploadimg.setImageURI(imageurl);
                uploadImage();
            }
        }catch (Exception e){
            Snackbar.make(findViewById(android.R.id.content),"#3 Exception Occured: "+e,Snackbar.LENGTH_LONG).show();
        }
    }

    private void uploadImage() {

        try {


            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Uploading Image....");
            pd.show();

            final String randomkey = UUID.randomUUID().toString();
            final String filename = "abc.jpg";
            StorageReference riversRef = storageReference.child(empref+"/"+filename);

            riversRef.putFile(imageurl)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            Snackbar.make(findViewById(android.R.id.content), "Image Uploaded", Snackbar.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed to Upload", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                            //double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            double progressper = (snapshot.getBytesTransferred() / snapshot.getTotalByteCount()) * 100;
                            pd.setMessage("Percentage: "+ (int)progressper+"%");

                        }
                    });

        }// Try
        catch (Exception e){
            Snackbar.make(findViewById(android.R.id.content),"#4 Exception Occured: "+e,Snackbar.LENGTH_LONG).show();
        }
    }

}