package com.botxgames.sdule.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.botxgames.sdule.R;
import com.botxgames.sdule.Utilities.*;
import com.botxgames.sdule.db.TAct;
import com.botxgames.sdule.entities.Act;
import com.botxgames.sdule.entities.Time;

import java.util.Calendar;

public class AMain extends AppCompatActivity {

	private Typeface typeface;
	private Act[] actsToday, actsTomo;
	private int highlightedActIndex = -1;
	private TextView dayText;
	private static int currentday = -1;
	private LinearLayout todayActLayout, tomoActLayout, nextDayListLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_main);

		setTodaysReminders();

		Setting.timeFormat24 = ShrPref.readData(this, C.sp24FORMAT, false);
		Setting.reminder_before = ShrPref.readData(this, C.spREMINDER, 0);
		Setting.vibrate = ShrPref.readData(this, C.spVIBRATE, true);
		Setting.showTomo = ShrPref.readData(this, C.spNEXTDAY, true);
		Setting.showHourGlass = ShrPref.readData(this, C.spHOURGLASS, true);

		todayActLayout = (LinearLayout) findViewById(R.id.a_main_today_list_layout);
		tomoActLayout = (LinearLayout) findViewById(R.id.a_main_nextday_layout);
		nextDayListLayout = (LinearLayout) findViewById(R.id.a_main_nextday_list_layout);

		typeface = Typeface.createFromAsset(getAssets(), Setting.font);

		dayText = (TextView) findViewById(R.id.a_main_day_text);
		dayText.setTypeface(typeface);
		dayText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				final Dialog dialog = new Dialog(AMain.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.x_days_dialog);
				dialog.getWindow().setBackgroundDrawableResource(R.drawable.border);
				final ListView daysList = (ListView) dialog.findViewById(R.id.x_days_listview);
				daysList.setAdapter(new MyDaysListAdapter(AMain.this, dialog));
				dialog.show();

			}
		});

		final ImageView plus = (ImageView) findViewById(R.id.a_main_plus);
		plus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				final Intent intent = new Intent(AMain.this, AActEdit.class);
				intent.putExtra("act_id", -142);
				AMain.this.startActivity(intent);

			}
		});

		final ImageView settings = (ImageView) findViewById(R.id.a_main_setting);
		settings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				final Intent intent = new Intent(AMain.this, ASettings.class);
				AMain.this.startActivity(intent);

			}
		});


	}

	@Override
	protected void onResume() {
		super.onResume();

		if (currentday < 0) {
			refreshActs(Time.getDayToday() % 7);
		} else {
			refreshActs(currentday % 7);
		}

	}

	private void refreshActs(int dayToday) {

		currentday = dayToday;

		dayText.setText(Time.getDayString(dayToday));

		TAct tAct = new TAct(this);

		actsToday = tAct.getActsForDay(dayToday);

		if (actsToday != null) {

			// highlight the current activity based on time
			final Time timeNow = Time.getTimeNow();
			int minDiff = timeNow.getIntTime();

			for (int i = 0; i < actsToday.length; i++) {

				final Act act = actsToday[i];
				final int diff = timeNow.getIntTime() - act.getTime().getIntTime();

				if (diff >= 0) {

					if (diff < minDiff) {
						minDiff = diff;
						highlightedActIndex = i;
					}

				} else {
					break;
				}

			}
		}

		// reset and setup today's stuff
		fillActsInLayout(actsToday, todayActLayout, false);

		// fill up acts for tomorrow if opted
		if (!Setting.showTomo) {
			tomoActLayout.setVisibility(View.GONE);
			return;
		} else {
			tomoActLayout.setVisibility(View.VISIBLE);
		}

		final int nextDay = (currentday + 1) % 7;
		actsTomo = tAct.getActsForDay(nextDay);

		final TextView textView = (TextView) tomoActLayout.findViewById(R.id.a_main_nextday_text);
		textView.setTypeface(typeface);
		textView.setText(Time.getDayString(nextDay));

		fillActsInLayout(actsTomo, nextDayListLayout, true);




	}

	private void fillActsInLayout(final Act[] acts, LinearLayout layout, boolean nextDay) {

		layout.removeAllViews();

		for (int i = 0; i < acts.length; i++) {

			int layoutId;
			if(nextDay){
				layoutId = R.layout.x_activity_tomo;
			} else {
				layoutId = R.layout.x_activity;
			}
			final View view = getLayoutInflater().inflate(layoutId, null);

			final Act act = acts[i];

			final TextView timeText = (TextView) view.findViewById(R.id.x_time);
			final TextView textText = (TextView) view.findViewById(R.id.x_text);
			final ImageView alarmImg = (ImageView) view.findViewById(R.id.x_alarm);

			timeText.setText(act.getTime().toString());
			textText.setText(act.getText());

			timeText.setTypeface(typeface);
			textText.setTypeface(typeface);
			textText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);

			if(!nextDay) {
				final ImageView hourglassImg = (ImageView) view.findViewById(R.id.x_hour_glass);
				hourglassImg.setVisibility(View.GONE);
				if (highlightedActIndex == i) {
					textText.setTextColor(C.getMyColor(AMain.this, R.color.colorAccent));
					textText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 56);
					if(Setting.showHourGlass) hourglassImg.setVisibility(View.VISIBLE);
				}
			}

			alarmImg.setVisibility(act.isRemind() ? View.VISIBLE : View.GONE);

			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					final Intent intent = new Intent(AMain.this, AActEdit.class);
					intent.putExtra("act_id", act.getId());
					AMain.this.startActivity(intent);
				}
			});

			layout.addView(view);

		}
	}


	@Override
	public void onBackPressed() {

		AlertDialog dialog = new AlertDialog.Builder(this)
				.setPositiveButton("yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						AMain.this.finish();
					}
				})
				.setNegativeButton("no", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.create();
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setMessage("Confirm Exit?");
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
			dialog.getWindow().setBackgroundDrawableResource(R.drawable.border);
		}
		dialog.show();

		TextView textView = (TextView) dialog.findViewById(android.R.id.message);
		textView.setTypeface(typeface);
		textView.setTextSize(28);

		Button yesButton = (Button) dialog.getWindow().findViewById(android.R.id.button1);
		yesButton.setTextColor(C.getMyColor(this, R.color.colorRed));
		yesButton.setTypeface(typeface);

		Button noButton = (Button) dialog.getWindow().findViewById(android.R.id.button2);
		noButton.setTypeface(typeface);


	}

	private class MyDaysListAdapter extends ArrayAdapter<String> {

		private Dialog dialog;

		public MyDaysListAdapter(Context context, Dialog dialog) {
			super(context, -1, new String[]{
					"Monday",
					"Tuesday",
					"Wednesday",
					"Thursday",
					"Friday",
					"Saturday",
					"Sunday"
			});
			this.dialog = dialog;
		}

		@Override
		public View getView(final int position, View convertView, final ViewGroup parent) {

			final LayoutInflater inflater = AMain.this.getLayoutInflater();

			final View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
			final TextView textView = (TextView) view.findViewById(android.R.id.text1);
			textView.setTypeface(typeface);

			textView.setText(getItem(position));
			if (position == currentday) {
				textView.setTextColor(C.getMyColor(AMain.this, R.color.colorAccent));
			}

			textView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					refreshActs(position);
					if (dialog.isShowing()) {
						dialog.dismiss();
					}


				}
			});

			return view;
		}
	}

	/**
	 * Called whenever the app is launched
	 */
	private void setTodaysReminders(){

		final AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		final Intent intent = new Intent(this, NextdayRmdrSetter.class);
		final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 5);

		alarmManager.cancel(pendingIntent);
		alarmManager.setExact(
				AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(),
				pendingIntent
		);

	}

}
