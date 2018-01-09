package kivaaz.com.firebasedownloadupload;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText emailET, passwordET;
    CheckBox signupCb, loginCb;
    Button signupBtn, loginBtn;
    private FirebaseAuth mAuth;
    SharedPreferences sharedpreferences;
    Dialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();

        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passET);

        signupCb = findViewById(R.id.signupCB);
        loginCb = findViewById(R.id.loginCB);

        signupBtn = findViewById(R.id.signupBTN);
        loginBtn = findViewById(R.id.loginBTN);

        progressDialog = new Dialog(this);

        String storedEmail = sharedpreferences.getString("emailKey",null);
        String storedPass = sharedpreferences.getString("passwordKey",null);

        if(storedEmail != null && storedPass != null ){
            createProgressDialog(progressDialog,storedEmail);
            loginUser(storedEmail,storedPass);
        }

        signupCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){
                    loginCb.setChecked(false);
                    signupBtn.setVisibility(View.VISIBLE);
                    loginBtn.setVisibility(View.GONE);
                }
            }
        });
        loginCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){
                    signupCb.setChecked(false);
                    signupBtn.setVisibility(View.GONE);
                    loginBtn.setVisibility(View.VISIBLE);
                }
            }
        });


        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailET.getText().toString().trim();
                final String password = passwordET.getText().toString().trim();

                createProgressDialog(progressDialog,email);

                mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        SharedPreferences.Editor editor = sharedpreferences.edit();

                        editor.putString("emailKey", user.getEmail());
                        editor.putString("passwordKey", password);
                        editor.commit();
                        progressDialog.dismiss();
                        startActivity(new Intent(getApplicationContext(),NavigationDrawer.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailET.getText().toString().trim();
                final String password = passwordET.getText().toString().trim();
                createProgressDialog(progressDialog,email);
                loginUser(email,password);
            }
        });

    }

    private void createProgressDialog(Dialog progressDialog, String storedEmail) {
        View customProgress = LayoutInflater.from(getApplicationContext()).inflate(R.layout.loading_indicator,null);
        TextView useremail = customProgress.findViewById(R.id.useremailTV);
        useremail.setText(storedEmail);

        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Signing in.....");
        progressDialog.setContentView(customProgress);
        progressDialog.show();
    }

    public void loginUser(String email, final String password){
        mAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser user = mAuth.getCurrentUser();
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString("emailKey", user.getEmail());
                editor.putString("passwordKey", password);
                editor.commit();
                progressDialog.dismiss();
                startActivity(new Intent(getApplicationContext(),NavigationDrawer.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
