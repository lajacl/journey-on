package com.lovelylavette.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lovelylavette.android.R;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.squareup.timessquare.CalendarPickerView.SelectionMode.RANGE;

public class DatePickerFragment extends Fragment {
    private static final String TAG = "DatePickerFragment";

    @BindView(R.id.calendar_picker)
    CalendarPickerView calendarPicker;


    public DatePickerFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_picker, container, false);
        ButterKnife.bind(this, view);
        setupCalendarPicker();
        return view;
    }

    private void setupCalendarPicker() {
        Calendar today = Calendar.getInstance();
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        nextYear.add(Calendar.MONTH, -1);
        nextYear.add(Calendar.DAY_OF_MONTH, -4);

        calendarPicker.init(today.getTime(), nextYear.getTime())
                .inMode(RANGE);

        calendarPicker.setOnInvalidDateSelectedListener(date -> {
        });
    }
}
