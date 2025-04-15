package com.example.smartmenuapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartmenuapp.models.User;
import com.google.firebase.auth.FirebaseAuth;

import com.example.smartmenuapp.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.signupButton.setOnClickListener(v -> {
            String fullName = Objects.requireNonNull(binding.nameEditText.getText()).toString().trim();
            String email = Objects.requireNonNull(binding.emailEditText.getText()).toString().trim();
            String phone = Objects.requireNonNull(binding.phoneEditText.getText()).toString().trim();
            String password = Objects.requireNonNull(binding.passwordEditText.getText()).toString().trim();
            String confirmPassword = Objects.requireNonNull(binding.confirmPasswordEditText.getText()).toString().trim();
            binding.nameInputLayout.setError(null);
            binding.emailInputLayout.setError(null);
            binding.phoneInputLayout.setError(null);
            binding.passwordInputLayout.setError(null);
            binding.confirmPasswordInputLayout.setError(null);

            if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                if (fullName.isEmpty()) {
                    binding.nameInputLayout.setError("Full Name is required");
                }
                if (email.isEmpty()) {
                    binding.emailInputLayout.setError("Email is required");
                }
                if (phone.isEmpty()) {
                    binding.phoneInputLayout.setError("Phone is required");
                }
                if (password.isEmpty()) {
                    binding.passwordInputLayout.setError("Password is required");
                }
                if (confirmPassword.isEmpty()) {
                    binding.confirmPasswordInputLayout.setError("Confirm Password is required");
                }
                return;
            }

            // التحقق من صحة البريد الإلكتروني
            if (!isValidEmail(email)) {
                binding.emailInputLayout.setError("Invalid email format");
                return;
            }

            // التحقق من صحة رقم الهاتف
            if (!isValidPhone(phone)) {
                binding.phoneInputLayout.setError("Invalid phone number");
                return;
            }

            // التحقق من تطابق كلمة المرور
            if (!password.equals(confirmPassword)) {
                binding.confirmPasswordInputLayout.setError("Passwords do not match");
                return;
            }
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // حفظ بيانات المستخدم في Firestore
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                User userProfile = new User(fullName, phone, email); // Model Class for User
                                FirebaseFirestore.getInstance().collection("users")
                                        .document(user.getUid())
                                        .set(userProfile)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(this, LoginActivity.class));
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            Toast.makeText(this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    } private boolean isValidPhone(String phone) {
        String phonePattern = "^[+]?[0-9]{10,13}$";
        Pattern pattern = Pattern.compile(phonePattern);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }
}