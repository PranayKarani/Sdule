// Created by pranay on 13/06/17.

package com.botxgames.sdule.Utilities;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import com.botxgames.sdule.entities.Act;

public class C {

	// SharedPreferences Constants
	public static final String spFILE_NAME = "sdule_sp_file";
	public static final String sp24FORMAT = "format_24";
	public static final String spREMINDER = "remind_before";
	public static final String spVIBRATE = "vibrate";
	public static final String spNEXTDAY = "next_day";
	public static final String spHOURGLASS = "hour_glass";

//	public static final int AMAIN_CALLEE = 24123;
//	public static final int AEDIT_CALLEE = 3452;

	public static int getMyColor(Context context, int color) {
		return ContextCompat.getColor(context, color);
	}

	public static String getNoticeText(Act act) {

		String actText = act.getText();
		if (Setting.reminder_before == 0) {
			actText = actText;// just a formality, don't change this line
		} else if (Setting.reminder_before == 1) {
			actText = actText + " in " + Setting.reminder_before + " minute.";
		} else {
			actText = actText + " in " + Setting.reminder_before + " minutes.";
		}
		return actText;

	}
}
