package com.example.smartmenuapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartmenuapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.example.smartmenuapp.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth auth;
    private AlertDialog loadingDialog;
    private void showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        loadingDialog = builder.create();
        loadingDialog.show();
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Login Error")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false);
        builder.create().show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.loginButton.setOnClickListener(v -> {
            String email = Objects.requireNonNull(binding.emailEditText.getText()).toString();
            String password = Objects.requireNonNull(binding.passwordEditText.getText()).toString();


            binding.emailInputLayout.setError(null);
            binding.passwordInputLayout.setError(null);

            if (email.isEmpty() || password.isEmpty()) {
                if (email.isEmpty()) {
                    binding.emailInputLayout.setError("Email required");
                }
                if (password.isEmpty()) {
                    binding.passwordInputLayout.setError("Password required");
                }
                return;
            }
            if (!isValidEmail(email)) {
                binding.emailInputLayout.setError("Invalid email");
                return;
            }
            showLoadingDialog();
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        hideLoadingDialog();

                        if (task.isSuccessful()) {
                            startActivity(new Intent(this, SplashActivity.class));
                            finish();
                        } else {
                            Exception exception = task.getException();

                            assert exception != null;
                            showErrorDialog( exception.getMessage() != null ? exception.getMessage() : "Login failed");

                        }
                    });
        });

        binding.signupButton.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}