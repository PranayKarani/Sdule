// Created by pranay on 13/06/17.

package com.botxgames.sdule.Utilities;

import android.content.Context;
import android.support.v4.content.ContextCompat;

public class C {

	// SharedPreferences Constants
	public static final String spFILE_NAME = "sdule_sp_file";
	public static final String sp24FORMAT = "format_24";
	public static final String spREMINDER = "remind_before";
	public static final String spVIBRATE = "vibrate";
	public static final String spNEXTDAY = "next_day";

	public static int getMyColor(Context context, int color) {
		return ContextCompat.getColor(context, color);
	}

}
