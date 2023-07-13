package com.updatedtamizha.vintage.registration;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.updatedtamizha.vintage.MainActivity;
import com.updatedtamizha.vintage.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsernameActivity extends AppCompatActivity {

    private CircleImageView profileImageview;
    private Button removeBtn,createAccountBtn;
    private EditText username;
    private ProgressBar progressBar;
    private Uri photoUri;
    private StorageReference storage;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private String url = "";

    public final static String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);

        init();

        storage = FirebaseStorage.getInstance().getReference();
        firebaseAuth= FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        profileImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(UsernameActivity.this)
                        .withPermissions(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ).withListener(new MultiplePermissionsListener() {
                    @Override

                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            selectImage();
                        } else {
                            Toast.makeText(UsernameActivity.this, "Please allow permission", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override

                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();
            }
        });
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoUri = null;
                profileImageview.setImageResource(R.drawable.profile);
            }
        });

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username.setError(null);
                if (username.getText().toString().isEmpty() || username.getText().toString().length() < 3){
                    username.setError("Minimum 3 characters are mandatory");
                    return;
                }

                if (!username.getText().toString().matches(USERNAME_PATTERN)){
                    username.setError("Only \"a to z, 0 to 9,_ and -\"these characters are allowed");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                firestore.collection("users").whereEqualTo("username",username.getText().toString())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull  Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> document = task.getResult().getDocuments();
                            if (document.isEmpty()) {
                                uploadData();

                            }else {
                                progressBar.setVisibility(View.INVISIBLE);
                               username.setError("Already exists");
                               return;
                            }
                        }else{
                            progressBar.setVisibility(View.INVISIBLE);
                            String error = task.getException().getMessage();
                            Toast.makeText(UsernameActivity.this,error , Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        });
    }
    private void init(){
        profileImageview= findViewById(R.id.profile_image);
        createAccountBtn= findViewById(R.id.create_account_btn);
        removeBtn= findViewById(R.id.remove_btn);
        progressBar= findViewById(R.id.progressbara);
        username= findViewById(R.id.username);
    }
    private void  selectImage(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityMenuIconColor(getResources().getColor(R.color.colorAccent))
                .setActivityTitle("Profile Photo")
                .setFixAspectRatio(true)
                .setAspectRatio(1,1)
                .start(this);

    }
    private void uploadData(){
        if (photoUri !=null){////upload profile with username
            final StorageReference ref = storage.child("profiles/"+firebaseAuth.getCurrentUser().getUid());
            UploadTask uploadTask = ref.putFile(photoUri);



            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        progressBar.setVisibility(View.INVISIBLE);
                        String error = task.getException().getMessage();
                        Toast.makeText(UsernameActivity.this,error , Toast.LENGTH_SHORT).show();

                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            url = uri.toString();
                        }
                    });
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        uploadUsername();
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        String error = task.getException().getMessage();
                        Toast.makeText(UsernameActivity.this,error , Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else{////upload username only
            uploadUsername();

        }

    }
    private void uploadUsername(){
        Map<String  ,Object> map = new HashMap<>();
        map.put("username",username.getText().toString());
        map.put("profile_URl",url);

        firestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).update(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent mainIntent = new Intent(UsernameActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }else {
                    progressBar.setVisibility(View.INVISIBLE);
                    String error = task.getException().getMessage();
                    Toast.makeText(UsernameActivity.this,error , Toast.LENGTH_SHORT).show();

                }

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                 photoUri = result.getUri();

                    Glide
                            .with(this)
                            .load(photoUri)
                            .centerCrop()
                            .placeholder(R.drawable.profile)
                            .into(profileImageview);




            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }
    }
}
