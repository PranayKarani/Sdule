package com.botxgames.sdule.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.botxgames.sdule.R;
import com.botxgames.sdule.Utilities.C;
import com.botxgames.sdule.Utilities.Setting;
import com.botxgames.sdule.Utilities.ShrPref;
import com.botxgames.sdule.db.TAct;
import com.botxgames.sdule.entities.Act;
import com.botxgames.sdule.entities.Time;
import org.w3c.dom.Text;

public class AMain extends AppCompatActivity {

	private Typeface typeface;
	private Act[] actsToday, actsTomo;
	private int highlightedActIndex = -1;
	private TextView dayText;
	private static int currentday = -1;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_main);

		Setting.timeFormat24 = ShrPref.readData(this, C.sp24FORMAT, false);
		Setting.reminder_before = ShrPref.readData(this, C.spREMINDER, 0);
		Setting.vibrate = ShrPref.readData(this, C.spVIBRATE, true);

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

		// fill up acts for tomorrow if opted
		if (Setting.showTomo) {

			actsTomo = tAct.getActsForDay((currentday + 1)%7);


		}


		final ListView lv = (ListView) findViewById(R.id.a_main_list);
		lv.setAdapter(new MyAdapter(this, actsToday));

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

	private class MyAdapter extends ArrayAdapter<Act> {

		MyAdapter(Context context, Act[] objects) {
			super(context, -1, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final LayoutInflater inflater = AMain.this.getLayoutInflater();

			final View view = inflater.inflate(R.layout.x_activity, parent, false);

			final Act act = getItem(position);

			final TextView timeText = (TextView) view.findViewById(R.id.x_time);
			final TextView textText = (TextView) view.findViewById(R.id.x_text);
			final ImageView alarmImg = (ImageView) view.findViewById(R.id.x_alarm);

			timeText.setText(act.getTime().toString());
			textText.setText(act.getText());

			timeText.setTypeface(typeface);
			textText.setTypeface(typeface);

			final ImageView hourglassImg = (ImageView) view.findViewById(R.id.x_hour_glass);
			hourglassImg.setVisibility(View.GONE);
			if (highlightedActIndex == position) {
				textText.setTextColor(C.getMyColor(AMain.this, R.color.colorAccent));
				hourglassImg.setVisibility(View.VISIBLE);
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

			return view;
		}
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


}
