package cz.muni.irtis.datacollector.metrics;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import cz.muni.irtis.datacollector.metrics.condition.IsScreenOn;
import cz.muni.irtis.datacollector.schedule.Metric;

public class Runtime extends Metric {
    private long tStart = -1;

    public Runtime(Context context, Object... params) {
        super(context, params);
        addPrerequisity(new IsScreenOn());
    }

    @Override
    public void run() {
        if (!isPrerequisitiesSatisfied())
            return;

        if (tStart < 0) {
            tStart = System.currentTimeMillis();
        } else {
            broadcastElapsedTime();
        }
    }

    @Override
    public void stop() {
        super.stop();
        tStart = -1;
    }

    private void broadcastElapsedTime() {
        String elapsed = calculateElapsedTime();
        Intent intent = new Intent("elapsed_time");
        intent.putExtra("elapsed", elapsed);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }

    private String calculateElapsedTime() {
        long tEnd = System.currentTimeMillis();
        long elapsedMilis = tEnd - tStart;

        int seconds = (int) (elapsedMilis / 1000);
        int minutes = (int) (seconds / 60);
        int hours = (int) (minutes / 60);

        return String.format("%d:%d:%d", hours % 59, minutes % 59, seconds % 59);
    }
}