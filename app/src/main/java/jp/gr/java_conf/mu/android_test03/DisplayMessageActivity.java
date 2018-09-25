package jp.gr.java_conf.mu.android_test03;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

public class DisplayMessageActivity extends AppCompatActivity {


    private BluetoothAdapter mBtAdapter;
    private TextView mResultView;
    private String mResult = "";
    // ブロードキャストレシーバの定義
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("mezamashi",action);
            if (BluetoothDevice.ACTION_FOUND.equals(action) || BluetoothDevice.ACTION_NAME_CHANGED.equals(action)){
        // 見つけたデバイス情報の取得
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mResult += "Device : " + device.getName() + "/" + device.getAddress() + "\n";
    //            mResultView.setText(mResult);
                Log.d("mezamashi",mResult);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);


        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // 端末がそもそもBlueToothをサポートしているかの確認
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null){
            //Bluetooth非対応端末の場合の処理
            Log.d("mezamashi","Bluetoothがサポートされてません。");
            message += " NG";
            finish();
        }else{
            //Bluetooth対応端末の場合の処理
            Log.d("mezamashi","Bluetoothがサポートされてます。");
            message += " OK";
        }

//        // ペアリング済みのデバイスを検索
//        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
//        Log.d("mezamashi", String.valueOf(pairedDevices.size()));
//        if (pairedDevices.size() > 1) {
//            for (BluetoothDevice device : pairedDevices) {
//                Log.d("mezamashi",device.getName());
//                Log.d("mezamashi",device.getAddress());
//            }
//        }

        // インテントフィルタの作成
        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        // ブロードキャストレシーバの登録
        registerReceiver(mReceiver, filter);

        Log.d("mezamashi","a");

        // BluetoothAdapterのインスタンス取得
        Log.d("mezamashi","b");
        // Bluetooth有効
        if (!bluetoothAdapter.isEnabled()) {
            Log.d("mezamashi","c");
            bluetoothAdapter.enable();
            Log.d("mezamashi","d");
        }
        // 周辺デバイスの検索開始
        Log.d("mezamashi","e");
        bluetoothAdapter.startDiscovery();
        Log.d("mezamashi","f");
        Log.d("mezamashi","g");

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);
    }
}
