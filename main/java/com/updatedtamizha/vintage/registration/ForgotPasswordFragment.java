package com.updatedtamizha.vintage.registration;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.updatedtamizha.vintage.R;

import static com.updatedtamizha.vintage.registration.CreateAccountFragment.VALID_EMAIL_ADDRESS_REGEX;

public class ForgotPasswordFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }
    private EditText email;
    private Button resetBtn;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        firebaseAuth = FirebaseAuth.getInstance();


        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

           if (VALID_EMAIL_ADDRESS_REGEX.matcher(email.getText().toString()).find()) {
                    progressBar.setVisibility(View.VISIBLE);
                    resetBtn.setEnabled(false);
                    firebaseAuth.sendPasswordResetEmail(email.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progressBar.setVisibility(View.VISIBLE);
                                        Toast.makeText(getContext(), "Password reset email sent successfully", Toast.LENGTH_LONG).show();
                                        getActivity().onBackPressed();
                                    }else {
                                        String error = task.getException().getMessage();
                                        email.setError(error);
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                    resetBtn.setEnabled(true);
                                }
                            });

                }else{
                    email.setError("Please provide a valid email");
                }
           }
        });
    }
        private void init(View view){

            email = view.findViewById(R.id.Email);
            progressBar = view.findViewById(R.id.progressBar);
            resetBtn= view.findViewById(R.id.reset_btn);

        }
    }
