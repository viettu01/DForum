package com.tuplv.dforum.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.tuplv.dforum.R;

public class AddPostsFragment extends Fragment {

    Spinner spnCategory, spnType;
    EditText edtTitlePost, edtContentPost;
    Button btnAddPost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_posts, container, false);

        init(view);

        return view;
    }

    private void init(View view) {
        spnCategory = view.findViewById(R.id.spnCategory);
        spnType = view.findViewById(R.id.spnType);
        edtTitlePost = view.findViewById(R.id.edtTitlePost);
        edtContentPost = view.findViewById(R.id.edtContentPost);
        btnAddPost = view.findViewById(R.id.btnAddPost);
    }
}