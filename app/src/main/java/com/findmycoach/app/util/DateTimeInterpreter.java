package com.findmycoach.app.util;

import java.util.Calendar;

/**
 * Created by praka_000 on 3/4/2015.
 */
public interface DateTimeInterpreter {
    String interpretDate(Calendar date);
    String interpretTime(int hour);
}
