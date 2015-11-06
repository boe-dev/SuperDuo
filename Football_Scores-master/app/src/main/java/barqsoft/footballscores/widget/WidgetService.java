package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;

public class WidgetService extends RemoteViewsService {

    int widgetId;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        Log.d("WidgetServcie", "onGetViewFactory");

        widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, AppWidgetManager.INVALID_APPWIDGET_ID);
        return new ListRemoteViewFactory(this.getApplicationContext(), intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    class ListRemoteViewFactory implements RemoteViewsFactory {

        private ArrayList<Matches> matchesList;
        private Context context;

        public ListRemoteViewFactory(Context applicationContext, Intent intent) {
            this.context = applicationContext;
        }

        @Override
        public void onCreate() {
            matchesList = new ArrayList<Matches>();

            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String[] cDate = new String[1];
            cDate[0] = formatter.format(date);

            Cursor cursor = getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(), null, null, cDate, null);

            Log.d("WidgetSerivce", "c.size" + cursor.getCount());

            if (cursor.moveToFirst()) {
                do {
                    Log.d("WidgetSerivce", "getString = " + cursor.getString(getResources().getInteger(R.integer.col_home)));
                    Matches matches = new Matches();
                    matches.setNameHome(cursor.getString(getResources().getInteger(R.integer.col_home)));
                    matches.setNameAway(cursor.getString(getResources().getInteger(R.integer.col_away)));
                    matches.setScoreHome(cursor.getString(getResources().getInteger(R.integer.col_home_goals)));
                    matches.setScoreAway(cursor.getString(getResources().getInteger(R.integer.col_away_goals)));
                    matches.setMatchId(cursor.getInt(getResources().getInteger(R.integer.col_id)));
                    matches.setData(cursor.getString(getResources().getInteger(R.integer.col_matchtime)));
                    matchesList.add(matches);
                } while (cursor.moveToNext());
            }
            cursor.close();

        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return matchesList.size();
        }

        @Override
        public RemoteViews getViewAt(int i) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_item);
            remoteViews.setTextViewText(R.id.widget_home_name, matchesList.get(i).getNameHome());
            String homeScores = (Integer.parseInt(matchesList.get(i).getScoreHome()) >= 0) ? matchesList.get(i).getScoreHome() : "";
            String awayScores = (Integer.parseInt(matchesList.get(i).getScoreAway()) >= 0) ? matchesList.get(i).getScoreAway() : "";
            remoteViews.setTextViewText(R.id.widget_score_text, homeScores + " - " + awayScores);
            remoteViews.setTextViewText(R.id.widget_away_name, matchesList.get(i).getNameAway());
            remoteViews.setTextViewText(R.id.widget_data_text, matchesList.get(i).getData());

            Bundle bundle = new Bundle();
            bundle.putInt(WidgetProvider.ITEM, matchesList.get(i).getMatchId());
            Intent intent = new Intent();
            intent.putExtras(bundle);
            remoteViews.setOnClickFillInIntent(R.id.widget_match_layout, intent);

            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }


}
