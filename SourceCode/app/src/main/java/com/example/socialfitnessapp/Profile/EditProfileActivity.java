package com.example.socialfitnessapp.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialfitnessapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class EditProfileActivity extends AppCompatActivity {
    TextView changePP;
    EditText username, name, surname, bio;
    Button cancelBtn, saveBtn;
    ImageView profilePicture;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;
    StorageReference storageRef;
    FirebaseAuth fAuth;
    String userID;
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initialise();
        getUserInformation();
        getUserProfilePicture();
        buttons();

    }

    // Method that will fetch users profile picture
    private void getUserProfilePicture() {

        storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageRef.child("users/" + userID + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                Picasso.get().load(uri).into(profilePicture);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }


    // Fetches user info from db and then displays for editing
    protected void getUserInformation() {

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable  DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                username.setText(value.getString("username"));
                name.setText(value.getString("name"));
                surname.setText(value.getString("surname"));
                bio.setText(value.getString("bio"));
            }
        });

        saveData(documentReference); // Method that is called to save any changes the user to makes
    }

    // Checks that the activity worked correctly and image chosen is not null
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profilePicture.setImageURI(imageUri);
        }
    }

    // Method to upload picture to the firebase storage
    private void uploadPicture() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading image...");
        pd.show();

        StorageReference ref = storageRef.child("users/" + userID + "/profile.jpg");

        ref.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Image Uploaded!", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to Upload", Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("Percentage:  " + progressPercent + "%");
                    }
                });
    }

    // Method that initialises all the views
    protected void initialise() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        fStorage = FirebaseStorage.getInstance();
        storageRef = fStorage.getReference();


        profilePicture = findViewById(R.id.editProfile_profilePicture);
        cancelBtn = findViewById(R.id.editProfile_cancelButton);
        saveBtn = findViewById(R.id.editProfile_saveButton);
        username = findViewById(R.id.editProfile_usernameInput);
        name = findViewById(R.id.editProfile_nameInput);
        surname = findViewById(R.id.editProfile_surnameInput);
        bio = findViewById(R.id.editProfile_bioInput);
        changePP = findViewById(R.id.editProfile_changePPButton);
    }

    // Method that saves all changes made and uploads it when save button is clicked
    protected void saveData(DocumentReference reference) {

        // Saves all changes and uploads it
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uUsername = username.getText().toString().trim();
                String uName = name.getText().toString().trim();
                String uSurname = surname.getText().toString().trim();
                String uBio = bio.getText().toString().trim();

                // Uploads all the data and then takes you back to your profile
                reference.update("name", uName);
                reference.update("surname" , uSurname);
                reference.update("username", uUsername);
                reference.update("bio", uBio);

                uploadPicture();

                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                finish();
            }
        });
    }

    // Method that contains all the buttons that don't require anything passed to them
    protected void buttons() {

        // Allows the user to select a picture and displays that picture
        changePP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }//end of buttons

}