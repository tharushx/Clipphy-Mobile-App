package com.app.clipphy.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.clipphy.Activity.MainActivity;
import com.app.clipphy.R;

public class IntroFragment extends Fragment {
    public IntroFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_intro, container, false);

        Button getStart_button = view.findViewById(R.id.getStart_button);
        getStart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).loadFragment(new WelcomeFragment());
            }
        });
        return view;
    }
}
