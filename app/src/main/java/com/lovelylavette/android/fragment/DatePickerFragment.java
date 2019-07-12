package com.lovelylavette.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lovelylavette.android.R;

public class DatePickerFragment extends Fragment {
    private static final String TAG = "DatePickerFragment";


    public DatePickerFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_date_picker, container, false);
    }
}
