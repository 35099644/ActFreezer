package legacy.xzh;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btnRead;
/*    static MonitorThread monitorThread = new MonitorThread();
    static FreezerThread freezerThread=new FreezerThread();*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ComponentName com=new ComponentName("com.xzh.xposeddemo","com.xzh.xposeddemo.DetectionService");
        final Intent serviceIntent=new Intent();
        serviceIntent.setComponent(com);
        //startService(serviceIntent);
        btnRead= (Button) findViewById(R.id.buttonRead);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(serviceIntent);
            }
        });
    }

    protected void onDestroy(){
        super.onDestroy();
    }
}
