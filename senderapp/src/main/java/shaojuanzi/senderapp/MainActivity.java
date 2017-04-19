package shaojuanzi.senderapp;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       Intent serviceIntent=new Intent();
       serviceIntent.setComponent(new ComponentName("shaojuanzi.receiverapp", "shaojuanzi.receiverapp.ReceiverService"));

        startService(serviceIntent);
    }
}
