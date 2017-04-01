package com.awchoudhary.bookpocket;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by awaeschoudhary on 3/12/17.
 */

public class DatePickerCustom implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    EditText editText;
    private int day;
    private int month;
    private int year;
    private Context context;

    public DatePickerCustom(Context context, EditText editText)
    {
        Activity activity = (Activity)context;
        this.editText = editText;
        this.editText.setOnClickListener(this);
        this.context = context;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        updateDisplay();
    }

    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(context, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();

    }

    // updates the date in the date EditText
    private void updateDisplay() {
        editText.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(month + 1).append("/").append(day).append("/").append(year));
    }
}
