package kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

import io.paperdb.Paper;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.R;
import kz.taxiplus.ysmaiylbokeikhan.taxiplus.entities.User;

public class Utility {
    public static void setTheme(Context context, int theme) {
        Paper.init(context);
        Paper.book().write(context.getString(R.string.prefs_theme_key), theme);
    }
    public static int getTheme(Context context) {
        Paper.init(context);
        return Paper.book().read(context.getString(R.string.prefs_theme_key), 1);
    }

    public static String getToken(Context context) {
        Paper.init(context);
        User user = Paper.book().read(Constants.USER);

        return user.getToken();
    }

    public static void dismissKeyboard(Activity activity){
        View v = activity.getWindow().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}