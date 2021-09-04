package com.example.appacoba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginScreen extends AppCompatActivity {
    EditText email, pass;
    Button loginBtn;
    TextView passOublie, inscrire;
    FirebaseAuth fAuth;
    ProgressBar load;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[@#$%^&+=])" +     // at least 1 special character
                    "(?=\\S+$)" +            // no white spaces
                    ".{6,}" +                // at least 4 characters
                    "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        email = findViewById(R.id.txtEmail);
        pass = findViewById(R.id.txtPass);
        passOublie = findViewById(R.id.txtPassOublie);
        fAuth = FirebaseAuth.getInstance() ;
        loginBtn = findViewById(R.id.btnLog);
        load = findViewById(R.id.progBar);
        inscrire = findViewById(R.id.txtInscrire);



        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAdd = email.getText().toString().trim();
                String password = pass.getText().toString().trim();

                if (TextUtils.isEmpty(emailAdd)) {
                    email.setError("Veuillez remplir ce champ.");
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(emailAdd).matches()) {
                    email.setError("Saisir un email correct");
                    email.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    pass.setError("Veuillez remplir ce champ.");
                    return;
                }


                if (!PASSWORD_PATTERN.matcher(password).matches()) {
                    pass.setError("Votre mot de passe doit avoir au minimum: 6 characteres, un caractere specifique, et aucun espace");
                    return;
                }


                load.setVisibility(View.VISIBLE);

                //authentificate the user

                fAuth.signInWithEmailAndPassword(emailAdd,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginScreen.this, "Logged successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        } else {
                            Toast.makeText(LoginScreen.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            load.setVisibility(View.GONE);
                        }

                    }
                });





            }
        }
        );

       passOublie.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               EditText resetMail = new EditText(v.getContext());
               AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
               passwordResetDialog.setTitle("Récuperer mot de passe");
               passwordResetDialog.setMessage("Saisir votre email pour recevoir le lien de récuperation");
               passwordResetDialog.setView(resetMail);

               passwordResetDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       // extract the email and send reset link
                       String mail = resetMail.getText().toString();
                       fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               Toast.makeText(LoginScreen.this, "Le lien de récuperation est envoyé! Vérifier votre email.", Toast.LENGTH_SHORT).show();
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(LoginScreen.this, "Erreur ! le lien n'est pas envoyé" + e.getMessage(), Toast.LENGTH_SHORT).show();
                           }
                       });
                   }
               });

               passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       // close the dialog
                   }
               });

               passwordResetDialog.create().show();
           }
       });

       inscrire.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(getApplicationContext(), RegisterScreen.class));


           }
       });
    }
}