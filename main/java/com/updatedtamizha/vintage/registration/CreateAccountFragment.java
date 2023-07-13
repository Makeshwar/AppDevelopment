package com.updatedtamizha.vintage.registration;

import android.content.DialogInterface;
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
import com.google.firebase.auth.SignInMethodQueryResult;
import com.updatedtamizha.vintage.R;

import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class CreateAccountFragment extends Fragment {
    private EditText email,phone,password,confirmPassword;
    private Button createAccountBtn;
    private ProgressBar progressBar;
    private TextView loginTV;
    private FirebaseAuth firebaseAuth;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateAccountFragment() {
        // Required empty public constructor
    }
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);




    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateAccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateAccountFragment newInstance(String param1, String param2) {
        CreateAccountFragment fragment = new CreateAccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_account, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstancesState) {
        super.onViewCreated(view, savedInstancesState);

        init(view);
        firebaseAuth = FirebaseAuth.getInstance();

        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((RegisterActivity)getActivity()).setFragment(new LoginFragment());
            }
        });
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email.setError(null);
                if (email.getText().toString().isEmpty()){
                    email.setError("Required");
                    return;
                } if (phone.getText().toString().isEmpty()){
                    phone.setError("Required");
                    return;
                }
                if (password.getText().toString().isEmpty()){
                    password.setError("Required");
                    return;
                } if (confirmPassword.getText().toString().isEmpty()){
                    confirmPassword.setError("Required");
                    return;

                }
                if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email.getText().toString()).find() ){
                    email.setError("Please enter a valid Email");
                    return;
                }
                if(phone.getText().toString().length() !=10){
                    phone.setError("Please enter a valid Phone number.");
                    return;
                }
                if (!password.getText().toString().equals(confirmPassword.getText().toString())){
                    confirmPassword.setError("Password mismatched!");
                    return;
                }
                createAccount();

            }
        });
    }
    private void init(View view){
        email = view.findViewById(R.id.Email);
        phone = view.findViewById(R.id.phone);
        password = view.findViewById(R.id.password);
        confirmPassword = view.findViewById(R.id.confirm_Password);
        createAccountBtn = view.findViewById(R.id.create_account_btn);
        progressBar = view.findViewById(R.id.progressBar);
        loginTV = view.findViewById(R.id.login_text);

    }
    private void createAccount(){
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.fetchSignInMethodsForEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().getSignInMethods().isEmpty()){
                        ((RegisterActivity)getActivity()).setFragment(new OTPFragment(email.getText().toString(),phone.getText().toString(),password.getText().toString()));
                    }else{
                        email.setError("Email already exists!");
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }else{
                    String error = task.getException().getMessage();
                    Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}