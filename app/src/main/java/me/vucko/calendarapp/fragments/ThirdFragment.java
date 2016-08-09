package me.vucko.calendarapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import me.vucko.calendarapp.ContactUsActivity;
import me.vucko.calendarapp.R;
import me.vucko.calendarapp.SyncCalendarsActivity;


public class ThirdFragment extends Fragment {

    private Button notificationSettingsButton, calendarSyncButton, contactUsButton, helpButton;

    public static ThirdFragment newInstance() {
        ThirdFragment fragment = new ThirdFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third, container, false);
        notificationSettingsButton = (Button) view.findViewById(R.id.notificationSettingsButton);
        calendarSyncButton = (Button) view.findViewById(R.id.calendarSyncButton);
        contactUsButton = (Button) view.findViewById(R.id.contactUsButton);
        helpButton = (Button) view.findViewById(R.id.helpButton);
        setOnClickListeners();
        return view;
    }

    private void setOnClickListeners(){
        contactUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ContactUsActivity.class);
                startActivity(intent);
            }
        });
        calendarSyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SyncCalendarsActivity.class);
                startActivity(intent);
            }
        });
    }

}
