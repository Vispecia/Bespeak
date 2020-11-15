package com.app.vispecia.bespeak;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {


    EditText email,username,password;
    TextView login;
    FirebaseAuth mAuth;
    TextView spinner;
    ArrayAdapter<String> arrayAdapter;
    String selectedPlace = null;

   /* public void addNewUser(View view)
    {
        String user = username.getText().toString();
        String pass = password.getText().toString();
        if(!TextUtils.isEmpty(user)&&!TextUtils.isEmpty(pass)) {

            String id = myRef.push().getKey();
            Users u = new Users(user,pass,id,0,0,0);

            myRef.child(id).setValue(u);
            Toast.makeText(this, "Adding...", Toast.LENGTH_SHORT).show();
            username.setText("");
            password.setText("");
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);

        }
        else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    } */


    public void addNewUser(View view)
    {
        String user = username.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String eMail = email.getText().toString().trim();

        if(eMail.isEmpty())
        {
            email.setError("email is required");
            email.requestFocus();
            return;
        }
        if(user.isEmpty())
        {
            username.setError("username is required");
            username.requestFocus();
            return;
        }
        if(pass.isEmpty())
        {
            password.setError("password is required");
            password.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(eMail).matches())
        {
            email.setError("enter valid email");
            email.requestFocus();
            return;
        }
        if(pass.length()<6)
        {
            password.setError("password length need atleast 6");
            password.requestFocus();
            return;
        }


        mAuth.createUserWithEmailAndPassword(eMail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                 //  sendEmailVerification();

                    FirebaseUser regUser = FirebaseAuth.getInstance().getCurrentUser();

                    String user = username.getText().toString();
                    String pass = password.getText().toString();
                    if(!TextUtils.isEmpty(user)&&!TextUtils.isEmpty(pass)) {

                       // String id = myRef.push().getKey();
                        String place = selectedPlace;

                        Users u = new Users(user,regUser.getUid(),place,"","online");

                        placRef.child(place).child(regUser.getUid()).setValue(u);

                        myRef.child(regUser.getUid()).child("userdetails").setValue(u);


                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }





                    finish();
                    Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                    //OnAuth(task.getResult().getUser());
                    //mAuth.signOut();
                }else{
                    Toast.makeText(RegisterActivity.this,"error on creating user",Toast.LENGTH_SHORT).show();
                }
            }
        });




    }


    //Email verification code using FirebaseUser object and using isSucccessful()function.
    private void sendEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Check your Email for verification", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                }
            });
        }
    }


    private FirebaseDatabase mFirebasedatabase;
    private DatabaseReference myRef,placRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        spinner = findViewById(R.id.spinner);
        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.places));
        spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(RegisterActivity.this)
                        .setTitle("Select Place")
                        .setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                selectedPlace = arrayAdapter.getItem(which);
                                spinner.setText(selectedPlace);
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });


        mAuth = FirebaseAuth.getInstance();
        mFirebasedatabase = FirebaseDatabase.getInstance();
        myRef = mFirebasedatabase.getReference("users");
        placRef = mFirebasedatabase.getReference("places");

        email = (EditText)findViewById(R.id.editTextEmail);
        username = (EditText)findViewById(R.id.usernameSignUp);
        password = (EditText)findViewById(R.id.passwordSignUp);
        login = (TextView) findViewById(R.id.loginTextSignupScreen);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });

    }
}
