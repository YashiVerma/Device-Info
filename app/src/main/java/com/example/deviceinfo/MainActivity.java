package com.example.deviceinfo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MainActivity extends AppCompatActivity {


    private static final int PERMISSIONS_REQUEST_CODE = 0;


    TextView Imsi;
    TextView Imei;
    TextView Cellular;
    TextView Wifi, BtMac;


    private BatteryReceiver mBatteryReceiver = new BatteryReceiver();
    private IntentFilter mIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button BtnSysInfo = (Button) findViewById(R.id.getSystemInfo);
        Button BtnExportFile = (Button) findViewById(R.id.exportFile);
        Imei = (TextView) findViewById(R.id.imei);
        Imsi = (TextView) findViewById(R.id.imsi);
        final TextView WifiMac = (TextView) findViewById(R.id.wifiMac);
        BtMac = (TextView) findViewById(R.id.btMac);
        final TextView Version = (TextView) findViewById(R.id.version);
        final TextView AndroidName = (TextView) findViewById(R.id.name);
        final TextView Model = (TextView) findViewById(R.id.model);
        Wifi = (TextView) findViewById(R.id.wifi);
        Cellular = (TextView) findViewById(R.id.cellular);



        statusCheck();

        BtnSysInfo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {


                TelephonyManager mTelephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                loadInfo();
                getBluetoothMacAddress();
                getCellInfo();


                String lStRWifiMac = getWifiMacAddress();
              WifiMac.setText("WIFI-MAC  :  " + lStRWifiMac);



                Version.setText("Android Version  :  " + Build.VERSION.RELEASE);


                //android version  name
                String lStrVersionName = "";
                int androidVersion = Build.VERSION.SDK_INT;
                switch (androidVersion) {
                    case 14:
                        lStrVersionName = "15, Ice Cream Sandwich";
                        break;
                    case 15:
                        lStrVersionName = "15, Ice Cream Sandwich";
                        break;
                    case 16:
                        lStrVersionName = "16, Jelly Bean";
                        break;
                    case 17:
                        lStrVersionName = "17, Jelly Bean";
                        break;
                    case 18:
                        lStrVersionName = "18, Jelly Bean";
                        break;
                    case 19:
                        lStrVersionName = "19, KitKat";
                        break;
                    case 21:
                        lStrVersionName = "21, Lollipop";
                        break;
                    case 22:
                        lStrVersionName = "22, Lollipop";
                        break;
                    case 23:
                        lStrVersionName = "23, Marshmallow";
                        break;
                    case 24:
                        lStrVersionName = "24, Nougat";
                        break;
                    case 25:
                        lStrVersionName = "25, Nougat";
                        break;
                    case 26:
                        lStrVersionName = "26, Oreo";
                        break;
                    case 27:
                        lStrVersionName = "27, Oreo";
                        break;
                    case 28:
                        lStrVersionName = "28, Pie";
                        break;

                    case 29:
                        lStrVersionName = "29, Android 10";
                        break;
                    default:
                        lStrVersionName = "not found";
                        break;
                }
                AndroidName.setText("Android Name  :  " + lStrVersionName);

                Model.setText("Model  :  " + Build.MODEL);


            }
        });

        //export data to file
        BtnExportFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Requesting Permission to access External Storage
             /*   ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        EXTERNAL_STORAGE_PERMISSION_CODE);
          */
                String lStrFileData = Imei.getText().toString()+"\n"+Imsi.getText().toString()+"\n"+BtMac.getText().toString()+"\n"+
                        WifiMac.getText().toString()+"\n"+Version.getText().toString()+"\n"+AndroidName.getText()+"\n"+
                        Model.getText().toString()+"\n" +mBatteryReceiver.getPercentage()+"\n"+mBatteryReceiver.status()+"\n"
                        +Wifi.getText().toString()+"\n"+Cellular.getText().toString();



                // Creating folder
                File folder = getExternalFilesDir("DeviceInfo");

                // Creating file with name Info.txt
                File file = new File(folder, "Info.txt");
                writeTextData(file, lStrFileData);



            }
        });
    }

    //write
    private void writeTextData(File file, String data) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data.getBytes());
            Toast.makeText(this, "Done" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // get Wifi Mac Address
    public String getWifiMacAddress() {
        try {
            List<NetworkInterface> networkInterfaceList = Collections.list(NetworkInterface.getNetworkInterfaces());

            String stringMac = "";

            for (NetworkInterface networkInterface : networkInterfaceList) {
                if (networkInterface.getName().equalsIgnoreCase("wlan0")) {
                    for (int i = 0; i < networkInterface.getHardwareAddress().length; i++) {
                        String stringMacByte = Integer.toHexString(networkInterface.getHardwareAddress()[i] & 0xFF);

                        if (stringMacByte.length() == 1) {
                            stringMacByte = "0" + stringMacByte;
                        }

                        stringMac = stringMac + stringMacByte.toUpperCase() + ":";
                    }
                    break;
                }

            }
            return stringMac;
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return "0";
    }



    //Bt Mac
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void getBluetoothMacAddress() {
       try{ BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String m_bluetoothAdd = m_BluetoothAdapter.getAddress();
        BtMac.setText("BT-MAC  :"+m_bluetoothAdd);}
       catch(Exception e){
            String macAddress = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(), "bluetooth_address");
            BtMac.setText("BT-MAC  :"+macAddress);
        }

    }


    //wifi strength
    public static int getWifiStrengthPercentage(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            int rssi = wifiManager.getConnectionInfo().getRssi();
            int level = WifiManager.calculateSignalLevel(rssi, 5);

            return level;
        } catch (Exception e) {
            return 0;
        }
    }


    //connected wifi
    public static String getCurrentSsid(Context context) {
        String ssid = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
        }
        return ssid;
    }


    //IMEI  &&  IMSI
    public void loadInfo() {


        // Check if the permissions are already available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //  permissions has not been granted.
            requestPermissions();
        } else {
            //  permissions are already been granted.
            doPermissionGrantedStuffs();
        }
    }


    /**
     * Requests the permissions.
     * If the permissions have been denied previously, a dialog will prompt the user to grant the
     * permissions, otherwise it is requested directly.
     */
    private void requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE) &&
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        ACCESS_COARSE_LOCATION) &&
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        ACCESS_FINE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Permission Request")
                    .setMessage(getString(R.string.permissions_rationale))
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //re-request
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_PHONE_STATE, ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION},
                                    PERMISSIONS_REQUEST_CODE);
                        }
                    })

                    .show();
        } else {
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, ACCESS_COARSE_LOCATION
                            , ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            // Received permission result for READ_PHONE_STATE permission.est.");
            // Check if the only required permission has been granted
            if (grantResults.length == 4 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED
            ) {
                //  permissions have been granted, proceed with displaying IMEI Number and other stuff
                //alertAlert(getString(R.string.permisions_available));
                doPermissionGrantedStuffs();
            } else {
                alertAlert(getString(R.string.permissions_not_granted));
            }
        }
    }

    private void alertAlert(String msg) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do somthing here
                    }
                })

                .show();
    }


    public void doPermissionGrantedStuffs() {
        Wifi.setText("Wifi Name   : " + getCurrentSsid(getApplicationContext()) + "\n"
                + "Wifi Strength(Out of 5): " + getWifiStrengthPercentage(getApplicationContext()));


        if (android.os.Build.VERSION.SDK_INT >= 29) {
            String lStrImei = Settings.Secure.getString(
                    getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            Imei.setText("IMEI   :" + lStrImei);

            Imsi.setText("IMSI  :  Not Accessible");
            return;
        }


        //Have an  object of TelephonyManager
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Get IMEI Number of Phone
        String IMEINumber = tm.getDeviceId();
        //Get Subscriber ID
        String subscriberID = tm.getSubscriberId();
        Imsi.setText("IMSI  :  " + subscriberID);

        //Get SIM Serial Number
        // String SIMSerialNumber=tm.getSimSerialNumber();

        Imei.setText("IMEI:   " + IMEINumber);
    }



    //Cell Operator and Strength
    public void getCellInfo() {
        // cellular strength

        // Get System TELEPHONY service reference

        TelephonyManager telephonyManager = (TelephonyManager) getBaseContext()
                .getSystemService(Context.TELEPHONY_SERVICE);

        //Cellular Operator and Strength
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String CellularStrength = "PLEASE TURN ON LOCATION";
       if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{ACCESS_COARSE_LOCATION},
                        1000);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();   //This will give info of all sims present inside your mobile
                if (cellInfos != null) {
                    for (int i = 0; i < cellInfos.size(); i++) {
                        if (cellInfos.get(i).isRegistered()) {


                            if (cellInfos.get(i) instanceof CellInfoWcdma) {
                                CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) telephonyManager.getAllCellInfo().get(0);
                                CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                                CellularStrength = String.valueOf(cellSignalStrengthWcdma.getDbm());
                            } else if (cellInfos.get(i) instanceof CellInfoCdma) {
                                CellInfoCdma cellInfoCdma = (CellInfoCdma) telephonyManager.getAllCellInfo().get(0);
                                CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
                                CellularStrength = String.valueOf(cellSignalStrengthCdma.getDbm());
                            } else if (cellInfos.get(i) instanceof CellInfoGsm) {
                                CellInfoGsm cellInfogsm = (CellInfoGsm) telephonyManager.getAllCellInfo().get(0);
                                CellSignalStrengthGsm cellSignalStrengthGsm = cellInfogsm.getCellSignalStrength();
                                CellularStrength = String.valueOf(cellSignalStrengthGsm.getDbm());
                            } else if (cellInfos.get(i) instanceof CellInfoLte) {
                                CellInfoLte cellInfoLte = (CellInfoLte) telephonyManager.getAllCellInfo().get(0);
                                CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                                CellularStrength = String.valueOf(cellSignalStrengthLte.getDbm());

                            }
                            CellularStrength += " Dbm";
                        }
                    }
                }
            }

            }

        Cellular.setText("Cellular Operator  : " + telephonyManager.getNetworkOperatorName() +
                "\n" + "Cellular Strength : " + CellularStrength);

    }


    //battery
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBatteryReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mBatteryReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


   public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }

    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, please enable it")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}






