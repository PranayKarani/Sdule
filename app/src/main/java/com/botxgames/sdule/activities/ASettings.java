package com.botxgames.sdule.activities;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.botxgames.sdule.R;
import com.botxgames.sdule.Utilities.*;

public class ASettings extends MyBaseActivity implements View.OnClickListener{

	private Switch switch24, vibrateSwitch, nextdaySwitch, hourGlassSwitch;
	private Typeface typeface;
	private TextView reminderValue;

	int[] rmdrTms = {0, 1, 5, 10, 15, 30};
	String[] rmdrTxts = new String[]{
			"At the time of Activity",
			"1 min before",
			"5 mins before",
			"10 mins before",
			"15 mins before",
			"30 mins before"
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_settings);

		typeface = Typeface.createFromAsset(getAssets(), Setting.font);

		switch24 = (Switch) findViewById(R.id.a_settings_24);
		switch24.setChecked(Setting.timeFormat24);
		switch24.setOnClickListener(this);
		switch24.setTypeface(typeface);

		nextdaySwitch = (Switch) findViewById(R.id.a_settings_nextday);
		nextdaySwitch.setChecked(Setting.showTomo);
		nextdaySwitch.setOnClickListener(this);
		nextdaySwitch.setTypeface(typeface);

		hourGlassSwitch = (Switch) findViewById(R.id.a_settings_hourglass);
		hourGlassSwitch.setChecked(Setting.showHourGlass);
		hourGlassSwitch.setOnClickListener(this);
		hourGlassSwitch.setTypeface(typeface);

		final TextView reminderHeader = (TextView) findViewById(R.id.a_settings_reminder_heading);
		reminderHeader.setTypeface(typeface);
		reminderValue = (TextView) findViewById(R.id.a_settings_reminder_text);
		reminderValue.setTypeface(typeface);
		for(int i = 0; i < rmdrTms.length; i++) {
			if (Setting.reminder_before == rmdrTms[i]) {
				reminderValue.setText(rmdrTxts[i]);
				break;
			}
		}

		final LinearLayout reminderSettingLayout = (LinearLayout) findViewById(R.id.a_settings_reminder);
		reminderSettingLayout.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {

				final Dialog dialog = new Dialog(ASettings.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.x_remind_before);
				dialog.getWindow().setBackgroundDrawableResource(R.drawable.border);
				final ListView daysList = (ListView) dialog.findViewById(R.id.x_reminder_times_listview);
				daysList.setAdapter(new ASettings.MyReminderListAdapter(dialog));
				dialog.show();

			}
		});

		vibrateSwitch = (Switch) findViewById(R.id.a_settings_vibrate);
		vibrateSwitch.setOnClickListener(this);
		vibrateSwitch.setChecked(Setting.vibrate);
		vibrateSwitch.setTypeface(typeface);

		final TextView text = (TextView) findViewById(R.id.a_settings_text);
		text.setTypeface(typeface);

		final TextView feedbackText = (TextView) findViewById(R.id.a_settings_feedback);
		feedbackText.setTypeface(typeface);
		feedbackText.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				final String[] TO = {"pranaykarani@outlook.com"};
				Intent emailIntent = new Intent(Intent.ACTION_SEND);

				emailIntent.setData(Uri.parse("mailto:"));
				emailIntent.setType("text/plain");
				emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Sdule feedback");

				try {
					startActivity(Intent.createChooser(emailIntent, "Send Email"));
				} catch (android.content.ActivityNotFoundException ex) {
					showLongToast("There is no email client installed.");
				}
			}
		});

		final TextView aboutText = (TextView) findViewById(R.id.a_settings_about);
		aboutText.setTypeface(typeface);


	}

	@Override
	protected void onPause() {
		super.onPause();

		ShrPref.writeData(this, C.sp24FORMAT, Setting.timeFormat24);
		ShrPref.writeData(this, C.spREMINDER, Setting.reminder_before);
		ShrPref.writeData(this, C.spVIBRATE, Setting.vibrate);
		ShrPref.writeData(this, C.spNEXTDAY, Setting.showTomo);
		ShrPref.writeData(this, C.spHOURGLASS, Setting.showHourGlass);

	}

	@Override
	public void onClick(View v) {

		if(v == switch24){
			Setting.timeFormat24 = !Setting.timeFormat24;
			switch24.setChecked(Setting.timeFormat24);
		}

		if (v == vibrateSwitch) {
			Setting.vibrate = !Setting.vibrate;
			vibrateSwitch.setChecked(Setting.vibrate);
		}

		if (v == nextdaySwitch) {
			Setting.showTomo = !Setting.showTomo;
			nextdaySwitch.setChecked(Setting.showTomo);
		}

		if(v == hourGlassSwitch){
			Setting.showHourGlass = !Setting.showHourGlass;
			hourGlassSwitch.setChecked(Setting.showHourGlass);
		}

	}

	private class MyReminderListAdapter extends ArrayAdapter<String> {


		final Dialog dialog;

		public MyReminderListAdapter(Dialog dialog) {
			super(ASettings.this, -1, rmdrTxts);

			this.dialog = dialog;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			final LayoutInflater inflater = ASettings.this.getLayoutInflater();

			final View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
			final TextView textView = (TextView) view.findViewById(android.R.id.text1);
			textView.setTypeface(typeface);
			textView.setText(getItem(position));
			textView.setTextColor(C.getMyColor(ASettings.this, R.color.colorWhite));

			if (Setting.reminder_before == rmdrTms[position]) {
				textView.setTextColor(C.getMyColor(ASettings.this, R.color.colorAccent));
			}

			textView.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {
					Setting.reminder_before = rmdrTms[position];
					reminderValue.setText(getItem(position));
					if (dialog.isShowing()) {
						dialog.dismiss();

						// update the reminder pending intents
						final AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
						final Intent intent = new Intent(ASettings.this, TodayRmdrSetter.class);
						final PendingIntent pendingIntent = PendingIntent.getBroadcast(
								ASettings.this,
								0,
								intent,
								PendingIntent.FLAG_ONE_SHOT);

						alarmManager.cancel(pendingIntent);
						alarmManager.setExact(
								AlarmManager.RTC_WAKEUP,
								System.currentTimeMillis(),
								pendingIntent
						);

					}
				}
			});

			return view;
		}
	}

}
