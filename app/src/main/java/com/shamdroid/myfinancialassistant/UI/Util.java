package com.shamdroid.myfinancialassistant.UI;

import android.content.Context;
import android.os.Build;

/**
 * Created by mohammad on 21/09/16.
 */

public class Util {

    public static int getColor(Context context, int res){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getResources().getColor(res,context.getTheme());
        }else{
            return context.getResources().getColor(res);
        }
    }

}
