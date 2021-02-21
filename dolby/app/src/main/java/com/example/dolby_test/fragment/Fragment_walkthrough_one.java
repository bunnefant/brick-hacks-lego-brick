package com.example.dolby_test.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dolby_test.R;
import com.example.dolby_test.record;

public class Fragment_walkthrough_one extends Fragment{
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_walkthrough_one, container, false);
        Button next = (Button) v.findViewById(R.id.btn);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make your toast here
                Intent intent = new Intent(getActivity(), record.class);
                startActivity(intent);
            }
        });
        return inflater.inflate(R.layout.fragment_walkthrough_one, container, false);
    }
}