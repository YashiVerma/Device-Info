package com.example.deviceinfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.BatteryManager;
import android.widget.ImageView;
import android.widget.TextView;
public class BatteryReceiver extends BroadcastReceiver{
    TextView statusLabel;
    TextView percentageLabel;
    ImageView batteryImage;
 String message;
 int percentage;
    @Override
    public void onReceive(Context context, Intent intent) {

         statusLabel = ((MainActivity)context).findViewById(R.id.statusLabel);
         percentageLabel = ((MainActivity)context).findViewById(R.id.percentageLabel);
         batteryImage = ((MainActivity)context).findViewById(R.id.batteryImage);
        String action = intent.getAction();

        if (action != null && action.equals(Intent.ACTION_BATTERY_CHANGED)) {


        // Status
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
          message = "";

            switch (status) {
                case BatteryManager.BATTERY_STATUS_FULL:
                    message = "Full";
                    break;
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    message = "Charging";
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    message = "Discharging";
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    message = "Not charging";
                    break;
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    message = "Unknown";
                    break;
            }
           statusLabel.setText(message);


           // Percentage
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
             percentage = level * 100 / scale;
           percentageLabel.setText(percentage + "%");


          // Image
            Resources res = context.getResources();

            if (percentage >= 90) {
                batteryImage.setImageDrawable(res.getDrawable(R.drawable.b100));

            } else if (90 > percentage && percentage >= 80) {
                batteryImage.setImageDrawable(res.getDrawable(R.drawable.b75));

            }
            else if (80> percentage && percentage >=50) {
                batteryImage.setImageDrawable(res.getDrawable(R.drawable.ic_middlebattery));
            }
            else if (50 > percentage && percentage > 15) {
                batteryImage.setImageDrawable(res.getDrawable(R.drawable.ic_lowbattery));
            } else {
                batteryImage.setImageDrawable(res.getDrawable(R.drawable.b0));

            }

        }
    }
    public String status(){
        return message;
    }
    public int getPercentage(){
        return  percentage;
    }

}


