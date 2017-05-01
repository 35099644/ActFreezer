package android.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.netlab.actfreezer.R;
import com.netlab.ui.NavigationDrawerActivity;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
    private Sample[] mSamples;
    private GridView mGridView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Prepare list of samples in this dashboard.
        mSamples = new Sample[]{
                new Sample(R.string.navigationdraweractivity_title, R.string.navigationdraweractivity_description,
                        NavigationDrawerActivity.class),
        };

        // Prepare the GridView
        mGridView = (GridView) findViewById(android.R.id.list);
        mGridView.setAdapter(new SampleAdapter());
        mGridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> container, View view, int position, long id) {
        startActivity(mSamples[position].intent);
    }

    private class SampleAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mSamples.length;
        }

        @Override
        public Object getItem(int position) {
            return mSamples[position];
        }

        @Override
        public long getItemId(int position) {
            return mSamples[position].hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.sample_dashboard_item,
                        container, false);
            }

            ((TextView) convertView.findViewById(android.R.id.text1)).setText(
                    mSamples[position].titleResId);
            ((TextView) convertView.findViewById(android.R.id.text2)).setText(
                    mSamples[position].descriptionResId);
            return convertView;
        }
    }

    private class Sample {
        int titleResId;
        int descriptionResId;
        Intent intent;

        private Sample(int titleResId, int descriptionResId, Intent intent) {
            this.intent = intent;
            this.titleResId = titleResId;
            this.descriptionResId = descriptionResId;
        }

        private Sample(int titleResId, int descriptionResId,
                       Class<? extends Activity> activityClass) {
            this(titleResId, descriptionResId,
                    new Intent(MainActivity.this, activityClass));
        }
    }
}





//        extends AppCompatActivity {
//
//
//    private Button startServiceButton = null;
//    private ToggleButton confButton = null;
//
//    private final String TAG = "MainActivity";
//
//
//    private SharedPreferences sharedPreferences = null;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//
//        try {
//            Runtime.getRuntime().exec("su");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        PackageManager pm = getPackageManager();
//        List<ApplicationInfo> pkgList = pm.getInstalledApplications(0);
//
//        /**
//         * List package info for all installed pkgs
//         */
//        String[] pkg_array = new String[pkgList.size()];
//        int[] uid_array = new int[pkgList.size()];
//        int index = 0;
//        for(ApplicationInfo info : pkgList)
//        {
//            pkg_array[index] =  info.packageName;
//            uid_array[index] =  info.uid;
//            index ++;
//        }
//
//        /**
//         * print them to console
//         */
//        for(String pkg : pkg_array)
//        {
//            System.out.println(",\""+pkg+"\"");
//        }
//        for(int uid: uid_array)
//        {
//            System.out.println(",\""+uid+"\"");
//        }
//
//
//
//        startServiceButton = (Button) findViewById(R.id.startServiceButton);
//
//        startServiceButton.setOnClickListener(new View.OnClickListener() {
//                                                  @Override
//                                                  public void onClick(View view) {
//                                                      startService(new Intent(MainActivity.this, ServiceLogger.class));
//                                                  }
//                                              }
//        );
//
//
//        sharedPreferences = getSharedPreferences("actfreezer", Context.MODE_WORLD_READABLE);
//
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean("actfreezer", GlobalSettings.getConf());
//        editor.commit();
//
//        confButton = (ToggleButton) findViewById(R.id.ConfToggleButton);
//        //System.err.println(GlobalSettings.getConf());
//        confButton.setChecked(GlobalSettings.getConf());
//        confButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
//                if (checked) {
//                    Log.d(TAG, "" + checked);
//                    GlobalSettings.setConf(checked);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putBoolean("actfreezer", checked);
//                    editor.commit();
//
//                    Intent intent = new Intent();
//                    intent.setAction("ActfreeezerData");
//                    intent.putExtra("configuration", ""+checked);
//                    sendBroadcast(intent);
//                } else {
//                    Log.d(TAG, "" + checked);
//                    GlobalSettings.setConf(checked);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("actfreezer", ""+checked);
//                    editor.commit();
//
//                    Intent intent = new Intent();
//                    intent.setAction("ActfreeezerData");
//                    intent.putExtra("configuration", ""+ checked);
//
//                    sendBroadcast(intent);
//                }
//            }
//        });
//        //startService(new Intent(this, ServiceLogger.class));
//    }
//}
