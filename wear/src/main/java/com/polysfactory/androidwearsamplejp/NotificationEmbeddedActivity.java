package com.polysfactory.androidwearsamplejp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;

public class NotificationEmbeddedActivity extends Activity {

    private static final String START_ACTIVITY_PATH = "/start/MainActivity";
    private static final String TAG = "TEST";
    private GoogleApiClient mGoogleApiClient;
    public static final String EXTRA_KEY_COUNT = "extra_key_count";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d(TAG, "Google Api Client connected");
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
//                                restoreCurrentCount();
                                return null;
                            }
                        }.execute();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                    }
                }).build();
        mGoogleApiClient.connect();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                sendMessageToStartActivity();
                return null;
            }
        }.execute();

//        TextView textView = (TextView) findViewById(R.id.text);
//        Intent intent = getIntent();
//        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//        Intent batteryStatus = registerReceiver(mBroadcastReceiver, intentFilter);
//        try {
//            int count = intent.getIntExtra(EXTRA_KEY_COUNT, -1);
////            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
//            if (count >= 0) {
//                textView.setText(String.format("count is %d", count));
//            } else {
//                textView.setText("なんだて？");
//            }
//        } catch (Exception e) {
//            Log.d( "t", e.getMessage());
//        }

    }


    private void sendMessageToStartActivity() {
        Collection<String> nodes = getNodes();
        for (String node : nodes) {
            MessageApi.SendMessageResult result =
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, node, START_ACTIVITY_PATH, null).await();
            if (!result.getStatus().isSuccess()) {
                Log.e(TAG, "ERROR: failed to send Message: " + result.getStatus());
            }
        }
    }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();

        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int status = intent.getIntExtra("status", 0);
                int health = intent.getIntExtra("health", 0);
                boolean present = intent.getBooleanExtra("present", false);
                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 0);
                int icon_small = intent.getIntExtra("icon-small", 0);
                int plugged = intent.getIntExtra("plugged", 0);
                int voltage = intent.getIntExtra("voltage", 0);
                int temperature = intent.getIntExtra("temperature", 0);
                String technology = intent.getStringExtra("technology");

                String statusString = "";

                switch (status) {
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:
                        statusString = "unknown";
                        break;
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        statusString = "charging";
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        statusString = "discharging";
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        statusString = "not charging";
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        statusString = "full";
                        break;
                }

                String healthString = "";

                switch (health) {
                    case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                        healthString = "unknown";
                        break;
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                        healthString = "good";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                        healthString = "overheat";
                        break;
                    case BatteryManager.BATTERY_HEALTH_DEAD:
                        healthString = "dead";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                        healthString = "voltage";
                        break;
                    case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                        healthString = "unspecified failure";
                        break;
                }

                String acString = "";

                switch (plugged) {
                    case BatteryManager.BATTERY_PLUGGED_AC:
                        acString = "plugged ac";
                        break;
                    case BatteryManager.BATTERY_PLUGGED_USB:
                        acString = "plugged usb";
                        break;
                }

                Log.v("status", statusString);
                Log.v("health", healthString);
                Log.v("present", String.valueOf(present));
                Log.v("level", String.valueOf(level)); // ここがバッテリー量
                Log.v("scale", String.valueOf(scale));
                Log.v("icon_small", String.valueOf(icon_small));
                Log.v("plugged", acString);
                Log.v("voltage", String.valueOf(voltage));
                Log.v("temperature", String.valueOf(temperature));
                Log.v("technology", technology);
            }
        }
    };

}
