// Created by pranay on 13/06/17.

package com.botxgames.sdule.entities;

import android.util.Log;
import com.botxgames.sdule.Utilities.Setting;

import java.util.Calendar;

public class Time {

	private int hour;
	private int min;

	public Time(int hr, int min) {
		this.hour = hr;// store in 24 hour format
		this.min = min;
	}

	public Time(int time) {
		this.hour = Math.round(time / 100);
		this.min = time - this.hour * 100;
	}

	public int getIntTime() {
		return hour * 100 + min;
	}

	@Override
	public String toString() {

		if (Setting.timeFormat24) {

			return hour + ":" + (min < 10 ? "0" + min : min);

		} else {

			int hours = hour;
			int mins = min;

			String am = (hours >= 12) ? "pm" : "am";

			if(hours > 12){
				hours -= 12;
			} else {
				if(hours == 0){
					hours = 12;
				}
			}

			return hours + ":" + (mins < 10 ? "0" + mins : mins) + " " + am;

		}

	}

	public String[] toStringSperateAm() {

		if (Setting.timeFormat24) {
			return null;
		}

		int hours = hour;
		int mins = min;

		String am = (hours >= 12) ? "pm" : "am";

		if(hours > 12){
			hours -= 12;
		} else {
			if(hours == 0){
				hours = 12;
			}
		}
//		hours = (hours > 12) ? hours - 12 : hours;

		return new String[]{hours + ":" + (mins < 10 ? "0" + mins : mins), am};

	}

	public static Time getTimeNow() {
		// Get Current Time
		final Calendar c = Calendar.getInstance();
		final int mHour = c.get(Calendar.HOUR_OF_DAY);
		final int mMinute = c.get(Calendar.MINUTE);

		return new Time(mHour, mMinute);

	}

	public static int getDayToday() {

		final Calendar c = Calendar.getInstance();
		switch (c.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.MONDAY:
				return 0;
			case Calendar.TUESDAY:
				return 1;
			case Calendar.WEDNESDAY:
				return 2;
			case Calendar.THURSDAY:
				return 3;
			case Calendar.FRIDAY:
				return 4;
			case Calendar.SATURDAY:
				return 5;
			default:
				return 6;

		}

	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public Time getTimeBefore(int mins) {

		if (min >= mins) {

			return new Time(hour, min - mins);

		} else {

			return new Time(hour - 1, (60 + min) - mins);

		}

	}

	public long getTimeInLong() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		final int h = hour;
		final int m = min;
		calendar.set(Calendar.HOUR_OF_DAY, h);// 10
		calendar.set(Calendar.MINUTE, m);// 50
		Log.i("TAAAAAAAG", "hour: " + h + ", min: " + m);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTimeInMillis();
	}

	public static String getDayString(int day) {

		switch (day) {

			case 0:
				return "Monday";
			case 1:
				return "Tuesday";
			case 2:
				return "Wednesday";
			case 3:
				return "Thursday";
			case 4:
				return "Friday";
			case 5:
				return "Saturday";
			default:
				return "Sunday";

		}

	}

	public int[] timeDiff(Time toTime, Time fromTime) {

		final int[] timeDiff = new int[2];

		int hr = 0, min = 0;

		if (fromTime.hour < toTime.hour) {
			Log.e("ERROR", "from cannot be less than to");
			return null;
		} else if (fromTime.hour == toTime.hour) {

			if (fromTime.min < toTime.hour) {
				Log.e("ERROR", "from cannot be less than to");
				return null;
			}

			hr = 0;
			min = fromTime.min - toTime.min;


		} else {

			hr = fromTime.hour - toTime.hour;
			min = fromTime.min - toTime.min;

			if (min < 0) {
				hr--;
				min = 60 + min;
			}

		}

		return new int[]{hr, min};

	}


}
