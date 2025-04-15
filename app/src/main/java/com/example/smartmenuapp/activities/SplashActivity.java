package com.example.smartmenuapp.activities;

import androidx.appcompat.app.AppCompatActivity;



import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.smartmenuapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // تأكد من وجود activity_splash.xml

        // الانتظار لمدة 2-3 ثوانٍ قبل الانتقال إلى النشاط التالي
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // التحقق مما إذا كان المستخدم مسجلاً دخوله
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    // إذا كان المستخدم مسجلاً دخوله، توجيه المستخدم إلى MealListActivity
                    Intent intent = new Intent(SplashActivity.this, RecipeListActivity.class);
                    startActivity(intent);
                } else {
                    // إذا لم يكن المستخدم مسجلاً دخوله، توجيه المستخدم إلى شاشة تسجيل الدخول
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                finish(); // إنهاء النشاط الحالي (Splash)
            }
        }, 2000); // الانتظار لمدة 2 ثانية
    }
}
