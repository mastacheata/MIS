package de.fh_aachen.mis.mis_project;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.fh_aachen.mis.mis_project.database.NoteDataSource;

public class NewNoteActivity extends Activity {

    private NoteDataSource datasource;

    Date remind_me_date;
    private DatePickerDialog remind_me_date_picker;
    private TimePickerDialog remind_me_time_picker;

    Button save_btn;
    Switch remind_me_switch;
    EditText textarea;

    Context context;

    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        datasource = new NoteDataSource(this);
        datasource.open();

        context = this;
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        timeFormatter = new SimpleDateFormat("H:m:s", Locale.US);

        save_btn = (Button) findViewById(R.id.note_save_button);
        remind_me_switch = (Switch) findViewById(R.id.note_remind_me_switch);
        textarea = (EditText) findViewById(R.id.note_data);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("SaveBtn", "Clicked");
                String text = textarea.getText().toString();
                datasource.createNote(text);
                setResult(RESULT_OK, null);
                finish();
            }
        });

        remind_me_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Log.v("RemindMeS", "Checked");
                    Calendar newCalendar = Calendar.getInstance();

                    remind_me_time_picker =  new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(1,1,1,hourOfDay,minute);
                            Log.v("time is: ", timeFormatter.format(newDate.getTime()));
                        }
                    },newCalendar.get(Calendar.HOUR), newCalendar.get(Calendar.MINUTE), true);

                    remind_me_date_picker =  new DatePickerDialog(context, new OnDateSetListener() {

                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year, monthOfYear, dayOfMonth);
                            Log.v("date is: ", dateFormatter.format(newDate.getTime()));
                            remind_me_time_picker.show();
                        }

                    },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

                    remind_me_date_picker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            remind_me_switch.setChecked(false);
                        }
                    });
                    remind_me_date_picker.show();

                }
                else{
                    Log.v("RemindMeS", "Unchecked");
                }
            }
        });


    }

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
