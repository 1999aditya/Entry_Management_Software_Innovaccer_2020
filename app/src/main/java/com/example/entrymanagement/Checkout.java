package com.example.entrymanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Checkout extends AppCompatActivity {

    LinearLayout linear ;
    TextView clear;
    TextView mob; //visitor's number
    TextView email; //visitor's email
    TextView name; //visitors's name
    TextView out; // checkout time
    TextView time; // intime time
    TextView host; //host's name
    TextView hosta;//host's address
    EditText txt;
    Button bt;
    private DatabaseReference dbrcurrent = FirebaseDatabase.getInstance().getReference("current");
    private DatabaseReference dbrvisitor = FirebaseDatabase.getInstance().getReference("visitor");
    private Object timepickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        linear = findViewById(R.id.linear1);
        linear.setVisibility(linear.INVISIBLE);
        clear = findViewById(R.id.textView);
        clear.setVisibility(clear.INVISIBLE);
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        mob= findViewById(R.id.mob);
        time = findViewById(R.id.intime);
        out=findViewById(R.id.outtime);
        host=findViewById(R.id.host);
        hosta=findViewById(R.id.hosta);
        txt = findViewById(R.id.mobv1);
        bt=findViewById(R.id.register);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gettime();
                clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        linear.setVisibility(linear.INVISIBLE);
                        txt.setText("");
                        clear.setVisibility(clear.INVISIBLE);
                    }
                });
            }
        });

    }
    public void gettime()
    {
        final String phn = txt.getText().toString().trim();

        //checking visitor's mobile number field
        if (phn.isEmpty()) {
            txt.setError("Mobile Number is Required!");
            txt.requestFocus();
            return;
        }
        String regx = "^[6789][0-9]{9}$";
        Pattern pattern = Pattern.compile(regx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(phn);
        if (!matcher.find()) {
            txt.setError("Enter a Valid phone number!");
            txt.requestFocus();
            return;
        }

        // before giving exit checking whether already checked in or not

        dbrcurrent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(match(dataSnapshot,phn))
                {
                    //if matches then store visitor details and delete
                    val(phn);
                    return;

                }
                else
                {
                    //if dosent matches means visitor is not checked in
                    Toast.makeText(Checkout.this,"You have not checked in ",Toast.LENGTH_SHORT).show();
                }
                return ;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

         }
    public boolean match(DataSnapshot dataSnapshot,String phn)
    {
        for(DataSnapshot ds: dataSnapshot.getChildren())
        {
            if(ds.getKey().equals(phn))
            {
                return true;
            }
        }
        return false;
    }
    public void val(String vmob)
    {
        // now get data snapshot of visitor to take his/her details
        DatabaseReference dbr = FirebaseDatabase.getInstance().getReference("current").child(vmob);
        dbr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String vname = dataSnapshot.child("vname").getValue(String.class);
                name.setText("Guest :- "+vname);
                final String hname = dataSnapshot.child("hname").getValue(String.class);
                host.setText("Host :- "+hname);
                final String vemail = dataSnapshot.child("vemail").getValue(String.class);
                email.setText("Email :- "+vemail);
                final String hemail = dataSnapshot.child("hemail").getValue(String.class);
                final String vmob = dataSnapshot.child("vmob").getValue(String.class);
                mob.setText("Mobile Number :- "+ vmob);
                final String hmob = dataSnapshot.child("hmob").getValue(String.class);
                final String intime= dataSnapshot.child("intime").getValue(String.class);
                time.setText("Check-In Time :- " +intime);
                final String add = dataSnapshot.child("address").getValue(String.class);
                hosta.setText("Host Address :- "+add);

                 Log.d("TAG","answer is " +dataSnapshot.child("vmob").getValue(String.class)+" " +vname);
                Log.d("TAG","here");

                Calendar calendar = Calendar.getInstance();
                int currenthour = calendar.get(Calendar.HOUR_OF_DAY);
                int currentmin = calendar.get(Calendar.MINUTE);
                Log.d("TAG",String.valueOf(currenthour)+" "+String.valueOf(currentmin));

                //taking check-out time from visitor
                 TimePickerDialog timePickerDialog = new TimePickerDialog(Checkout.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        clear.setVisibility(clear.VISIBLE);

                        if(hourOfDay>=12)
                        {
                            String t;
                            if(hourOfDay==12)
                            {
                                t=String.format("%02d:%02d",hourOfDay,minutes)+" PM";
                                out.setText("Check-Out Time :- "+t.trim());
                                linear.setVisibility(linear.VISIBLE);
                                //updating visitor node
                                dbrvisitor.child(vmob+" "+intime).child("outtime").setValue(t);

                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                sendmail(vname, hname, vemail, vmob, intime, add, t.trim());
                                del(vmob);
                                return ;
                            }
                            else
                            {
                                t=String.format("%02d:%02d",hourOfDay-12,minutes)+" PM";
                                out.setText("OutTime :- "+t.trim());
                                linear.setVisibility(linear.VISIBLE);

                                //updating visitor node
                                dbrvisitor.child(vmob+" "+intime).child("outtime").setValue(t);
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                sendmail(vname, hname, vemail, vmob, intime, add, t.trim());
                                del(vmob);
                                return;
                            }
                        }
                        else
                        {
                            String t=String.format("%02d:%02d",hourOfDay,minutes)+" AM";
                            out.setText("OutTime :- "+t.trim());
                            linear.setVisibility(linear.VISIBLE);

                            //updating visitor node
                            dbrvisitor.child(vmob+" "+intime).child("outtime").setValue(t);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            sendmail(vname, hname, vemail, vmob, intime, add, t.trim());
                            del(vmob);
                            return ;
                        }

                    }
                }, currenthour, currentmin, false);

                timePickerDialog.show();
                return ;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }
    public void del(String phn)
    {
        Log.d("TAG", "del: came here");
        //after taking all details of visitor delete it.
        DatabaseReference del = FirebaseDatabase.getInstance().getReference("current").child(phn);
        del.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("TAG", "onComplete: DeleteSuccessful");
            }
        });
    }

    public void sendmail(String vname,String hname,String vemail,String vmob,String intime,String add,String outtime)
    {
        Log.d("TAG", "sendmail: came here");
        String subject = "Thank's For Visiting";
        String message = "Name :- " + vname + "\n" +
                "Mobile No. :- "+ vmob + "\n" +
                "Check-in time :- "+ intime + "\n" +
                "Check-out time :- "+ outtime + "\n" +
                "Host :- "+ hname + "\n" +
                "Address :- " +add +"." ;

        JavaMailAPI JavaMailAPI = new JavaMailAPI(Checkout.this,vemail,subject,message);
        JavaMailAPI.execute();

//        Toast.makeText(Checkout.this,vname+" " +hname+" " +vemail+" "+hemail+" " +vmob+" "+
               // hmob+" "+intime+" "+outtime,Toast.LENGTH_LONG).show();
    }



}
