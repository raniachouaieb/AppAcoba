package com.example.appacoba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.regex.Pattern;

public class RegisterScreen extends AppCompatActivity {
    EditText Name,mail, password, phone;
    TextView txtToLogin;
    Button btnRegister;
    ProgressBar loading;
    FirebaseAuth fAuth;

    //private static final Pattern num =
          //  Pattern.compile(
           //         "((\\+|00)216)?(74|71|78|70|72|9|4|2|5|73|75|76|77|79)[0-9]{6}"
           // );


    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[@#$%^&+=])" +     // at least 1 special character
                    "(?=\\S+$)" +            // no white spaces
                    ".{6,}" +                // at least 4 characters
                    "$");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        Name = findViewById(R.id.txtUserName);
        mail = findViewById(R.id.txtMail);
        password = findViewById(R.id.txtPassword);
        phone = findViewById(R.id.txtPhone);
        txtToLogin = findViewById(R.id.txtHaveAccount);
        btnRegister= findViewById(R.id.btnRegister);
        loading = findViewById(R.id.progBar);
        fAuth = FirebaseAuth.getInstance() ;

        if(fAuth.getCurrentUser() !=null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class ));
            finish();
        }




        txtToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginScreen.class));

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nom = Name.getText().toString().trim();

                String adresseEmail = mail.getText().toString().trim();
                String mdp = password.getText().toString().trim();
                String tel = phone.getText().toString().trim();

                if(TextUtils.isEmpty(nom)){
                    Name.setError("Veuillez remplir ce champ.");
                    return;
                }

                if(TextUtils.isEmpty(adresseEmail)){
                    mail.setError("Veuillez remplir ce champ.");
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(adresseEmail).matches()){
                    mail.setError("Saisir un email correct");
                    mail.requestFocus();
                    return;
                }



                if(TextUtils.isEmpty(mdp)){
                    password.setError("Veuillez remplir ce champ.");
                    return;
                }

                if (!PASSWORD_PATTERN.matcher(mdp).matches()) {
                    password.setError("Votre mot de passe doit avoir au minimum: 6 characteres, un caractere specifique, et aucun espace");
                    return;
                }
                if(TextUtils.isEmpty(tel)){
                    phone.setError("Veuillez remplir ce champ.");
                    return;
                }

                if(tel.length() == 13){
                    phone.setError("le numéro du telephone doit etre au min 8 chiffres");
                    return;
                }

                //if(!num.matcher(tel).matches()){
                 //   phone.setError("entrer un numéro valide");
                  //  return;
               // }
                loading.setVisibility(View.VISIBLE);


                //create a new user
                fAuth.createUserWithEmailAndPassword(adresseEmail, mdp).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(nom, adresseEmail, tel);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(RegisterScreen.this, "Sucess", Toast.LENGTH_SHORT).show();
                                                loading.setVisibility(View.GONE);
                                                startActivity(new Intent(getApplicationContext(),LoginScreen.class));


                                            } else{
                                                Toast.makeText(RegisterScreen.this, "Erreur création! Essayer une autre fois" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                loading.setVisibility(View.GONE);

                                            }


                                        }
                                    });
                        }  else {
                            Toast.makeText(RegisterScreen.this, "Erreur création de l'utilisateur" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);

                        }
                    }
                });



            }
        });

    }
}