package com.example.v2_splashscreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Fragment1 extends Fragment {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static String empref = SignInPage.getEmpRef();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment1_layout,container,false);

        TextView bloodpress = (TextView)view.findViewById(R.id.bpid);
        TextView hrtrate = (TextView)view.findViewById(R.id.hrid);
        TextView bodytemp = (TextView)view.findViewById(R.id.tempid);
        TextView oxygen = (TextView)view.findViewById(R.id.oxyid);


        DatabaseReference ref = database.getReference(empref);

        ref.child("Sensor Data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    String bp = snapshot.child("bp").getValue().toString();
                    bloodpress.setText(bp);

                    String hr = snapshot.child("hr").getValue().toString();
                    hrtrate.setText(hr);

                    String bdtemp = snapshot.child("btemp").getValue().toString();
                    bodytemp.setText(bdtemp);

                    String oxyg = snapshot.child("oxy").getValue().toString();
                    oxygen.setText(oxyg);

                }catch (Exception e){
                   // Toast.makeText(FirstPage.this,"# Exception :"+e,Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}
