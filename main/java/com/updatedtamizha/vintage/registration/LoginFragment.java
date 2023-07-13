package com.updatedtamizha.vintage.registration;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.updatedtamizha.vintage.MainActivity;
import com.updatedtamizha.vintage.R;

import java.util.List;

import static com.updatedtamizha.vintage.registration.CreateAccountFragment.VALID_EMAIL_ADDRESS_REGEX;


public class LoginFragment extends Fragment {


    private EditText emailORphone,password;
    private Button loginBtn;
    private ProgressBar progressBar;
    private TextView createAccountTV,forgotPassword;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                if (emailORphone.getText().toString().isEmpty()) {
                    emailORphone.setError("Required");
                    return;
                }
                if (password.getText().toString().isEmpty()) {
                    password.setError("Required");
                    return;
                }

                if (VALID_EMAIL_ADDRESS_REGEX.matcher(emailORphone.getText().toString()).find()) {
                    progressBar.setVisibility(View.VISIBLE);
                    login(emailORphone.getText().toString());
                }else if (emailORphone.getText().toString().matches("\\d{10}")){
                    FirebaseFirestore.getInstance().collection("users").whereEqualTo("phone",emailORphone.getText().toString())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<DocumentSnapshot> document = task.getResult().getDocuments();
                                if (document.isEmpty()) {
                                    emailORphone.setError("phone not found");
                                    progressBar.setVisibility(View.INVISIBLE);
                                    return;

                                }else {
                                    String email = document.get(0).get("email").toString();
                                    login(email);
                                }
                            }else{
                                String error = task.getException().getMessage();
                                Toast.makeText(getContext(),error , Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }else {
                    emailORphone.setError("Please enter a valid Email or Phone");
                }

            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((RegisterActivity)getActivity()).setFragment(new ForgotPasswordFragment());
            }
        });


        createAccountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((RegisterActivity)getActivity()).setFragment(new CreateAccountFragment());
            }
        });

    }

    private void init(View view){
        emailORphone = view.findViewById(R.id.email_or_phone);
        password = view.findViewById(R.id.password);
        loginBtn = view.findViewById(R.id.login_btn);
        progressBar = view.findViewById(R.id.progressBar);
        createAccountTV = view.findViewById(R.id.create_account_text);
        forgotPassword = view.findViewById(R.id.forgot_password);


    }
    private void login(String email){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email,password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent mainIntent = new Intent(getContext(), MainActivity.class);
                    startActivity(mainIntent);
                    getActivity().finish();
                }else {
                    String error = task.getException().getMessage();
                    Toast.makeText(getContext(),error , Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}