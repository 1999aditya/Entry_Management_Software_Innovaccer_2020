package com.example.entrymanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class Check extends AppCompatActivity {

    EditText vn; //visitor's name
    EditText hn; //host's name
    EditText vem; //visitor's email
    EditText hem; //host's email
    EditText vpn; //visitor's phonenumuber
    EditText hpn; //host's phonenumuber
    EditText add; //host's address
    Button bt;  //check-in button
    final int SEND_SMS_PERMISSION_REQUEST_CODE = 0;

    private DatabaseReference dbrcurrent; //current node
    private DatabaseReference dbrvisitor; // visitor node
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checki);

        dbrcurrent = FirebaseDatabase.getInstance().getReference("current");
        dbrvisitor = FirebaseDatabase.getInstance().getReference("visitor");

        vn = findViewById(R.id.namev);
        hn = findViewById(R.id.nameh);
        vem = findViewById(R.id.emailv);
        hem = findViewById(R.id.emailh);
        vpn = findViewById(R.id.mobv);
        hpn = findViewById(R.id.mobh);
        add=findViewById(R.id.add);

        bt = findViewById(R.id.button3);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

    }

    public void submit() {
        final String vname = vn.getText().toString().trim();
        final String hname = hn.getText().toString().trim();
        final String vemail = vem.getText().toString().trim();
        final String hemail = hem.getText().toString().trim();
        final String vmob = vpn.getText().toString().trim();
        final String hmob = hpn.getText().toString().trim();
        final String address = add.getText().toString().trim();
       // final String intime;

        //checking visitor's name field
        if (vname.isEmpty()) {
            vn.setError("Name is Required!");
            vn.requestFocus();
            return;
        }

        //checking visitor's mobile number field
        if (vmob.isEmpty()) {
            vpn.setError("Mobile Number is Required!");
            vpn.requestFocus();
            return;
        }

        String regx = "^[6789][0-9]{9}$";
        Pattern pattern = Pattern.compile(regx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(vmob);
        if (!matcher.find()) {
            vpn.setError("Enter a Valid phone number!");
            vpn.requestFocus();
            return;
        }

        //checking visitor's Email field
        if (vemail.isEmpty()) {
            vem.setError("Email is Required!");
            vem.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(vemail).matches()) {
            vem.setError("Enter a valid email!");
            vem.requestFocus();
            return;
        }

        //checking host's name field
        if (hname.isEmpty()) {
            hn.setError("Name is Required!");
            hn.requestFocus();
            return;
        }
        //checking host's mobile number field
        if (hmob.isEmpty()) {
            hpn.setError("Mobile Numeber is Required!");
            hpn.requestFocus();
            return;
        }

        regx = "^[6789][0-9]{9}$";
        pattern = Pattern.compile(regx, Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(hmob);
        if (!matcher.find()) {
            hpn.setError("Enter a Valid phone number!");
            hpn.requestFocus();
            return;
        }

        //If host's and visitor's number cannot be same
        //these comments can be removed
        if(vmob.equals(hmob))
        {
            hpn.setError("This is Guest's Number");
            hpn.requestFocus();
            return;
        }

        //checking host's Email field
        if (hemail.isEmpty()) {
            hem.setError("Email is Required!");
            hem.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(hemail).matches()) {
            hem.setError("Enter a valid email!");
            hem.requestFocus();
            return;
        }

        //If host's and visitor's Email-Id cannot be same
        //these comments can be removed
        /*if(vemail.equals(hemail))
        {
            hem.setError("This is Guest's Email ID");
            hem.requestFocus();
            return;
        }*/

        //Address is empty
        if (address.isEmpty()) {
            add.setError("Address is Required!");
            add.requestFocus();
            return;
        }

        //taking check-In Time
        //intime = time();

//        Log.d("Time","current time is "+intime);

        //before giving entry, check whether visitor is already checked in or not
      dbrcurrent.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot)
          {
               if(match(dataSnapshot,vmob))
               {
                    visitorexist(vmob);
                    return;
               }
               else
               {
                   register();
                   return ;
               }
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
            if (ds.getKey().equals(phn))
            {
                return true;
            }
        }
        return false;
    }
    public void visitorexist(String vmob)
    {
        DatabaseReference dbr = FirebaseDatabase.getInstance().getReference("current").child(vmob);
        dbr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Toast.makeText(Check.this, "You have already checked in at "+
                        dataSnapshot.child("intime").getValue(String.class), Toast.LENGTH_LONG).show();
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void register()
    {

            final String vname = vn.getText().toString().trim();
            final String hname = hn.getText().toString().trim();
            final String vemail = vem.getText().toString().trim();
            final String hemail = hem.getText().toString().trim();
            final String vmob = vpn.getText().toString().trim();
            final String hmob = hpn.getText().toString().trim();
            final String address = add.getText().toString().trim();
            final String intime;
            intime = time();
            try {
                saveVisitorInfo(vname, hname, vemail, hemail, vmob, hmob, intime,address);
                //Thread.sleep(1000);
                Toast.makeText(Check.this, "Entry Stored Successfully at " + intime, Toast.LENGTH_SHORT).show();
                //sendmail(hemail,vname,vmob,vemail);
            }
            catch (Exception e) {
                Toast.makeText(Check.this, "Internal Error Occured", Toast.LENGTH_SHORT).show();
            }

             sendmail(hemail,vname,vmob,vemail); //send E-mail to host
            //For SMS
            //check if permission not granted
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED)
            {
                //if permissinon not granted then check whether user has denied the permission
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.SEND_SMS))
                {
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
                }
                else //if user has not denied the permission then pop will appear ,accept or deny
                {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},SEND_SMS_PERMISSION_REQUEST_CODE);
                }
            }
            else        //if permission granted
            {
                sendsms(hmob,vname,vmob,vemail);
            }

    }

    //after getting result of request permission ,the result will be passed through this method
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        final String vname = vn.getText().toString().trim();
        final String vemail = vem.getText().toString().trim();
        final String vmob = vpn.getText().toString().trim();
        final String hmob = hpn.getText().toString().trim();
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case SEND_SMS_PERMISSION_REQUEST_CODE :
            {
                if(grantResults.length>0&&grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    sendsms(hmob,vname,vmob,vemail);
                    Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void sendmail(String hemail, String vname, String vmob, String vemail)
    {
        String subject = "Visitor's Details";
        String message = "Name :- " + vname + "\n" +
                "Mobile No. :- "+ vmob + "\n" +
                "Email :- " +vemail  +"\n"+
                "This Person is coming to meet you .";

          JavaMailAPI JavaMailAPI = new JavaMailAPI(this,hemail,subject,message);
          JavaMailAPI.execute();
    }
    public String time()
    {
        Calendar calendar = Calendar.getInstance();
        //TimeZone tz = calendar.getTimeZone();
        SimpleDateFormat time = new SimpleDateFormat("hh:mm a ");
        String s = (time.format(calendar.getTime())).toString().trim();
        return s;
    }

    private void saveVisitorInfo(String vname,String hname,String vemail,String hemail,String vmob,String hmob,String intime,String address)
    {
        VisitorInformation visitorInformation=new VisitorInformation(vname,hname,vemail,hemail,vmob,hmob,intime,address);
        dbrcurrent.child(vmob).setValue(visitorInformation);
        dbrvisitor.child(vmob+" "+intime).setValue(visitorInformation);
    }

    public void sendsms(String hmob,String vname,String vmob,String vemail)
    {
        String message = "Name :- " + vname + "\n" +
                "Mobile No. :- "+ vmob + "\n" +
                "Email :- " +vemail  +"\n"+
                "This Person is coming to meet you .";

                SmsManager smgr = SmsManager.getDefault();
                smgr.sendTextMessage(hmob, null, message, null, null);
                //Toast.makeText(Check.this, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
               // Log.d("Tag","SMS Sent Successfully");

        }
}
