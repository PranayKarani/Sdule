// Created by pranay on 26/06/17.

package com.botxgames.sdule.Utilities;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import com.botxgames.sdule.R;
import com.botxgames.sdule.db.TAct;
import com.botxgames.sdule.entities.Act;
import com.botxgames.sdule.entities.Time;

/**
 * This will be triggered at midnight and will set today's reminders
 */
public class NextdayRmdrSetter extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {

		final TAct tAct = new TAct(context);
		final Act[] acts = tAct.getActsForDayWithReminders(Time.getDayToday());

		for (Act act : acts) {

			act.setReminder(context);

		}

		NotificationCompat.Builder nb = new NotificationCompat.Builder(context)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentTitle("Sdule set for today")
				.setContentText(acts.length + " activities sduled");
		NotificationManager nM = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		nM.notify(1, nb.build());


	}


}
