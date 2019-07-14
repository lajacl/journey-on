package com.lovelylavette.android.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import com.lovelylavette.android.R;
import com.lovelylavette.android.model.Trip;
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
    public static final String EXTRA_DATE = "com.lovelylavette.android.translatedtravel.date";
    private static final String ARG_TRIP = "trip";
    private Trip trip;
    private Calendar today;
    private Calendar nextYear;
    private Calendar startDate;
    private Calendar endDate;
    private int lastSelectionSize = 0;

    @BindView(R.id.calendar_picker)
    CalendarPickerView calendarPicker;
    @BindView(R.id.flight_type)
    RadioGroup flightType;
    @BindView(R.id.apply_btn)
    Button applyBtn;


    public DatePickerFragment() {
    }

    public static DatePickerFragment newInstance(Trip trip) {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRIP, trip);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            trip = (Trip) getArguments().getSerializable(ARG_TRIP);
            Log.i(TAG, trip.toString());
        } else {
            trip = new Trip();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_picker, container, false);
        ButterKnife.bind(this, view);
        setFlightTypeDefault();
        setupCalendar();
        setCalendarListeners();
        setFlightTypeListener();
        setApplyBtnListener();
        return view;
    }

    private void setFlightTypeDefault() {
        if (trip.isRoundTrip()) {
            flightType.check(R.id.round_trip_radio);
        } else {
            flightType.check(R.id.one_way_radio);
        }
    }

    private void setupCalendar() {
        today = Calendar.getInstance();
        nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        nextYear.add(Calendar.MONTH, -1);
        nextYear.add(Calendar.DAY_OF_MONTH, -4);
        startDate = trip.getDepartureDate() == null ? Calendar.getInstance() : trip.getDepartureDate();
        endDate = trip.getReturnDate() == null ? Calendar.getInstance() : trip.getReturnDate();

        if (trip.isRoundTrip()) {
            calendarInit(RANGE);
        } else {
            calendarInit(SINGLE);
        }
    }

    private void calendarInit(CalendarPickerView.SelectionMode mode) {
        calendarPicker.init(today.getTime(), nextYear.getTime())
                .inMode(mode);

        calendarPicker.selectDate(startDate.getTime());
        if (trip.isRoundTrip()) {
            calendarPicker.selectDate(endDate.getTime());
        }
    }

    private void setCalendarListeners() {
//            Prevents invalid date selection toast message
        calendarPicker.setOnInvalidDateSelectedListener(null);

        calendarPicker.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                List<Date> selectedDates = calendarPicker.getSelectedDates();

                if (!trip.isRoundTrip() || selectedDates.size() > 1 ||
                        ((selectedDates.size() == 1 && lastSelectionSize == 1 &&
                                date.equals(startDate.getTime()) && date.equals(endDate.getTime())))) {
                    updateApplyBtnState(true);
                } else {
                    updateApplyBtnState(false);
                }

                startDate.setTime(selectedDates.get(0));
                endDate.setTime(selectedDates.get(selectedDates.size() - 1));
                lastSelectionSize = selectedDates.size();
            }

            @Override
            public void onDateUnselected(Date date) {
            }
        });
    }

    private void updateApplyBtnState(boolean toEnable) {
        applyBtn.setEnabled(toEnable);
    }

    private void setFlightTypeListener() {
        flightType.setOnCheckedChangeListener((group, checkedId) -> {
            updateApplyBtnState(true);

            switch (checkedId) {
                case R.id.round_trip_radio:
                    trip.setRoundTrip(true);
                    calendarInit(RANGE);
                    break;
                case R.id.one_way_radio:
                    trip.setRoundTrip(false);
                    calendarInit(SINGLE);
                    break;
            }
        });
    }

    private void setApplyBtnListener() {
        applyBtn.setOnClickListener(v -> {
            if (getTargetFragment() == null) {
                return;
            }

            trip.setDepartureDate(startDate);
            trip.setReturnDate(endDate);

            Intent intent = new Intent();
            intent.putExtra(EXTRA_DATE, trip);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            getFragmentManager().popBackStack();

            if (trip.isRoundTrip()) {
                Log.i(TAG, String.format("%s/%s - %s/%s", startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH),
                        endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH)));
            } else {
                Log.i(TAG, String.format("%s/%s", startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH)));
            }
        });
    }

}
