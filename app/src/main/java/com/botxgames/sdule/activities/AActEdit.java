package com.botxgames.sdule.activities;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.botxgames.sdule.R;
import com.botxgames.sdule.Utilities.AlarmReciever;
import com.botxgames.sdule.Utilities.C;
import com.botxgames.sdule.Utilities.Setting;
import com.botxgames.sdule.db.TAct;
import com.botxgames.sdule.entities.Act;
import com.botxgames.sdule.entities.Time;
import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Set;

public class AActEdit extends MyBaseActivity implements View.OnClickListener, View.OnLongClickListener {

	private int actId;
	private TextView timeText, timeAmText, doneText, cancelText;
	private EditText actText;
	private Switch remindSwitch;

	private Button monB, tueB, wedB, thrB, friB, satB, sunB;

	private Act act;
	private AlarmManager am;

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_act_edit);
		setTheme(Setting.theme);

		actId = getIntent().getIntExtra("act_id", -190);

		final Typeface typeface = Typeface.createFromAsset(getAssets(), Setting.font);
		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		final TextView titleText = (TextView) findViewById(R.id.a_act_edit_text);
		titleText.setTypeface(typeface);
		timeText = (TextView) findViewById(R.id.a_act_edit_time);
		timeAmText = (TextView) findViewById(R.id.a_act_edit_time_am);
		doneText = (TextView) findViewById(R.id.a_act_edit_done);
		cancelText = (TextView) findViewById(R.id.a_act_edit_cancel);
		remindSwitch = (Switch) findViewById(R.id.a_act_edit_remind);
		remindSwitch.setTypeface(typeface);
		remindSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

			}
		});

		timeText.setTypeface(typeface);
		if (Setting.timeFormat24) {
			timeAmText.setVisibility(View.GONE);
		} else {
			timeAmText.setTypeface(typeface);
			timeAmText.setVisibility(View.VISIBLE);
		}
		doneText.setTypeface(typeface);
		cancelText.setTypeface(typeface);
		actText = (EditText) findViewById(R.id.a_act_edit_activity);
		actText.setTypeface(typeface);

		timeText.setOnClickListener(this);

		monB = (Button) findViewById(R.id.a_act_edit_day_0);
		monB.setOnLongClickListener(this);
		tueB = (Button) findViewById(R.id.a_act_edit_day_1);
		tueB.setOnLongClickListener(this);
		wedB = (Button) findViewById(R.id.a_act_edit_day_2);
		wedB.setOnLongClickListener(this);
		thrB = (Button) findViewById(R.id.a_act_edit_day_3);
		thrB.setOnLongClickListener(this);
		friB = (Button) findViewById(R.id.a_act_edit_day_4);
		friB.setOnLongClickListener(this);
		satB = (Button) findViewById(R.id.a_act_edit_day_5);
		satB.setOnLongClickListener(this);
		sunB = (Button) findViewById(R.id.a_act_edit_day_6);
		sunB.setOnLongClickListener(this);


		if (actId > 0) {

			titleText.setText("Edit Activity");
			doneText.setText("save");
			doneText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					TAct tAct = new TAct(AActEdit.this);
					act.setText(actText.getText().toString());
					act.setRemind(remindSwitch.isChecked());
					if (act.isDaysEmpty()) {
						showLongToast("Select at least one week day");
					} else {


						setReminder();

						tAct.update(act);
						AActEdit.this.finish();
					}

				}
			});

			cancelText.setText("delete");
			cancelText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					final Snackbar sb = Snackbar.make(findViewById(R.id.a_act_edit),

							"Delete activity " + act.getText() + "?",
							Snackbar.LENGTH_SHORT)
							.setAction("Sure", new View.OnClickListener() {
								@Override
								public void onClick(View v) {

									TAct tAct = new TAct(AActEdit.this);
									tAct.delete(actId);
									AActEdit.this.finish();

								}
							});

					View sbv = sb.getView();
					sbv.setBackgroundColor(C.getMyColor(AActEdit.this, R.color.colorPrimaryDark));
					sb.setActionTextColor(C.getMyColor(AActEdit.this, R.color.colorRed));
					TextView tv = (TextView) (sb.getView()).findViewById(android.support.design.R.id.snackbar_text);
					TextView tv1 = (TextView) (sb.getView()).findViewById(android.support.design.R.id.snackbar_action);
					tv.setTypeface(typeface);
					tv1.setTypeface(typeface);
					sb.show();

				}
			});

			TAct tAct = new TAct(this);

			act = tAct.getAct(actId);
			if (Setting.timeFormat24) {
				timeText.setText(act.getTime().toString());
			} else {
				final String[] timeString = act.getTime().toStringSperateAm();
				timeText.setText(timeString[0]);
				timeAmText.setText(timeString[1]);
			}
			actText.setText(act.getText());
			remindSwitch.setChecked(act.isRemind());

			toggleDayButton(monB, act.getDays()[0] == 1);
			toggleDayButton(tueB, act.getDays()[1] == 1);
			toggleDayButton(wedB, act.getDays()[2] == 1);
			toggleDayButton(thrB, act.getDays()[3] == 1);
			toggleDayButton(friB, act.getDays()[4] == 1);
			toggleDayButton(satB, act.getDays()[5] == 1);
			toggleDayButton(sunB, act.getDays()[6] == 1);


		} else {

			titleText.setText("Add new Activity");

			act = new Act(-1, Time.getTimeNow(), "", false, new int[]{0, 0, 0, 0, 0, 0, 0});

			act.setDayValue(Time.getDayToday(), 1);


			toggleDayButton(monB, Time.getDayToday() == 0);
			toggleDayButton(tueB, Time.getDayToday() == 1);
			toggleDayButton(wedB, Time.getDayToday() == 2);
			toggleDayButton(thrB, Time.getDayToday() == 3);
			toggleDayButton(friB, Time.getDayToday() == 4);
			toggleDayButton(satB, Time.getDayToday() == 5);
			toggleDayButton(sunB, Time.getDayToday() == 6);

			if (Setting.timeFormat24) {
				timeText.setText(Time.getTimeNow().toString());
			} else {
				final String[] timeString = Time.getTimeNow().toStringSperateAm();
				timeText.setText(timeString[0]);
				timeAmText.setText(timeString[1]);
			}

			cancelText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					AActEdit.this.finish();
				}
			});

			doneText.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {


					act.setText(actText.getText().toString());
					act.setRemind(remindSwitch.isChecked());

					final TAct tAct = new TAct(AActEdit.this);
					if (act.getText().isEmpty()) {
						actText.setError("Activity cannot be empty");
					} else {

						if (act.isDaysEmpty()) {
							showLongToast("select at least one week day");
						} else {

							setReminder();

							tAct.insertNewAct(act);
							AActEdit.this.finish();
							showShortToast("New Activity Added");
						}
					}

				}
			});

		}
	}

	private void setReminder() {

		final Intent intent = new Intent(AActEdit.this, AlarmReciever.class);

		// set reminder
		String actText = act.getText();
		if (Setting.reminder_before == 0) {
			actText = "Time for " + actText + ".";
		} else if (Setting.reminder_before == 1) {

			actText = actText + " in " + Setting.reminder_before + " minute.";

		} else {

			actText = actText + " in " + Setting.reminder_before + " minutes.";

		}
		intent.putExtra("act", actText);
		final PendingIntent pendingIntent = PendingIntent.getBroadcast(AActEdit.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		if (act.isRemind()) {

			final Time newTime = act.getTime().getTimeBefore(Setting.reminder_before);
			log_i("act time: " + act.getTime().toString() + ", reminder time: " + newTime.toString());
			am.set(
					AlarmManager.RTC_WAKEUP,
					newTime.getTimeInLong(),
					pendingIntent
			);
			showShortToast("reminder set!");
		} else {
			am.cancel(pendingIntent);
		}
	}

	@Override
	public void onClick(View v) {

		if (v == timeText) {


			// Get Current Time
			final Calendar c = Calendar.getInstance();
			int hr = c.get(Calendar.HOUR_OF_DAY);
			int min = c.get(Calendar.MINUTE);

			if (act != null) {

				hr = act.getTime().getHour();
				min = act.getTime().getMin();

			}

			// Launch Time Picker Dialog
			TimePickerDialog timePickerDialog = new TimePickerDialog(this,
					new TimePickerDialog.OnTimeSetListener() {

						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
											  int minute) {

							final Time time = new Time(hourOfDay, minute);
							act.setTime(time);
							if (Setting.timeFormat24) {
								timeText.setText(time.toString());
							} else {
								final String[] timeString = time.toStringSperateAm();
								timeText.setText(timeString[0]);
								timeAmText.setText(timeString[1]);
							}
						}
					}, hr, min, Setting.timeFormat24);
			timePickerDialog.show();
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public void onDayToggle(View view) {

		final int day = Integer.parseInt(String.valueOf(view.getTag()));

		if (act.getDays()[day] == 1) {
			act.getDays()[day] = 0;
			((Button) view).setTextColor(C.getMyColor(this, R.color.colorWhite));
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				view.setBackgroundTintList(ColorStateList.valueOf(C.getMyColor(this, R.color.colorWhite)));
			} else {
				view.setBackgroundColor(R.color.colorWhite);
			}
		} else {
			act.getDays()[day] = 1;
			((Button) view).setTextColor(C.getMyColor(this, R.color.colorAccent));
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				view.setBackgroundTintList(ColorStateList.valueOf(C.getMyColor(this, R.color.colorAccent)));
			} else {
				view.setBackgroundColor(R.color.colorAccent);
			}
//			view.setBackgroundTintList(ColorStateList.valueOf(C.getMyColor(this, R.color.colorAccent)));
		}


	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	private void toggleDayButton(Button button, boolean on) {


		if (!on) {
			button.setTextColor(C.getMyColor(this, R.color.colorWhite));
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				button.setBackgroundTintList(ColorStateList.valueOf(C.getMyColor(this, R.color.colorWhite)));
			} else {
				button.setBackgroundColor(R.color.colorWhite);
			}
		} else {
			button.setTextColor(C.getMyColor(this, R.color.colorAccent));
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				button.setBackgroundTintList(ColorStateList.valueOf(C.getMyColor(this, R.color.colorAccent)));
			} else {
				button.setBackgroundColor(R.color.colorAccent);
			}
//			button.setBackgroundTintList(ColorStateList.valueOf(C.getMyColor(this, R.color.colorAccent)));
		}


	}


	@Override
	public boolean onLongClick(View v) {

		if (v == monB) {
			showShortToast("Monday");
		}
		if (v == tueB) {
			showShortToast("Tuesday");
		}
		if (v == wedB) {
			showShortToast("Wednesday");
		}
		if (v == thrB) {
			showShortToast("Thursday");
		}
		if (v == friB) {
			showShortToast("Friday");
		}
		if (v == satB) {
			showShortToast("Saturday");
		}
		if (v == sunB) {
			showShortToast("Sunday");
		}


		return true;
	}
}

