package com.lovelylavette.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.lovelylavette.android.R;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.squareup.timessquare.CalendarPickerView.SelectionMode.RANGE;
import static com.squareup.timessquare.CalendarPickerView.SelectionMode.SINGLE;

public class DatePickerFragment extends Fragment {
    private static final String TAG = "DatePickerFragment";
    private Calendar startDate;
    private Calendar endDate;

    @BindView(R.id.calendar_picker)
    CalendarPickerView calendarPicker;
    @BindView(R.id.flight_type)
    RadioGroup flightType;


    public DatePickerFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_picker, container, false);
        ButterKnife.bind(this, view);
        setupCalendar();
        setCalendarListeners();
        setFlightTypeListener();
        return view;
    }

    private void setupCalendar() {
        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();
        endDate.add(Calendar.YEAR, 1);
        endDate.add(Calendar.MONTH, -1);
        endDate.add(Calendar.DAY_OF_MONTH, -4);

        calendarInit(RANGE);
    }

    private void calendarInit(CalendarPickerView.SelectionMode mode) {
        calendarPicker.init(startDate.getTime(), endDate.getTime())
                .inMode(mode);
    }

    private void setCalendarListeners() {

        calendarPicker.setOnInvalidDateSelectedListener(date -> {
//            Prevents invalid date toast
        });

        calendarPicker.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
            }

            @Override
            public void onDateUnselected(Date date) {
            }
        });
    }

    private void setFlightTypeListener() {
        flightType.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.round_trip_radio:
                    calendarInit(RANGE);
                    break;
                case R.id.one_way_radio:
                    calendarInit(SINGLE);
                    break;
            }
        });
    }
}
