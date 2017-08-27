package com.seniorproject.uop.commuter;

import android.app.ActionBar;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.AlarmViewHolder> {

    static final String TAG = "RVAdapter";
    private List<Alarm> alarms;
    private OnAlarmInteractionListener mAlarmListener;

    public RVAdapter(Context context) {
        alarms = new ArrayList<>();
        Alarm alarm;

        alarm = new Alarm(context);
        alarm.setArrival(11, 30);
        alarm.setLabel("Senior Project");
        alarm.setPrepTime(50);
        alarm.setDur(10);
        alarms.add(alarm);

        alarm = new Alarm(context);
        alarm.setArrival(7, 15);
        alarm.setPrepTime(45);
        alarm.setLabel("Wake Up!");
        alarms.add(alarm);
    }

    public void setAlarmInteractionListener(OnAlarmInteractionListener listener) {
        mAlarmListener = listener;
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new AlarmViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AlarmViewHolder holder, final int position) {
        final Alarm alarm = alarms.get(position);
        holder.wakeTime.setText(alarm.getWake().getTime());
        holder.arrivalTime.setText(alarm.getArrival().getTime());
        holder.label.setText(alarm.getLabel());
        collapse(holder.mExpLayout);
        holder.expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Expand button pressed");
                if (holder.mExpLayout.getVisibility() != View.VISIBLE) {
                    expand(holder.mExpLayout);

                } else {

                    collapse(holder.mExpLayout);
                }
            }
        });

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlarmListener.onEditAlarm(alarm, position);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarms.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.i(TAG, "Alarm Set");
                    alarms.get(position).setAlarm();
                } else {
                    alarms.get(position).cancelAlarm();
                }
            }
        });

    }

    public void addAlarm(Alarm alarm) {
        if (alarm != null && !alarms.contains(alarm)) {
            alarms.add(alarm);
            notifyDataSetChanged();
        } else {
            Log.i(TAG, "Trying to add null alarm");
        }

    }

    public static void expand(final View v) {
        v.measure(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ActionBar.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public void setAlarmAt(Alarm alarm, int position) {
        //Check if the position is not out of bounds and the alarm is not null
        if(0 <= position && position < alarms.size() && alarm != null) {
            alarms.set(position, alarm);
            this.notifyDataSetChanged();
        } else {
            Log.i(TAG, "Setting alarm failed. Either the index it out of bounds or alarm is null exception");
        }
    }

    public interface OnAlarmInteractionListener {
        void onEditAlarm(Alarm alarm, int pos);
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {

        public static final String TAG = "AlarmViewHolder";
        public LinearLayout mExpLayout;
        public Button expandButton;
        public Button editButton;
        public Button deleteButton;
        public TextView arrivalTime;
        public TextView wakeTime;
        public TextView label;
        public Switch toggle;

        AlarmViewHolder(View itemView) {
            super(itemView);
            mExpLayout = (LinearLayout) itemView.findViewById(R.id.expandableLayout);
            toggle = (Switch) itemView.findViewById(R.id.alarmSetSwitch);
            expandButton = (Button) itemView.findViewById(R.id.expandButton);
            editButton = (Button) itemView.findViewById(R.id.editButton);
            deleteButton = (Button) itemView.findViewById(R.id.deleteButton);
            label = (TextView) itemView.findViewById(R.id.alarmLabel);
            arrivalTime = (TextView) itemView.findViewById(R.id.arrivalTime);
            wakeTime = (TextView) itemView.findViewById(R.id.wakeTime);
        }
    }

}