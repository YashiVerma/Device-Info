package com.example.deviceinfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;



public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {


    private static final int PERMISSIONS_REQUEST_CODE = 0;
    private static final int WRITE_PERMISSION_REQUEST_CODE = 100;


    TextView Imsi;
    TextView Imei;
    TextView Cellular;
    TextView Wifi, BtMac;


    private BatteryReceiver mBatteryReceiver = new BatteryReceiver();
    private IntentFilter mIntentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        loadInfo();
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            getCellInfo();
        else
            EnableGPSAutoMatically();

        getBluetoothMacAddress();



                String lStRWifiMac = getWifiMacAddress();
              WifiMac.setText("WIFI-MAC  :  " + lStRWifiMac);



                Version.setText("Android Version  :  " + Build.VERSION.RELEASE);


                //android version  name
                String lStrVersionName = "";
                int androidVersion = Build.VERSION.SDK_INT;
                switch (androidVersion) {
                    case 14:
                        lStrVersionName = "14, Ice Cream Sandwich";
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




        //export data to file
        BtnExportFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lStrFileData = Imei.getText().toString()+"\n"+Imsi.getText().toString()+"\n"+BtMac.getText().toString()+"\n"+
                        WifiMac.getText().toString()+"\n"+Version.getText().toString()+"\n"+AndroidName.getText()+"\n"+
                        Model.getText().toString()+"\n" +"Battery Status  :"+mBatteryReceiver.getPercentage()+"  "+mBatteryReceiver.status()+"\n"
                        +Wifi.getText().toString()+"\n"+Cellular.getText().toString();

                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    if(Build.VERSION.SDK_INT >= 29){
                        try {
                            ContentValues values = new ContentValues();

                            values.put(MediaStore.MediaColumns.DISPLAY_NAME, "DeviceInfo");       //file name
                            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");        //file extension, will automatically add to file
                            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/DeviceInfo");

                            Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);

                            OutputStream outputStream = getContentResolver().openOutputStream(uri);

                            outputStream.write(lStrFileData.getBytes());

                            outputStream.close();

                            Toast.makeText(v.getContext(), "File created successfully in downloads folder", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Toast.makeText(v.getContext(), "Failed to create file", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if (Build.VERSION.SDK_INT >= 23) {
                        if (checkWritePermission()) {
                            WritetoFile(lStrFileData);
                        } else {
                            requestWritePermission(); // Code for permission
                        }
                    } else {
                        WritetoFile(lStrFileData);
                    }
                }
            }
        });
    }

    //write
    public void WritetoFile(String data){
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File dir = new File(externalStorageDirectory.getAbsolutePath() + "/DeviceInfo/");
        dir.mkdir();
        File file = new File(dir, "DeviceInfo.txt");
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(data.getBytes());
            os.close();
            Toast.makeText(getApplicationContext(), "File created successfully in DeviceInfo folder", Toast.LENGTH_SHORT).show();
        } catch ( IOException e) {
            Toast.makeText(getApplicationContext(), "Failed to create file", Toast.LENGTH_SHORT).show();
        }
    }
    //check writing to external storage permission
    private boolean checkWritePermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    // request to get permission to write
    private void requestWritePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to create files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_REQUEST_CODE);
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
        String lStrBluetoothAdapter = m_BluetoothAdapter.getAddress();
        BtMac.setText("BT-MAC  :"+lStrBluetoothAdapter);}
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
            if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
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

    //Request to enable Location Services
    private void EnableGPSAutoMatically() {
        GoogleApiClient googleApiClient = null;
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true);
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            toast("GPS enabled");
                            //getting cell info after turning on location
                            getCellInfo();
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            toast("GPS is not on");
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(MainActivity.this, 1000);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            toast("Setting change not allowed");
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            //getting cell info after turning on location
                            getCellInfo();
                            break;
                    }

                }
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        //getting cell info after turning on location
        getCellInfo();
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        toast("Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        toast("Failed");
    }
    private void toast(String message) {
        try {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Log.i(message,"Window has been closed");
        }
    }

}






