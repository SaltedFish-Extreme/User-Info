package com.xianyu.myuser;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private final UserDBHelper userDBHelper = new UserDBHelper(this);
    private int sex = 1;
    private final String[] ORDER_COLUMNS = new String[]{"name"};

    EditText ReName;
    EditText RePwd;
    EditText RePwd2;
    EditText ReMail;
    EditText RePhone;
    RadioButton Male;
    RadioButton FeMale;
    Button Register;
    Button Login;
    RadioGroup rgSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ReName = findViewById(R.id.re_name);
        RePwd = findViewById(R.id.re_pwd);
        RePwd2 = findViewById(R.id.re_pwd2);
        ReMail = findViewById(R.id.re_mail);
        RePhone = findViewById(R.id.re_phone);
        Male = findViewById(R.id.Male);
        FeMale = findViewById(R.id.FeMale);
        rgSex = findViewById(R.id.rg_sex);
        Register = findViewById(R.id.register);
        Login = findViewById(R.id.login);

        sex();
        judge();
        register();
        login();
    }

    private void sex() {
        rgSex.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.Male:
                    //当用户选择男性时
                    sex = 1;
                    break;
                case R.id.FeMale:
                    //当用户选择女性时
                    sex = 2;
                    break;
            }
        });
    }

    private void judge() {
        ReName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (RegexUtil.checkChinese(ReName.getText().toString())) {
                    ReName.setTextColor(Color.WHITE);
                } else {
                    ReName.setTextColor(Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ReName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                try (SQLiteDatabase db = userDBHelper.getReadableDatabase(); Cursor cursor = db.query(UserDBHelper.TABLE_NAME, ORDER_COLUMNS, "name = ?", new String[]{ReName.getText().toString()}, null, null, null)) {
                    if (cursor.getCount() > 0) {
                        ReName.setTextColor(Color.RED);
                        Toast.makeText(RegisterActivity.this, "用户名已存在，请重新输入！", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(RegisterActivity.this, "有问题，请处理", Toast.LENGTH_SHORT).show();
                }
            }
        });
        RePwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (RegexUtil.checkPassword(RePwd.getText().toString())) {
                    RePwd.setTextColor(Color.WHITE);
                } else {
                    RePwd.setTextColor(Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        RePwd2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (RePwd2.getText().toString().equals(RePwd.getText().toString())) {
                    RePwd2.setTextColor(Color.WHITE);
                } else {
                    RePwd2.setTextColor(Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ReMail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (RegexUtil.checkEmail(ReMail.getText().toString())) {
                    ReMail.setTextColor(Color.WHITE);
                } else {
                    ReMail.setTextColor(Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        RePhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (RegexUtil.checkPhone(RePhone.getText().toString())) {
                    RePhone.setTextColor(Color.WHITE);
                } else {
                    RePhone.setTextColor(Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void save() {
        SQLiteDatabase db = null;
        try {
            db = userDBHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put("name", ReName.getText().toString());
            contentValues.put("pwd", RePwd.getText().toString());
            contentValues.put("mail", ReMail.getText().toString());
            contentValues.put("phone", RePhone.getText().toString());
            contentValues.put("sex", sex);
            db.insertOrThrow(UserDBHelper.TABLE_NAME, null, contentValues);
            db.setTransactionSuccessful();
        } catch (SQLiteConstraintException e) {
            Toast.makeText(this, "数据有误", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    private void register() {
        try {
            Register.setOnClickListener(v -> {
                SQLiteDatabase db = userDBHelper.getReadableDatabase();
                try (Cursor cursor = db.query(UserDBHelper.TABLE_NAME, ORDER_COLUMNS, "name = ?", new String[]{ReName.getText().toString()}, null, null, null)) {
                    if (ReName.getText().toString().equals("") || RePwd.getText().toString().equals("") || RePwd2.getText().toString().equals("") || ReMail.getText().toString().equals("") || RePhone.getText().toString().equals("")) {
                        Toast.makeText(this, "请先填写个人信息", Toast.LENGTH_SHORT).show();
                    } else if (ReMail.getTextColors().getDefaultColor() != -1 || RePhone.getTextColors().getDefaultColor() != -1 || ReName.getTextColors().getDefaultColor() != -1 || RePwd.getTextColors().getDefaultColor() != -1 || RePwd2.getTextColors().getDefaultColor() != -1) {
                        Toast.makeText(this, "请先确认填写信息是否准确", Toast.LENGTH_SHORT).show();
                    } else if (!RePwd2.getText().toString().equals(RePwd.getText().toString())) {
                        RePwd2.setTextColor(Color.RED);
                    } else if (cursor.getCount() > 0) {
                        ReName.setTextColor(Color.RED);
                        Toast.makeText(RegisterActivity.this, "用户名已存在，请重新输入！", Toast.LENGTH_SHORT).show();
                    } else {
                        save();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "有问题，请确认", Toast.LENGTH_SHORT).show();
        }
    }

    private void login() {
        Login.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }
}