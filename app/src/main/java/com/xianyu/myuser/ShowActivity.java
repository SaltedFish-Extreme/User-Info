package com.xianyu.myuser;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ShowActivity extends AppCompatActivity {

    private final UserDBHelper userDBHelper = new UserDBHelper(this);
    private final String[] ORDER_COLUMNS = new String[]{"name"};

    private Button BtnModify;
    private TextView swName;
    private TextView swPwd;
    private TextView swMail;
    private TextView swPhone;
    private TextView swSex;
    int id;
    int gender;
    String sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        swName = findViewById(R.id.sw_name);
        swPwd = findViewById(R.id.sw_pwd);
        swMail = findViewById(R.id.sw_mail);
        swPhone = findViewById(R.id.sw_phone);
        swSex = findViewById(R.id.sw_gender);
        BtnModify = findViewById(R.id.modify);

        Intent intent = getIntent();
        id = intent.getIntExtra("Id", 0);
        swName.setText(intent.getStringExtra("name"));
        swPwd.setText(intent.getStringExtra("pwd"));
        swMail.setText(intent.getStringExtra("mail"));
        swPhone.setText(intent.getStringExtra("phone"));
        if (intent.getIntExtra("sex", 0) == 1) {
            sex = "男";
            gender = 1;
        }
        if (intent.getIntExtra("sex", 0) == 2) {
            sex = "女";
            gender = 2;
        }
        swSex.setText(sex);

        modify();
        save();
    }

    private void modify() {
        swName.setOnClickListener(v -> {
            final EditText inputServer = new EditText(ShowActivity.this);
            AlertDialog.Builder builder = new AlertDialog.Builder(ShowActivity.this);
            builder.setTitle("修改用户名").setIcon(R.drawable.info).setView(inputServer)
                    .setNegativeButton("Cancel", null);
            builder.setPositiveButton("Ok", (dialog, which) -> swName.setText(inputServer.getText().toString()));
            builder.show();
        });
        swPwd.setOnClickListener(v -> {
            final EditText inputServer = new EditText(ShowActivity.this);
            AlertDialog.Builder builder = new AlertDialog.Builder(ShowActivity.this);
            builder.setTitle("修改密码").setIcon(R.drawable.info).setView(inputServer)
                    .setNegativeButton("Cancel", null);
            builder.setPositiveButton("Ok", (dialog, which) -> swPwd.setText(inputServer.getText().toString()));
            builder.show();
        });
        swMail.setOnClickListener(v -> {
            final EditText inputServer = new EditText(ShowActivity.this);
            AlertDialog.Builder builder = new AlertDialog.Builder(ShowActivity.this);
            builder.setTitle("修改邮箱").setIcon(R.drawable.info).setView(inputServer)
                    .setNegativeButton("Cancel", null);
            builder.setPositiveButton("Ok", (dialog, which) -> swMail.setText(inputServer.getText().toString()));
            builder.show();
        });
        swPhone.setOnClickListener(v -> {
            final EditText inputServer = new EditText(ShowActivity.this);
            AlertDialog.Builder builder = new AlertDialog.Builder(ShowActivity.this);
            builder.setTitle("修改手机号").setIcon(R.drawable.info).setView(inputServer)
                    .setNegativeButton("Cancel", null);
            builder.setPositiveButton("Ok", (dialog, which) -> swPhone.setText(inputServer.getText().toString()));
            builder.show();
        });
        swSex.setOnClickListener(v -> {
            final String[] sex = new String[]{"男", "女"};
            AlertDialog.Builder builder = new AlertDialog.Builder(ShowActivity.this);
            builder.setTitle("修改性别").setIcon(R.drawable.info);
            builder.setSingleChoiceItems(sex, gender - 1, (dialog, which) -> {
                swSex.setText(sex[which]);
                gender = which + 1;
            });
            AlertDialog alert = builder.create();
            alert.show();
        });
    }

    private void save() {
        BtnModify.setOnClickListener(v -> {
            SQLiteDatabase db = userDBHelper.getReadableDatabase();
            final AlertDialog.Builder normalDialog =
                    new AlertDialog.Builder(ShowActivity.this);
            normalDialog.setIcon(R.drawable.info);
            normalDialog.setTitle("确认信息");
            normalDialog.setMessage("确定提交修改吗?");
            normalDialog.setPositiveButton("取消", null);
            normalDialog.setNegativeButton("确定",
                    (dialog, which) -> {
                        try (Cursor cursor = db.query(UserDBHelper.TABLE_NAME, ORDER_COLUMNS, "name = ? AND Id != ?", new String[]{swName.getText().toString(), id + ""}, null, null, null)) {
                            if (cursor.getCount() > 0) {
                                Toast.makeText(ShowActivity.this, "用户名已存在，请重新输入", Toast.LENGTH_SHORT).show();
                            } else if (!RegexUtil.checkChinese(swName.getText().toString()) || !RegexUtil.checkPassword(swPwd.getText().toString()) || !RegexUtil.checkEmail(swMail.getText().toString()) || !RegexUtil.checkPhone(swPhone.getText().toString())) {
                                Toast.makeText(ShowActivity.this, "输入的信息格式有误，请重新检查确认", Toast.LENGTH_SHORT).show();
                            } else {
                                saveDB();
                            }
                        } catch (Exception e) {
                            Toast.makeText(ShowActivity.this, "有问题，请确认", Toast.LENGTH_SHORT).show();
                        }
                    });
            //显示
            normalDialog.show();
        });
    }

    private void saveDB() {
        SQLiteDatabase db = null;
        try {
            db = userDBHelper.getWritableDatabase();
            db.beginTransaction();

            ContentValues cv = new ContentValues();
            cv.put("name", swName.getText().toString());
            cv.put("pwd", swPwd.getText().toString());
            cv.put("mail", swMail.getText().toString());
            cv.put("phone", swPhone.getText().toString());
            cv.put("sex", gender);

            db.update(UserDBHelper.TABLE_NAME,
                    cv,
                    "Id = ?",
                    new String[]{String.valueOf(id)});

            db.setTransactionSuccessful();
            Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ShowActivity.this, LoginActivity.class));
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "修改失败", Toast.LENGTH_SHORT).show();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }
}
