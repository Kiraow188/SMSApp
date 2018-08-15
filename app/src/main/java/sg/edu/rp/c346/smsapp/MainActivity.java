package sg.edu.rp.c346.smsapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.Array;

public class MainActivity extends AppCompatActivity {
    EditText etTo, etMsg;
    Button btn, btnSend;
    BroadcastReceiver br = new MessageReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(br, filter);

        etMsg = findViewById(R.id.etMsg);
        etTo = findViewById(R.id.etTo);
        btn = findViewById(R.id.button);
        btnSend = findViewById(R.id.button2);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String to = etTo.getText().toString();
                String msg = etMsg.getText().toString();
                if (to.contains(",")){
                    String[] arrays = to.split(",");
                    for (int i=0; i<arrays.length; i++){
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(arrays[i], null, msg, null, null);
                    }
                }else {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(to, null, msg, null, null);
                }
                Toast.makeText(MainActivity.this,"Message Sent", Toast.LENGTH_LONG).show();
                etMsg.setText("");
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String to = etTo.getText().toString();
                String msg = etMsg.getText().toString();
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:"));
                sendIntent.putExtra("address", to);
                sendIntent.putExtra("sms_body", msg);
                startActivity(sendIntent);
                etMsg.setText("");
            }
        });
    }
    private void checkPermission() {
        int permissionSendSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int permissionRecvSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);
        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED &&
                permissionRecvSMS != PackageManager.PERMISSION_GRANTED) {
            String[] permissionNeeded = new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissionNeeded, 1);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(br);
    }
}
