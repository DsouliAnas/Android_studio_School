package com.example.school_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AProposFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a_propos, container, false);
        TextView descriptionTextView = view.findViewById(R.id.textViewDescription);
        TextView developerTextView = view.findViewById(R.id.textViewDeveloper);
        TextView contactTextView = view.findViewById(R.id.textViewContact);

        descriptionTextView.setText("Welcome to the School Project App! This app helps you manage your courses and schedule.");
        developerTextView.setText("Developed by Mohamed Anas Dsouli");
        contactTextView.setText("For inquiries, contact us at: support@example.com");

        return view;
    }
}
