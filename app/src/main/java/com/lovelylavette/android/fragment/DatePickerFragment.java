package com.lovelylavette.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.lovelylavette.android.R;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.squareup.timessquare.CalendarPickerView.SelectionMode.RANGE;
import static com.squareup.timessquare.CalendarPickerView.SelectionMode.SINGLE;

public class DatePickerFragment extends Fragment {
    private static final String TAG = "DatePickerFragment";
    private Calendar today = Calendar.getInstance();
    private Calendar nextYear = Calendar.getInstance();
    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();

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
        nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        nextYear.add(Calendar.MONTH, -1);
        nextYear.add(Calendar.DAY_OF_MONTH, -4);

        calendarInit(RANGE);
    }

    private void calendarInit(CalendarPickerView.SelectionMode mode) {
        calendarPicker.init(today.getTime(), nextYear.getTime())
                .inMode(mode);
    }

    private void setCalendarListeners() {

        calendarPicker.setOnInvalidDateSelectedListener(date -> {
//            Prevents invalid date toast
        });

        calendarPicker.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                List<Date> selectedDates = calendarPicker.getSelectedDates();
                startDate.setTime(selectedDates.get(0));
                endDate.setTime(selectedDates.get(selectedDates.size() - 1));
                Log.i(TAG, "Start Date: " + (startDate.get(Calendar.MONTH) + 1) + " - " + startDate.get(Calendar.DAY_OF_MONTH));
                Log.i(TAG, "End Date: " + (endDate.get(Calendar.MONTH) + 1) + " - " + endDate.get(Calendar.DAY_OF_MONTH));
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
                    calendarPicker.selectDate(startDate.getTime());
                    calendarPicker.selectDate(endDate.getTime());
                    break;
                case R.id.one_way_radio:
                    calendarInit(SINGLE);
                    calendarPicker.selectDate(startDate.getTime());
                    break;
            }
        });
    }

}
