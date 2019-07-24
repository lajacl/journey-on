package com.lovelylavette.android.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lovelylavette.android.R;
import com.lovelylavette.android.model.User;
import com.lovelylavette.android.util.AppPrefs;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private Context context;
    private AppPrefs prefs;
    private User user;

    @BindView(R.id.user_name)
    EditText userName;
    @BindView(R.id.user_origin)
    TextView userOrigin;
    @BindView(R.id.user_currency)
    TextView userCurrency;
    @BindView(R.id.user_wishlist)
    EditText userWishlist;
    @BindView(R.id.save_btn)
    Button saveUser;


    public ProfileFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        displaySavedInfo();
        addNameChangeListener();
        addWishlistChangeListener();
        setSaveOnClickListener();
        return view;
    }

    private void displaySavedInfo() {
        prefs = new AppPrefs(context);
        user = prefs.getUser();
        if (user == null) {
            user = new User();
        } else {
            userName.setText(user.getName());
            userOrigin.setText(user.getOrigin());
            userCurrency.setText(user.getCurrency());
            userWishlist.setText(user.getWishlist());
        }
        Log.i(TAG, "User: " + user.toString());
    }

    private void addNameChangeListener() {
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                user.setName(s.toString());
                saveUser.setVisibility(View.VISIBLE);
            }
        });
    }

    private void addWishlistChangeListener() {
        userWishlist.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                user.setWishlist(s.toString());
                saveUser.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setSaveOnClickListener() {
        saveUser.setOnClickListener(v -> {
            prefs.saveUser(user);
            saveUser.setVisibility(View.GONE);
            Toast.makeText(context, R.string.profile_saved, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
