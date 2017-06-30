// Created by pranay on 14/06/17.

package com.botxgames.sdule.Utilities;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.botxgames.sdule.R;

public class SingleReminderReciever extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("tag", "Time for " + intent.getStringExtra("act"));

		final String actText = intent.getStringExtra("act");
		final String actTime = intent.getStringExtra("act_time");

		NotificationCompat.Builder nb = new NotificationCompat.Builder(context)
				.setSmallIcon(R.mipmap.ic_launcher)
				.setContentTitle(actText)
				.setContentText(actTime);
		NotificationManager nM = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		nM.notify(1,nb.build());

		try {
			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(context, notification);
			r.play();
			if(Setting.vibrate) {
				Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(100);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}



	}
}
