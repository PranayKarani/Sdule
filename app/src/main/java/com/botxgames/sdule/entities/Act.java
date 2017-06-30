// Created by pranay on 13/06/17.

package com.botxgames.sdule.entities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.botxgames.sdule.Utilities.C;
import com.botxgames.sdule.Utilities.Setting;
import com.botxgames.sdule.Utilities.SingleReminderReciever;

import java.util.Date;

public class Act {

	private int id;
	private Time time;
	private String text;
	private boolean remind;
	private int[] days = new int[7];// 0 - no, >0 - yes

	public Act(int id, Time time, String text, boolean remind, int[] days) {
		this.id = id;
		this.time = time;
		this.text = text;
		this.remind = remind;
		this.days = days;
	}

	public int getId(){
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isRemind() {
		return remind;
	}

	public void setRemind(boolean remind) {
		this.remind = remind;
	}

	public int[] getDays() {
		return days;
	}

	public String getDaysToString(){
		StringBuilder builder = new StringBuilder();
		for (int i : days) {
			builder.append(i);
		}
		return builder.toString();
	}

	public boolean isDaysEmpty(){

		boolean isEmpty = true;

		for (int i : days) {
			if (i == 1) {
				isEmpty = false;
				return isEmpty;
			}
		}

		return isEmpty;

	}

	public void setDays(int[] days) {
		this.days = days;
	}

	public void setDayValue(int index, int value) {
		days[index] = value;
	}

	public int getDayValue(int index) {
		return days[index];
	}

	@Override
	public String toString() {
		return time + "\n" + text + "\n" + remind + "\n" + getDaysToString();
	}

	public void setReminder(Context context){

		final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		final Intent in = new Intent(context, SingleReminderReciever.class);

		in.putExtra("act", C.getNoticeText(this));
		in.putExtra("act_time", "at " + getTime().toString());
		final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, getId(), in, PendingIntent.FLAG_ONE_SHOT);

		final long rmdrTime = getTime().getTimeBefore(Setting.reminder_before).getTimeInLong();

		alarmManager.cancel(pendingIntent);
		alarmManager.setExact(
				AlarmManager.RTC_WAKEUP,
				rmdrTime,
				pendingIntent
		);

	}

}
