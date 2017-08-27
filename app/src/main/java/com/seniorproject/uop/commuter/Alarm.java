package com.seniorproject.uop.commuter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Alarm implements Serializable {

    private static String TAG = Alarm.class.getSimpleName();

    //Hour will be set in 24 hour time
    private Time wake, arrival;
    private String label;
    private int prepTime;
    private String origin;
    private String dest;

    private int dur;
    private PendingIntent alarmIntent;
    private Context mContext;
    private AlarmManager alarmMgr;

    Alarm(Context context) {
        wake = new Time();
        arrival = new Time();
        arrival.set(9, 0);
        label = "New Alarm";
        origin = "Edit starting address";
        dest = "Edit ending address";
        prepTime = 30;
        mContext = context;
        dur = 0;
    }

    public void setDur(int dur) {
        this.dur = dur/60;
    }

    public int getDur() { return dur; }

    public int getPrepTime() { return prepTime; }

    public void setPrepTime(int prepTime) { this.prepTime = prepTime; }

    public void calculateWakeTime() {
        // dur is in seconds
        Log.i(TAG, "Calulating time");
        Log.i(TAG, String .format("Duration: %d", dur));

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, arrival.hour);
        c.set(Calendar.MINUTE, arrival.min);

        long arrivalMill = c.getTimeInMillis();
        long durMill = TimeUnit.MINUTES.toMillis(this.dur);
        long prepMill = TimeUnit.MINUTES.toMillis(prepTime);
        long totalMill = durMill + prepMill;
        long newWake = arrivalMill - totalMill;

        c.setTimeInMillis(newWake);
        wake.set(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        Log.i(TAG, "Calender time from calender");
        Log.i(TAG, new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS").format(c.getTime()));
        Log.i(TAG, String.format("New wake time: %s", wake.getTime()));
    }

    public void setAlarm() {
        if (mContext != null) {
            calculateWakeTime();

            Calendar now = Calendar.getInstance();
            Calendar alarm = Calendar.getInstance();

            alarm.set(Calendar.HOUR_OF_DAY, wake.hour);
            alarm.set(Calendar.MINUTE, wake.min);
            long alarmMillis = alarm.getTimeInMillis();

            if (alarm.before(now)) alarmMillis+= 86400000L;  //Add 1 day if time selected before now
            alarm.setTimeInMillis(alarmMillis);

            Log.i(TAG, "Setting alarm with:");
            String text = String.format("Starting alarm at: %s",
                    new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS").format(alarm.getTime()));
            Log.i(TAG, text);

            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(mContext, text, duration);
            toast.show();

            // Starts the alarm
            /*
            alarmMgr = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(mContext, MainActivity.class);
            alarmIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
            alarmMgr.set(AlarmManager.RTC_WAKEUP, alarm.getTimeInMillis(), alarmIntent);
            */
        }
    }

    public void cancelAlarm() {
        if(alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
        }
        Log.i(Alarm.class.getSimpleName(), "Canceled");
    }

    public Time getWake() {
        calculateWakeTime();
        return wake;
    }

    // Arrival time
    public void setArrival(int hour, int min) {
        arrival.set(hour, min);
    }
    public Time getArrival() {
        return arrival;
    }

    // Origin
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    public void setDest(String destination) {
        this.dest = destination;
    }

    // Destination
    public String getOrigin() {
        return origin;
    }
    public String getDest() {
        return dest;
    }

    // Label
    public void setLabel(String label) {
        this.label = label;
    }
    public String getLabel() {
        return label;
    }

    public class Time {
        public int hour = 0;
        public int min = 0;

        Time() {}

        public void set(int hour, int min) {
            this.hour = hour;
            this.min = min;
            Log.i("Alarm", String.format("%d:%d", hour, min));
        }

        @Override
        public String toString() {
            return String.format("%s %s", getTime(), getAmPm());
        }

        public String getTime() {
            if (hour == 0) { hour = 12; }
            if (hour < 13) {
                return String.format("%d:%02d", hour, min);
            } else {
                return String.format("%d:%02d", hour-12, min);
            }
        }

        public String getAmPm() {
            return (hour > 12) ? "pm" : "am";
        }
    }
}

