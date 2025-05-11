package com.example.crabfood.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeHelper {
    public static String formatOpeningHours(String openingTime, String closingTime) {
        try {
            // Định dạng gốc (giờ 24h)
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

            // Định dạng mong muốn (giờ 12h AM/PM)
            SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());

            // Parse & format
            Date openDate = inputFormat.parse(openingTime);
            Date closeDate = inputFormat.parse(closingTime);

            if (openDate != null && closeDate != null) {
                return "Giờ mở cửa: " + outputFormat.format(openDate) + " - " + outputFormat.format(closeDate);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "Giờ mở cửa: chưa biết";
    }
}
