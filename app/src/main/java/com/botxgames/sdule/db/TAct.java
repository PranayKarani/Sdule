// Created by pranay on 13/06/17.

package com.botxgames.sdule.db;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import com.botxgames.sdule.Utilities.TodayRmdrSetter;
import com.botxgames.sdule.activities.ASettings;
import com.botxgames.sdule.entities.Act;
import com.botxgames.sdule.entities.Time;

public class TAct {

	public static final String TABLE_NAME = "Act";
	public static final String ID = "_ID";
	public static final String TIME = "act_datetime";
	public static final String TEXT = "act_text";
	public static final String MON = "act_0";
	public static final String TUE = "act_1";
	public static final String WED = "act_2";
	public static final String THR = "act_3";
	public static final String FRI = "act_4";
	public static final String SAT = "act_5";
	public static final String SUN = "act_6";
	public static final String REMIND = "act_remind";

	private DBHelper dbHelper;

	private Context context;

	/* Query Strings */
	static String q_CREATE_TABLE() {
		return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
				TIME + " INTEGER," +
				TEXT + " TEXT," +
				MON + " TEXT," +
				TUE + " TEXT," +
				WED + " TEXT," +
				THR + " TEXT," +
				FRI + " TEXT," +
				SAT + " TEXT," +
				SUN + " TEXT," +
				REMIND + " INTEGER" +
				")";
	}

	private String SELECT_FROM_TABLE = "SELECT * FROM " + TABLE_NAME;

	private String q_SELECT_ACT(int actId) {
		return SELECT_FROM_TABLE + " WHERE " + ID + " = " + actId;
	}

	private String q_SELECT_ACTS_FOR_DAY(int day) {

		return SELECT_FROM_TABLE + " WHERE act_" + day + " > 0 ORDER BY " + TIME + " ASC";
	}

	private String q_SELECT_ACTS_FOR_DAY_WITH_REMINDERS(int day){
		return SELECT_FROM_TABLE + " WHERE act_" + day + " > 0 AND " + REMIND + " > 0 ORDER BY " + TIME + " ASC";
	}


	public TAct(Context context) {
		this.context = context;
		dbHelper = new DBHelper(context);
	}



	public long insertNewAct(Act act) {


		final ContentValues cv = new ContentValues();
		cv.put(TIME, act.getTime().getIntTime());
		cv.put(TEXT, act.getText());
		cv.put(MON, act.getDays()[0]);
		cv.put(TUE, act.getDays()[1]);
		cv.put(WED, act.getDays()[2]);
		cv.put(THR, act.getDays()[3]);
		cv.put(FRI, act.getDays()[4]);
		cv.put(SAT, act.getDays()[5]);
		cv.put(SUN, act.getDays()[6]);

		cv.put(REMIND, act.isRemind()? 1 : 0);
		return dbHelper.insert(TABLE_NAME, cv);

	}

	private Act extractActFromCursor(Cursor c) {

		final int id = c.getInt(c.getColumnIndex(ID));
		final int time = c.getInt(c.getColumnIndex(TIME));
		final String text = c.getString(c.getColumnIndex(TEXT));
		final int remind = c.getInt(c.getColumnIndex(REMIND));

		final int[] ds = new int[7];

		ds[0] = c.getInt(c.getColumnIndex(MON));
		ds[1] = c.getInt(c.getColumnIndex(TUE));
		ds[2] = c.getInt(c.getColumnIndex(WED));
		ds[3] = c.getInt(c.getColumnIndex(THR));
		ds[4] = c.getInt(c.getColumnIndex(FRI));
		ds[5] = c.getInt(c.getColumnIndex(SAT));
		ds[6] = c.getInt(c.getColumnIndex(SUN));

		return new Act(id, new Time(time), text, remind > 0, ds);

	}

	public Act getAct(int actId) {

		final Cursor c = dbHelper.select(q_SELECT_ACT(actId), null);

		if (c.moveToFirst()) {
			return extractActFromCursor(c);
		} else {
			return null;
		}
	}

	public Act[] getActsForDay(int day) {

		final Cursor c = dbHelper.select(q_SELECT_ACTS_FOR_DAY(day), null);

		final Act[] acts = new Act[c.getCount()];

		while (c.moveToNext()) {

			final int pos = c.getPosition();
			acts[pos] = extractActFromCursor(c);

		}

		return acts;

	}

	public Act[] getActsForDayWithReminders(int day){


		final Cursor c = dbHelper.select(q_SELECT_ACTS_FOR_DAY_WITH_REMINDERS(day), null);

		final Act[] acts = new Act[c.getCount()];

		while (c.moveToNext()) {

			final int pos = c.getPosition();
			acts[pos] = extractActFromCursor(c);

		}

		return acts;


	}

	public void update(Act act) {

		final ContentValues cv = new ContentValues();

		cv.put(TIME, act.getTime().getIntTime());
		cv.put(TEXT, act.getText());
		cv.put(MON, act.getDays()[0]);
		cv.put(TUE, act.getDays()[1]);
		cv.put(WED, act.getDays()[2]);
		cv.put(THR, act.getDays()[3]);
		cv.put(FRI, act.getDays()[4]);
		cv.put(SAT, act.getDays()[5]);
		cv.put(SUN, act.getDays()[6]);
		cv.put(REMIND, act.isRemind());

		dbHelper.update(TABLE_NAME, cv, ID + " = ?", new String[]{String.valueOf(act.getId())});

	}

	public void delete(int actId) {

		dbHelper.delete(TABLE_NAME, ID + " = ?", new String[]{
				String.valueOf(actId)
		});

		// resdule the today's reminders if an activity is deleted33
		final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		final Intent intent = new Intent(context, TodayRmdrSetter.class);
		final PendingIntent pendingIntent = PendingIntent.getBroadcast(
				context,
				0,
				intent,
				PendingIntent.FLAG_ONE_SHOT);

		alarmManager.cancel(pendingIntent);
		alarmManager.setExact(
				AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + 50,
				pendingIntent
		);

	}
}
