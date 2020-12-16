package com.xianyu.myuser;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private final UserDBHelper userDBHelper = new UserDBHelper(this);
    private final String[] ORDER_COLUMNS = new String[]{"Id", "name", "pwd", "mail", "phone", "sex"};
    private static final String TAG = "LoginActivity";

    private TextView loginName;
    private TextView loginPwd;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginName = findViewById(R.id.login_name);
        loginPwd = findViewById(R.id.login_pwd);
        Button registerBtn = findViewById(R.id.register_btn);
        loginBtn = findViewById(R.id.login_btn);
        registerBtn.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
        login();
    }

    private void login() {
        loginBtn.setOnClickListener(v -> {
            try (
                    SQLiteDatabase db = userDBHelper.getReadableDatabase();
                    Cursor cursor = db.query(UserDBHelper.TABLE_NAME, ORDER_COLUMNS, "name = ? AND pwd = ?", new String[]{loginName.getText().toString(), loginPwd.getText().toString()}, null, null, null)) {
                if (loginName.getText().toString().equals("") || loginPwd.getText().toString().equals("")) {
                    Toast.makeText(this, "请先填写用户名或密码", Toast.LENGTH_SHORT).show();
                } else if (cursor.getCount() <= 0) {
                    Toast.makeText(LoginActivity.this, "用户名或密码填写错误", Toast.LENGTH_SHORT).show();
                } else {
                    if (cursor.moveToFirst()) {
                        Users user = parseUsers(cursor);
                        Intent intent = new Intent(this, ShowActivity.class);
                        intent.putExtra("Id", user.Id);
                        intent.putExtra("name", user.name);
                        intent.putExtra("pwd", user.pwd);
                        intent.putExtra("mail", user.mail);
                        intent.putExtra("phone", user.phone);
                        intent.putExtra("sex", user.sex);
                        startActivity(intent);
                        finish();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "login: ", e);
            }
        });
    }

    private Users parseUsers(Cursor cursor) {
        Users users = new Users();
        users.Id = (cursor.getInt(cursor.getColumnIndex("Id")));
        users.name = (cursor.getString(cursor.getColumnIndex("name")));
        users.pwd = (cursor.getString(cursor.getColumnIndex("pwd")));
        users.mail = (cursor.getString(cursor.getColumnIndex("mail")));
        users.phone = (cursor.getString(cursor.getColumnIndex("phone")));
        users.sex = (cursor.getInt(cursor.getColumnIndex("sex")));
        return users;
    }
}