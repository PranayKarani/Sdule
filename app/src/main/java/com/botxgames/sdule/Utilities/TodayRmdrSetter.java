// Created by pranay on 27/06/17.

package com.botxgames.sdule.Utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.botxgames.sdule.db.TAct;
import com.botxgames.sdule.entities.Act;
import com.botxgames.sdule.entities.Time;

public class TodayRmdrSetter extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		final TAct tAct = new TAct(context);
		final Act[] acts = tAct.getActsForDayWithReminders(Time.getDayToday());

		for (Act act : acts) {

			if (act.getTime().getIntTime() < Time.getTimeNow().getIntTime()) {
				continue;
			}

			act.setReminder(context);

		}
	}
}
