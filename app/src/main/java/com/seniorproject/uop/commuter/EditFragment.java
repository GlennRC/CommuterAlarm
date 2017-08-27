package com.seniorproject.uop.commuter;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class EditFragment extends Fragment {

    private static String TAG = EditFragment.class.getSimpleName();
    Alarm mAlarm;
    View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.edit_fragment, container, false);

        //Set Alarm label
        TextView label = (TextView) v.findViewById(R.id.label);
        label.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                mAlarm.setLabel(v.getText().toString());
                return false;
            }
        });

        //User pressed Done
        Button doneEditing = (Button) v.findViewById(R.id.done_action);
        doneEditing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Done Editing");
                MainActivity ma = (MainActivity) getActivity();
                ma.doneEditing();
                getFragmentManager().popBackStack();
            }
        });

        //User Canceled
        Button cancelEditing = (Button) v.findViewById(R.id.cancel_action);
        cancelEditing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Cancel");
                getFragmentManager().popBackStack();
            }
        });

        //Set Arrival Time
        final TextView arrivalTime = (TextView) v.findViewById(R.id.arrivalTime);
        RelativeLayout arrivalLayout = (RelativeLayout) v.findViewById(R.id.arrival_layout);
        arrivalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TimePickerDialog mTimePicker = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.HOUR_OF_DAY, selectedHour);
                                c.set(Calendar.MINUTE, selectedMinute);
                                mAlarm.setArrival(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
                                arrivalTime.setText(mAlarm.getArrival().toString());
                                reCalcWakeTime();
                            }
                        }, mAlarm.getArrival().hour, mAlarm.getArrival().min, false);
                mTimePicker.show();
            }
        });

        //Set Prep Time
        final EditText prepTime = (EditText) v.findViewById(R.id.prepTime);
        RelativeLayout prepLayout = (RelativeLayout) v.findViewById(R.id.prep_layout);
        prepLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepTime.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity()
                        .getSystemService(getContext().INPUT_METHOD_SERVICE);
                imm.showSoftInput(prepTime, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        prepTime.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                mAlarm.setPrepTime(Integer.valueOf(v.getText().toString()));
                reCalcWakeTime();
                return false;
            }
        });

        //Show the map
        RelativeLayout showMap = (RelativeLayout) v.findViewById(R.id.address_layout);
        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMap();
            }
        });

        return v;
    }

    private void showMap() {
        ((MainActivity) getActivity()).setAlarm(mAlarm);
        FragmentTransaction ft = getActivity()
                .getSupportFragmentManager()
                .beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.container, new EditMapFragment());
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public  void onResume() {
        super.onResume();
        Log.i(TAG, "Resuming");

        Log.i(TAG, "Setting toolbar");
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Alarm");

        MainActivity ma = (MainActivity) getActivity();
        mAlarm = ma.getAlarm();

        if (mAlarm != null) {
            TextView label = (TextView) v.findViewById(R.id.label);
            if (label != null) { label.setText(mAlarm.getLabel()); }

            TextView arrivalTime = (TextView) v.findViewById(R.id.arrivalTime);
            if (arrivalTime != null) { arrivalTime.setText(mAlarm.getArrival().toString()); }

            TextView origin = (TextView) v.findViewById(R.id.origin);
            if (origin != null) { origin.setText(mAlarm.getOrigin()); }

            TextView destination = (TextView) v.findViewById(R.id.destination);
            if (destination != null) { destination.setText(mAlarm.getDest()); }

            TextView prepTime = (TextView) v.findViewById(R.id.prepTime);
            if (prepTime != null) { prepTime.setText(String.valueOf(mAlarm.getPrepTime())); }

            TextView duration = (TextView) v.findViewById(R.id.duration);
            if (duration != null) { duration.setText(String.format("%d min", mAlarm.getDur())); }

            reCalcWakeTime();
        }
    }

    private void reCalcWakeTime() {
        TextView wakeTime = (TextView) v.findViewById(R.id.calc_wake);
        if(wakeTime != null) { wakeTime.setText(mAlarm.getWake().toString()); }
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).setAlarm(mAlarm);
    }
}
