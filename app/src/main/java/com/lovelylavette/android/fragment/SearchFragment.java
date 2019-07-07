package com.lovelylavette.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.lovelylavette.android.R;
import com.lovelylavette.android.api.GooglePlacesApi;
import com.lovelylavette.android.model.Trip;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";
    private static final int AUTOCOMPLETE_SEARCH_LOCALE_REQUEST_CODE = 1;
    private Context context;
    private Trip trip;

    @BindView(R.id.root_view)
    LinearLayout rootView;
    @BindView(R.id.search_locale)
    TextView localeTextView;
    @BindView(R.id.search_budget)
    EditText budgetEditText;
    @BindView(R.id.next_locale)
    ImageView localeNext;
    @BindView(R.id.next_sight)
    ImageView sightNext;
    @BindView(R.id.next_budget)
    ImageView budgetNext;
    @BindView(R.id.next_any)
    ImageView anyNext;


    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);
        trip = new Trip();
        addRootViewFocusListener();
        localeTextView.setOnClickListener(v -> GooglePlacesApi.showAutocomplete(context, this, AUTOCOMPLETE_SEARCH_LOCALE_REQUEST_CODE));
        addBudgetChangeListener();
        addNextListeners();
        return view;
    }

    private void addRootViewFocusListener() {
        rootView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        });
    }

    private void addBudgetChangeListener() {
        budgetEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int budget = s.toString().matches("[0-9]+") ? Integer.parseInt(s.toString()) : 0;
                trip.setBudget(budget);

                if (budget > 0) {
                    budgetNext.setVisibility(View.VISIBLE);
                } else {
                    budgetNext.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void addNextListeners() {
        View.OnClickListener nextListener = v -> getFragmentManager().beginTransaction()
                .replace(R.id.frag_container, NeedsFragment.newInstance(trip))
                .addToBackStack(null).commit();

        localeNext.setOnClickListener(nextListener);
        sightNext.setOnClickListener(nextListener);
        budgetNext.setOnClickListener(nextListener);
        anyNext.setOnClickListener(nextListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_SEARCH_LOCALE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place destination = Autocomplete.getPlaceFromIntent(data);
                trip.setDestination(destination);
                Log.i(TAG, "Selected Place: " + destination.toString());
                SpannableString city = getUnderlinedText(destination.getName());
                localeTextView.setText(city);
                localeNext.setVisibility(View.VISIBLE);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            }
        }
    }

    private SpannableString getUnderlinedText(String text) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), 0);
        return spannableString;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
