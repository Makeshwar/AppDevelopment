package com.updatedtamizha.vintage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.updatedtamizha.vintage.fragment.HomeFragment;
import com.updatedtamizha.vintage.fragment.NotificationFragment;
import com.updatedtamizha.vintage.fragment.ProfileFragment;
import com.updatedtamizha.vintage.fragment.SearchFragment;
import com.updatedtamizha.vintage.registration.RegisterActivity;
import com.updatedtamizha.vintage.registration.UsernameActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private FrameLayout frameLayout;
    private TabLayout tabLayout;
    private List<Fragment> fragmentList;
    private int tabPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        init();

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        checkUsername();
        fragmentList = new ArrayList<>();
        fragmentList.add(new HomeFragment());
        fragmentList.add(new SearchFragment());
        fragmentList.add(new NotificationFragment());
        fragmentList.add(new NotificationFragment());
        fragmentList.add(new ProfileFragment());


        tabLayout.getTabAt(2).getIcon().setTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() != 2) {
                    tabPosition = tab.getPosition();

                    tab.getIcon().setTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    setFragment(tab.getPosition());
                }else {
                    tabLayout.getTabAt(tabPosition).select();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() != 2) {
                    tab.getIcon().setTintList(ColorStateList.valueOf(Color.parseColor("#BDBDBD")));
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.getTabAt(0).getIcon().setTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        setFragment(0);


    }


    private void init(){
        frameLayout = findViewById(R.id.framelayout);
        tabLayout = findViewById(R.id.tablayout);
    }

    public void setFragment(int position){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        fragmentTransaction.replace(frameLayout.getId(),fragmentList.get(position));
        fragmentTransaction.commit();
    }

    private  void checkUsername(){
        firestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().exists()){

                                if(!task.getResult().contains("username")){
                                    Intent usernameIntent = new Intent(MainActivity.this, UsernameActivity.class);
                                    startActivity(usernameIntent);
                                    finish();
                                }

                            }else {
                                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                                        startActivity(registerIntent);
                                        finish();
                            }
                        }else {
                            String error = task.getException().getMessage();
                            Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }
}