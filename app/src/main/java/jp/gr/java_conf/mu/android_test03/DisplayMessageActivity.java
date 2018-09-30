package jp.gr.java_conf.mu.android_test03;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DisplayMessageActivity extends AppCompatActivity {

    BluetoothAdapter bluetoothAdapter;


    // ブロードキャストレシーバの定義
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("mezamashi",action);
            if (BluetoothDevice.ACTION_FOUND.equals(action) || BluetoothDevice.ACTION_NAME_CHANGED.equals(action)){
               // 見つけたデバイス情報の取得
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String mResult = "Device : " + device.getName() + "/" + device.getAddress() + "\n";
                Log.d("mezamashi",mResult);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);


        // テスト1 ↑
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
//
//        // 端末がそもそもBlueToothをサポートしているかの確認
//        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if(bluetoothAdapter == null){
//            //Bluetooth非対応端末の場合の処理
//            Log.d("mezamashi","Bluetoothがサポートされてません。");
//            message += " NG";
//            finish();
//        }else{
//            //Bluetooth対応端末の場合の処理
//            Log.d("mezamashi","Bluetoothがサポートされてます。");
//            message += " OK";
//        }
//
//        // インテントフィルタの作成
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(BluetoothDevice.ACTION_FOUND);
//        filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
//        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
//        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//
//        // ブロードキャストレシーバの登録
//        registerReceiver(mReceiver, filter);
//
//        // BluetoothAdapterのインスタンス取得
//        // Bluetooth有効
//        if (!bluetoothAdapter.isEnabled()) {
//            bluetoothAdapter.enable();
//        }
//        // 周辺デバイスの検索開始
//        bluetoothAdapter.startDiscovery();





        // BLE まわり
//        //BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
//        BluetoothManager bluetoothManager = Context.getSystemService(BluetoothManager.class);
//        // mBluetoothAdapterの取得
//        mBluetoothAdapter = bluetoothManager.getAdapter();

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // mBluetoothLeScannerの初期化
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        Log.d("mezamashi","scan呼び出し前");
        scan(null,null,true);
        Log.d("mezamashi","scan呼び出し後");




        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);
    }








    // ---BLE対応テスト??
    // BLEスキャンのタイムアウト時間
    private static final long SCAN_PERIOD = 20000;

    private ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private ScanCallback mScanCallback;
    private Handler mHandler = new Handler();




    // ScanCallbackの初期化
    private ScanCallback initCallbacks() {

        return new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                Log.d("mezamashi","onScanResult");
                super.onScanResult(callbackType, result);

                if (result != null && result.getDevice() != null) {
                    Log.d("mezamashi","デバイス検知: " + result.getDevice().getName() + "/" + result.getDevice().getAddress());
                    if (isAdded(result.getDevice())) {
                        // No add
                    } else {
                        saveDevice(result.getDevice());
                    }
                }

            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                Log.d("mezamashi","onBatchScanResults");
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(int errorCode) {
                Log.d("mezamashi","onScanFailed"+"("+errorCode + ")");
                super.onScanFailed(errorCode);
            }

        };

    }

    // スキャン実施
    public void scan(List<ScanFilter> filters, ScanSettings settings,
                     boolean enable) {
        Log.d("mezamashi","scan");
        mScanCallback = initCallbacks();


        if (enable) {
            Log.d("mezamashi","if enable直下");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    isScanning = false;
                    Log.d("mezamashi","run");
                    mBluetoothLeScanner.stopScan(mScanCallback);
                    Log.d("mezamashi","run2");

                    for(BluetoothDevice device : deviceList){
                        String deviceInfo = device.getName() + "/" + device.getAddress();
                        Log.d("mezamashi",deviceInfo);

                        if(device.getName() != null && device.getName().equals("GiC63372")){
                            connect(getApplicationContext(),device);
                        }
                    }

                }
            }, SCAN_PERIOD);

 //           isScanning = true;
            Log.d("mezamashi","startScan直前");
            mBluetoothLeScanner.startScan(mScanCallback);
            Log.d("mezamashi","startScan直後");
            // スキャンフィルタを設定するならこちら
            // mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
        } else {
 //           isScanning = false;
            mBluetoothLeScanner.stopScan(mScanCallback);
        }

    }

    // スキャン停止
    public void stopScan() {
        Log.d("mezamashi","stopScan");
        if (mBluetoothLeScanner != null) {
            mBluetoothLeScanner.stopScan(mScanCallback);
        }

    }

    // スキャンしたデバイスのリスト保存
    public void saveDevice(BluetoothDevice device) {
        Log.d("mezamashi","saveDevice");
        if (deviceList == null) {
            deviceList = new ArrayList<>();
        }

        deviceList.add(device);

    }

    // スキャンしたデバイスがリストに追加済みかどうかの確認
    public boolean isAdded(BluetoothDevice device) {
        Log.d("mezamashi","isAdded");
        if (deviceList != null && deviceList.size() > 0) {
            return deviceList.contains(device);
        } else {
            return false;
        }

    }

    private BluetoothGatt bluetoothGatt;

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            Log.d("mezamashi","onConnectionStateChange");
            super.onConnectionStateChange(gatt, status, newState);

            switch(newState){
                case BluetoothProfile.STATE_CONNECTED :
                    Log.d("mezamashi", "STATE_CONNECTED");
                    break;
                case BluetoothProfile.STATE_CONNECTING :
                    Log.d("mezamashi", "STATE_CONNECTING");
                    break;
                case BluetoothProfile.STATE_DISCONNECTED :
                    Log.d("mezamashi", "STATE_DISCONNECTED");
                    break;
                case BluetoothProfile.STATE_DISCONNECTING :
                    Log.d("mezamashi", "STATE_DISCONNECTING");
                    break;
                default :
                    Log.d("mezamashi", "other");
                    break;
            }

            // 接続成功し、サービス取得
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("mezamashi", "discoverService前");
                bluetoothGatt = gatt;
                discoverService();
                Log.d("mezamashi", "discoverService後");
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d("mezamashi","onServicesDiscovered");
            super.onServicesDiscovered(gatt, status);

            List<BluetoothGattService> serviceList = gatt.getServices();

            for (BluetoothGattService service : serviceList) {
               // サービス一覧を取得したり探したりする処理
               // あとキャラクタリスティクスを取得したり探したりしてもよい
               Log.d("mezamashi","---");
               Log.d("mezamashi","InstanceID: " + service.getInstanceId());
               Log.d("mezamashi","UUID: " + service.getUuid().toString());


                   List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                   for (BluetoothGattCharacteristic characteristic : characteristics) {
                       String characteristicUuid = characteristic.getUuid().toString();
                       Log.d("mezamashi", "charactaristic: " + characteristicUuid);

                       BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                       if(descriptor == null){
                           Log.d("mezamashi", "descriptor取得失敗");
                       }else{
                           Log.d("mezamashi", "descriptor取得成功");

                           boolean registered = gatt.setCharacteristicNotification(characteristic, true);
                           descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                           gatt.writeDescriptor(descriptor);
                       }

                   }
               }

//            Log.d("mezamashi", "a");
//            BluetoothGattService service = gatt.getService(UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb"));
//            Log.d("mezamashi", "b");
//            BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb"));
//            Log.d("mezamashi", "c");
//            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
//            Log.d("mezamashi", "d");
//            boolean registered = gatt.setCharacteristicNotification(characteristic, true);
//            Log.d("mezamashi", "e");
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            Log.d("mezamashi", "f");
//            gatt.writeDescriptor(descriptor);

            Log.d("mezamashi","onServicesDiscovered終わり");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {

            Log.d("mezamashi","onCharacteristicChanged");
            String uuid = characteristic.getUuid().toString();
            Log.d("mezamashi",uuid);
            Byte value = characteristic.getValue()[0];
            Log.d("mezamashi",value.toString());
        }
    };

    // Gattへの接続要求
    public void connect(Context context, BluetoothDevice device) {
        Log.d("mezamashi","connect");
        bluetoothGatt = device.connectGatt(context, false, mGattCallback);
        bluetoothGatt.connect();

    }

    // サービス取得要求
    public void discoverService() {
        Log.d("mezamashi","discoverService");
        if (bluetoothGatt != null) {
            bluetoothGatt.discoverServices();
        }

    }





}
