package com.seniorproject.uop.commuter;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getSimpleName();
    private int position;
    private boolean isPositionSet;
    private RVAdapter mAdapter;
    private Alarm alarm;
    private ListFragment mListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new RVAdapter(this);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mListFragment = new ListFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.add(R.id.container, mListFragment);
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Commuter Alarm Clock");
    }

    public void doneEditing() {
        if (isPositionSet) {
            mListFragment.doneEditingExisting(alarm, position);
        } else {
            mListFragment.doneEditingNewAlarm(alarm);
        }
        isPositionSet = false;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }

    public Alarm getAlarm() {
        return alarm;
    }

    public RVAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new RVAdapter(this);
        }
        return mAdapter;
    }

    public void setPosition(int position) {
        this.position = position;
        isPositionSet = true;
    }

}
