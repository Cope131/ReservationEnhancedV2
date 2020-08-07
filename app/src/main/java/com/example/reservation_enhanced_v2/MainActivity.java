package com.example.reservation_enhanced_v2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    EditText etName, etTelephone, etSize, etDay, etTime;
    CheckBox checkBox;
    Button btReserve, btReset;

    //Date & Time
    int curDay = 0;
    int curMonth = 0;
    int curYear = 0;

    int curHour = 0;
    int curMin = 0;

    String formattedTime = "";
    String formattedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.editTextName);
        etTelephone = findViewById(R.id.editTextTelephone);
        etSize = findViewById(R.id.editTextSize);
        checkBox = findViewById(R.id.checkBox);

        etDay = findViewById(R.id.editTextDay);
        etTime = findViewById(R.id.editTextTime);

        btReserve = findViewById(R.id.buttonReserve);
        btReset = findViewById(R.id.buttonReset);

        etDay.setText(String.format("%02d/%02d/%04d", curDay, (curMonth + 1), curYear));
        etTime.setText(String.format("%02d : %02d", curHour, curMin));

        //generate current date
        updateDate(true);
        updateTime(true);

        //click date
        etDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              showDatePickerDialog();
            }
        });

        //click time
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
            }
        });

        //click reserve
        btReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean emptyName = etName.getText().toString().isEmpty();
                final boolean emptySize = etSize.getText().toString().isEmpty();
                final boolean emptyPhone = etTelephone.getText().toString().isEmpty();

                if (emptyName || emptySize || emptyPhone) {
                    Toast.makeText(MainActivity.this, R.string.required_warning, Toast.LENGTH_SHORT).show();
                } else {
                    showConfirmDialog();
                }

            }
        });

        //click reset
        btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetInput();
            }
        });
    }

    //retrieved last reservation
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        String name = sp.getString("name","");
        String tel = sp.getString("tel", "");
        String size = sp.getString("size","");
        boolean smoking = sp.getBoolean("smoking", false);
        String day = sp.getString("day", String.format("%02d/%02d/%04d", curDay, (curMonth + 1), curYear));
        String time = sp.getString("time", String.format("%02d : %02d", curHour, curMin));

        etName.setText(name);
        etTelephone.setText(tel);
        etSize.setText(size);
        checkBox.setChecked(smoking);
        etDay.setText(day);
        etTime.setText(time);
    }

    //save reservation
    @Override
    protected void onPause() {
        super.onPause();

        final boolean emptyName = etName.getText().toString().isEmpty();
        final boolean emptySize = etSize.getText().toString().isEmpty();
        final boolean emptyPhone = etTelephone.getText().toString().isEmpty();

        if (!(emptyName && emptySize && emptyPhone)) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor prefEdit = sp.edit();

            //store
            prefEdit.putString("name", etName.getText().toString());
            prefEdit.putString("tel", etTelephone.getText().toString());
            prefEdit.putString("size", etSize.getText().toString());
            prefEdit.putBoolean("smoking", checkBox.isChecked());
            prefEdit.putString("day", String.format("%02d/%02d/%04d", curDay, (curMonth + 1), curYear));
            prefEdit.putString("time", String.format("%02d : %02d", curHour, curMin));

            prefEdit.commit();
        }
    }

    public void showConfirmDialog() {

        final String name = etName.getText().toString();
        final String size = etSize.getText().toString();

        String isSmoke = checkBox.isChecked() ? "smoking" : "non-smoking";

        //build dialog
        AlertDialog.Builder myBuilder = new AlertDialog.Builder(MainActivity.this);

        //set characteristics
        myBuilder.setTitle(R.string.confirm_message)
                .setMessage("New Reservation" + "\n" +
                            "Name: " + name + "\n" +
                            "Size: " + size + "\n" +
                            "Smoking: " + isSmoke + "\n" +
                            "Date: " + formattedDate + "\n" +
                            "Time: " + formattedTime)
                .setCancelable(false)
                .setPositiveButton(R.string.confirmButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, R.string.res_confirmed, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancelButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, R.string.res_cancelled, Toast.LENGTH_SHORT).show();
                    }
                });

        //show dialog
        AlertDialog myDialog  = myBuilder.create();
        myDialog.show();
    }

    public void showTimePickerDialog() {

        TimePickerDialog myTimeDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                curHour = hourOfDay;
                curMin = minute;
                updateTime(false);
            }
        }, curHour, curMin, true);

        //show time picker dialog
        myTimeDialog.show();
    }

    public void showDatePickerDialog() {
        DatePickerDialog myDateDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                curYear = year;
                curMonth = monthOfYear;
                curDay = dayOfMonth;
                updateDate(false);
            }
        }, curYear, curMonth, curDay);

        //show date picker dialog
        myDateDialog.show();
    }

    public void resetInput() {
        etName.getText().clear();
        etTelephone.getText().clear();
        etSize.getText().clear();
        checkBox.setChecked(false);
        //set to current date and time
        updateDate(true);
        updateTime(true);
    }

    public void updateDate(boolean reset) {
        if (reset) {
            Calendar cal = Calendar.getInstance();
            curDay = cal.get(Calendar.DAY_OF_MONTH);
            curMonth = cal.get(Calendar.MONTH);
            curYear = cal.get(Calendar.YEAR);

        }
        formattedDate = String.format("%02d/%02d/%04d", curDay, (curMonth + 1), curYear);
        etDay.setText(formattedDate);
    }

    public void updateTime(boolean reset) {
        if (reset) {
            Calendar cal = Calendar.getInstance();
            curHour = cal.get(Calendar.HOUR_OF_DAY);
            curMin = cal.get(Calendar.MINUTE);

        }
        formattedTime = String.format("%02d : %02d", curHour, curMin);
        etTime.setText(formattedTime);

    }

}