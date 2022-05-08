package com.example.woodfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterAcitivity extends AppCompatActivity {

    private static final String LOG_TAG = RegisterAcitivity.class.getName();
    private static final String PREF_KEY = RegisterAcitivity.class.getPackage().toString();
    private static final int SECRET_KEY = 99;
    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    EditText userNameEditText;
    EditText passwordEditText;
    EditText passwordAgainEditText;
    EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);
        if (secret_key != 99){
            finish();
        }
        userNameEditText = findViewById(R.id.userNameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordAgainEditText = findViewById(R.id.passwordAgainEditText);
        emailEditText = findViewById(R.id.emailEditText);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String email = preferences.getString("email", "");
        String password = preferences.getString("password", "");

        emailEditText.setText(email);
        passwordEditText.setText(password);
        passwordAgainEditText.setText(password);

        mAuth = FirebaseAuth.getInstance();
        
        Log.i(LOG_TAG, "onCreate");
    }

    public void register(View view) {
        String userName = userNameEditText.getText().toString();
        String password  = passwordEditText.getText().toString();
        String email  = emailEditText.getText().toString();
        String password_again  = passwordAgainEditText.getText().toString();

        if(!password.equals(password_again)){
            Log.e(LOG_TAG, "Nem egyenlő a jelszó a megerősítéssel");
        }

        Log.i("RegisterActivity", "Regisztrált: " + userName + ", email: " + email);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG, "User created successfully");
                    startBrowsing();
                }else{
                    Log.d(LOG_TAG, "User wasn't created successfully");
                    Toast.makeText(RegisterAcitivity.this, "User wasn't created successfully: " + task.getException().getMessage() , Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void cancel(View view) {
        finish();
    }

    private void startBrowsing(){
        Intent intent = new Intent(this, RecipeListActivity.class);
        startActivity(intent);
    }

}