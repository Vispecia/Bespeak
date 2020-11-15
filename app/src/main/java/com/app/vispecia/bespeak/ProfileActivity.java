package com.app.vispecia.bespeak;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.vispecia.bespeak.notifications.Token;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity implements RecyclerViewBookAdapter.BookViewClickListener{


    private final int PICK_FROM_CAMERA = 121;
    private final int PICK_FROM_FILE = 101;
    private final int PICK_FROM_FILE_FOR_USER_PROFILE_IMAGE = 100;
    private final int PICK_FROM_CAMERA_FOR_PROFILE_IMAGE = 131;
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final String FILE_PROVIDER_AUTHORITY = "com.vispecia.android.fileprovider";
    private Uri imageURI,profileImgUri;
    private String mTempPhotoPath;
    private String name,id,place,profileImg,token;

    ImageView userProfileImageView;


    Dialog optionDialog;
    Dialog deleteWarningDialog;
    TextView delete;
    CheckBox checkBox;
    Button okBtn;

    Button deleteBtn,cancelBtn;



    RecyclerView mLayout;
    ArrayList<Book> mData;
    RecyclerViewBookAdapter mBookAdapter;

    TextView userName, bio;
    ImageView bookImg;
    String userPlace;

    BottomAppBar bottomAppBar;
    CoordinatorLayout mCoordinatorLayout;
    FloatingActionButton addFab,seeFollowingFab;


    FirebaseAuth mAuth;
    DatabaseReference mBookDataRef,userInfoRef;
    FirebaseDatabase mDatabase;
    FirebaseUser user;
    StorageReference mStorageRef;
    StorageReference mUserImageRef;

    @Override
    protected void onStart() {

        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            saveIDInSharedPref(FirebaseAuth.getInstance().getCurrentUser());
        }

        super.onStart();

        //retrieveData();
    }

    @Override
    protected void onResume() {

        if(FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            saveIDInSharedPref(FirebaseAuth.getInstance().getCurrentUser());
        }
        else
        {
            finish();
            startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

        super.onResume();

    }

    private void saveIDInSharedPref(FirebaseUser user) {

        SharedPreferences sp = getSharedPreferences("SP_USER",MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("Current_UserID",user.getUid());
        edit.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userProfileImageView = findViewById(R.id.profile_img);

        userName = findViewById(R.id.username_textView);
        bio = findViewById(R.id.bio_textView);

        mLayout = findViewById(R.id.book_Container_recyclerview);
        mData = new ArrayList<>();

        optionDialog = new Dialog(this);
        deleteWarningDialog = new Dialog(this);

        optionDialog.setContentView(R.layout.select_book_option);
        deleteWarningDialog.setContentView(R.layout.delete_book_warning);

        delete = optionDialog.findViewById(R.id.deleteBookText);
        checkBox = optionDialog.findViewById(R.id.exchangeCheckBox);
        okBtn = optionDialog.findViewById(R.id.buttonOkay);

        deleteBtn = deleteWarningDialog.findViewById(R.id.buttonDelete);
        cancelBtn = deleteWarningDialog.findViewById(R.id.buttonCancel);


        mDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("images");
        mUserImageRef = FirebaseStorage.getInstance().getReference("profileImages");


        //for token
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> token = instanceIdResult.getToken());


        if(mAuth.getCurrentUser() != null)
        {
            saveIDInSharedPref(FirebaseAuth.getInstance().getCurrentUser());
            updateToken(token);
        }

        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }






        userInfoRef = mDatabase.getReference("users").child(user.getUid()).child("userdetails");
        mBookDataRef = mDatabase.getReference("users").child(user.getUid()).child("books");
        mBookDataRef.keepSynced(true);

        mCoordinatorLayout = findViewById(R.id.profile_activity_coordinatorLayout);
        bottomAppBar = findViewById(R.id.profileActivity_bottomAppBar);
        addFab = findViewById(R.id.profile_activity_back_home_fab);
        seeFollowingFab = findViewById(R.id.see_following_profile_fab);


        final String []items = new String[] {"from camera","from gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.select_dialog_item,items);

        // building alert box to select from camera or from gallery for BOOKS
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Select image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // If you do not have permission, request it
                    ActivityCompat.requestPermissions(ProfileActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_STORAGE_PERMISSION);
                }
                else {
                    if (i == 0) {
                        //camera

                        // Launch the camera if the permission exists
                        launchCamera();
                        dialogInterface.cancel();
                    } else {
                        // gallery
                        if (Build.VERSION.SDK_INT <= 19) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_FROM_FILE);
                        } else if (Build.VERSION.SDK_INT > 19) {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_FROM_FILE);
                        }

                    }
                }

            }
        });

        // attaching the alert dialog builder
        final AlertDialog alertDialog = builder.create();



        //building alert box to select from camera or from gallery for USER_PROFILE_IMAGE
        AlertDialog.Builder builder2 = new AlertDialog.Builder(ProfileActivity.this);
        builder2.setTitle("Select image");
        builder2.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // If you do not have permission, request it
                    ActivityCompat.requestPermissions(ProfileActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_STORAGE_PERMISSION);
                }
                else {
                    if (i == 0) {
                        //camera

                        // Launch the camera if the permission exists
                        launchCameraForUserProfileImage();
                        dialogInterface.cancel();
                    } else {
                        // gallery
                        if (Build.VERSION.SDK_INT <= 19) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_FROM_FILE_FOR_USER_PROFILE_IMAGE);
                        } else if (Build.VERSION.SDK_INT > 19) {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_FROM_FILE_FOR_USER_PROFILE_IMAGE);
                        }

                    }
                }

            }
        });

        final AlertDialog alertDialog2 = builder2.create();

        userProfileImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                alertDialog2.show();
                return true;
            }
        });



        seeFollowingFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProfileActivity.this,FollowingActivity.class);
                startActivity(intent);

            }
        });


        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ProfileActivity.this,R.style.BottomSheetTheme);

                final View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_book_bottom_sheet,
                        (LinearLayout)findViewById(R.id.addBook_bottom_sheet),false);

                bookImg = v.findViewById(R.id.bookImageChooserBottomSheet);


                v.findViewById(R.id.bookImageChooserBottomSheet).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // showing the alertdialog
                        alertDialog.show();
                    }
                });


                v.findViewById(R.id.addNewBookButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        EditText e1 = v.findViewById(R.id.bookNameBottomSheetEdittext);
                        final String bName = e1.getText().toString().trim();

                        EditText e2 = v.findViewById(R.id.authorNameBottomSheetEdittext);
                        final String aName = e2.getText().toString().trim();




                        if(bName!=null && aName!=null && imageURI!=null && !bName.equals("") && !aName.equals("")) {

                            //store image on firebase storage and retriev it in a cardview


                            final StorageReference storageReference = mStorageRef.child(System.currentTimeMillis() + "."+ getExtension(imageURI));
                            storageReference.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            String key = mBookDataRef.push().getKey();

                                            Book book = new Book(bName, aName,uri.toString(),key,true);
                                            mBookDataRef.child(key).setValue(book);
                                        }
                                    });

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                                }
                            });


                        }
                        else{
                            Snackbar.make(mCoordinatorLayout, "No field should left empty.", Snackbar.LENGTH_SHORT)
                                    .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent))
                                    .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark))
                                    .show();
                        }

                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.setContentView(v);
                bottomSheetDialog.show();

            }
        });

        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                item.setChecked(true);

                switch (item.getItemId()) {
                    case R.id.profile_app_bar:  // need change from profile activity to home activity (later task)
                        Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.settings_app_bar:
                        FirebaseAuth.getInstance().signOut();
                        finish();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;
                    case R.id.search_app_bar:
                        Intent i = new Intent(ProfileActivity.this, MainActivity.class);
                        i.putExtra("place",userPlace);
                        startActivity(i);
                        break;
                    case R.id.request_app_bar:
                        Intent intent1 = new Intent(ProfileActivity.this, RequestActivity.class);
                        startActivity(intent1);
                        break;


//                    case R.id.chat_app_bar:
//                        ChatListFragment c = new ChatListFragment();
//                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                        ft.replace(R.id.container,ft,"");
//                        ft.commit();
//                        break;


                }

                return true;
            }
        });

        retrieveData();
    }




    private void retrieveData() {

        mBookDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mData.clear();

                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    Book book = data.getValue(Book.class);
                    mData.add(book);
                }

                mBookAdapter = new RecyclerViewBookAdapter(ProfileActivity.this,mData,ProfileActivity.this);
                mLayout.setLayoutManager(new GridLayoutManager(ProfileActivity.this,3));
                mLayout.setAdapter(mBookAdapter);
                mBookAdapter.updateData(mData);
                mBookAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        userInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName.setText("");
                bio.setText("");

                    Users user = dataSnapshot.getValue(Users.class);

                    userName.setText(user.getUsername());
                    bio.setText("Change on settings tab");
                    userPlace = user.getPlace();
                    name = user.getUsername();
                    id = user.getId();
                    place = user.getPlace();

                    try {

                        if(user.getProfileImage()!=null || !user.getProfileImage().equals("")) {
                            Picasso.get().load(user.getProfileImage())
                                    .fit()
                                    .into(userProfileImageView);
                        }
                        else
                        {
                            userProfileImageView.setImageResource(R.drawable.ic_avatar);
                        }




                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode!=RESULT_OK)
            return;

        if(requestCode == PICK_FROM_FILE && data!=null && data.getData()!=null)
        {
            imageURI = data.getData();
            Log.d("uri",imageURI+"");
            Picasso.get().load(imageURI)
                    .resizeDimen(R.dimen.image_size,R.dimen.image_size)
                    .centerCrop()
                    .onlyScaleDown()
                    .into(bookImg);
        }
        else if(requestCode == PICK_FROM_FILE_FOR_USER_PROFILE_IMAGE && data!=null && data.getData()!=null)
        {
            profileImgUri = data.getData();
            Picasso.get().load(profileImgUri)
                    .fit()
                    .into(userProfileImageView);

                storeImgIntoFirebase();


        }
        else if(requestCode == PICK_FROM_CAMERA)
        {
            Picasso.get().load(imageURI).into(bookImg);
        }
        else if(requestCode == PICK_FROM_CAMERA_FOR_PROFILE_IMAGE)
        {
            Picasso.get().load(profileImgUri).into(userProfileImageView);

            storeImgIntoFirebase();
        }

    }

    private void storeImgIntoFirebase() {

        final StorageReference storageReference = mUserImageRef.child(System.currentTimeMillis() + "."+ getExtension(profileImgUri));
        storageReference.putFile(profileImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        HashMap<String,Object> mp = new HashMap<>();
                        mp.put("profileImage",uri.toString());
                        //userInfoRef.setValue(new Users(name,id,place,uri.toString(),"online"));
                        userInfoRef.updateChildren(mp);
                    }
                });

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });


    }

    @Override
    public void bookOnClickListener(final Book index,final View exchangeView) {

        checkBox.setChecked(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(index.getKey()+"_status",true));

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBookDataRef.child(index.getKey()).removeValue();
                Toast.makeText(getApplicationContext(),"Book deleted successfully",Toast.LENGTH_SHORT).show();
                deleteWarningDialog.dismiss();
                optionDialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteWarningDialog.dismiss();
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteWarningDialog.show();

            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = checkBox.isChecked();
                exchangeView.setBackgroundColor(checked ? Color.parseColor("#008000") : Color.parseColor("#FF0000"));
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                        .putBoolean(index.getKey()+"_status", checked).commit();

                index.setExchangable(checked);
                mBookDataRef.child(index.getKey()).setValue(index);


                optionDialog.dismiss();
            }
        });

        optionDialog.show();

    }

    private String getExtension(Uri uri)
    {
        ContentResolver c = getContentResolver();
        MimeTypeMap mimi = MimeTypeMap.getSingleton();
        return mimi.getExtensionFromMimeType(c.getType(uri));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Called when you request permission to read and write to external storage
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera

                } else {
                    // If you do not get permission, show a Toast
                    Toast.makeText(this, "Please grant permission!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void launchCamera()
    {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // File file = new File(getApplicationContext().getExternalFilesDir(null).getAbsolutePath(),"book"+System.currentTimeMillis() + ".jpg");
        //imageURI = Uri.fromFile(file);

        File photoFile = null;


        try {
            photoFile = BitmapUtils.createTempImageFile(getApplicationContext());

//                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageURI);
//                        cameraIntent.putExtra("captured",true);
//                        startActivityForResult(cameraIntent,PICK_FROM_CAMERA);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(photoFile!=null)
        {
            mTempPhotoPath = photoFile.getAbsolutePath();
            Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                    FILE_PROVIDER_AUTHORITY,
                    photoFile);
            imageURI = photoURI;
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
            startActivityForResult(cameraIntent,PICK_FROM_CAMERA);
        }
    }

    private void launchCameraForUserProfileImage()
    {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // File file = new File(getApplicationContext().getExternalFilesDir(null).getAbsolutePath(),"book"+System.currentTimeMillis() + ".jpg");
        //imageURI = Uri.fromFile(file);

        File photoFile = null;


        try {
            photoFile = BitmapUtils.createTempImageFile(getApplicationContext());

//                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageURI);
//                        cameraIntent.putExtra("captured",true);
//                        startActivityForResult(cameraIntent,PICK_FROM_CAMERA);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(photoFile!=null)
        {
            mTempPhotoPath = photoFile.getAbsolutePath();
            Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                    FILE_PROVIDER_AUTHORITY,
                    photoFile);
            profileImgUri = photoURI;
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
            startActivityForResult(cameraIntent,PICK_FROM_CAMERA_FOR_PROFILE_IMAGE);
        }
    }

    // update token (related to notification of messages)
    public void updateToken(String token)
    {
        DatabaseReference dbRef = mDatabase.getReference("tokens");
        Token token1 = new Token(token);
        dbRef.child(user.getUid()).setValue(token1);
    }



}
