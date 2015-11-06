package barqsoft.footballscores.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.service.myFetchService;


public class WidgetProvider extends AppWidgetProvider {

    public static final String ITEM = "barqsoft.footballscores.widget.ITEM";
    public static final String ACTION = "barqsoft.footballscores.widget.ACTION";
    public static final String REFRESH = "barqsoft.footballscores.widget.REFRESH";

    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent.getAction().equals(ACTION)) {
            int matchId = intent.getIntExtra(ITEM, 0);
            Intent main = new Intent(context, MainActivity.class);
            main.putExtra(MainActivity.MATCH_DETAIL, matchId);
            main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(main);
        } else if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE) && intent.getAction().equals(REFRESH)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, WidgetProvider.class);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(componentName), R.id.list_view_test);
        }

        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {



        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String[] cDate = new String[1];
        cDate[0] = formatter.format(date);

        Log.d("WidgetProvider", "appWidgetIds.length" +  appWidgetIds.length);

        for (int i = 0; i < appWidgetIds.length; i++) {

            Log.d("WidgetProvider", "" + i + " ... " + appWidgetIds[i]);

            Intent intent = new Intent(context, WidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            Intent intentSync = new Intent(context, myFetchService.class);
            intentSync.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intentSync.putExtra(REFRESH, 0);
            PendingIntent pendingSync = PendingIntent.getService(context, 0, intentSync, 0);
            rv.setOnClickPendingIntent(R.id.reload_icon_view, pendingSync);

            rv.setTextViewText(R.id.header_view, context.getString(R.string.widget_scores));
            rv.setTextViewText(R.id.day_view, cDate[0]);

            Intent clickIntent = new Intent(context, WidgetProvider.class);
            clickIntent.setAction(WidgetProvider.ACTION);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            PendingIntent clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.list_view_test, clickPendingIntent);

            rv.setRemoteAdapter(R.id.list_view_test, intent);
            rv.setEmptyView(R.id.list_view_test, R.id.empty_view);
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
    }
}
