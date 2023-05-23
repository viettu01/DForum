package com.tuplv.dforum.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tuplv.dforum.R;
public class SearchFragment extends Fragment {

    EditText edtSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        
        init(view);

        return view;
    }

    private void init(View view) {
        edtSearch = view.findViewById(R.id.edtSearch);
    }
}