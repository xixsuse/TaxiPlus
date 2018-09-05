package kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils;

public class Application extends android.app.Application {
    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    private static boolean activityVisible = false;
}
