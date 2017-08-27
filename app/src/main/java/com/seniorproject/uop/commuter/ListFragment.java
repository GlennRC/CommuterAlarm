package com.seniorproject.uop.commuter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.MissingFormatArgumentException;


public class ListFragment extends Fragment {

    private static String TAG = ListFragment.class.getSimpleName();

    EditFragment mEditFragment;
    RVAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_fragment, container, false);
        final MainActivity ma = ((MainActivity) getActivity());

        //This is the edit button
        mAdapter = ((MainActivity) getActivity()).getAdapter();
        mAdapter.setAlarmInteractionListener(new RVAdapter.OnAlarmInteractionListener() {
            @Override
            public void onEditAlarm(Alarm alarm, int pos) {
                Log.i(this.getClass().toString(), String.format("Alarm: %d was pressed", pos));
                ma.setPosition(pos);
                ma.setAlarm(alarm);
                showEditFragment();
            }
        });
        RecyclerView rv = (RecyclerView)v.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(mAdapter);

        mEditFragment = new EditFragment();

        //This is the add button
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ma.setAlarm(new Alarm(getContext()));
                showEditFragment();
            }
        });

        return v;
    }

    public void doneEditingExisting(Alarm a, int pos) {
        Log.i(TAG, "done editing");
        mAdapter.setAlarmAt(a, pos);
    }

    public void doneEditingNewAlarm(Alarm a) {
        mAdapter.addAlarm(a);
    }

    private void showEditFragment() {
        FragmentTransaction ft = getActivity()
                .getSupportFragmentManager()
                .beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.container, mEditFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "Setting toolbar");
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Commuter Alarm Clock");
    }
}
