package com.example.crabfood.helpers;

import androidx.room.TypeConverter;

import com.example.crabfood.model.OptionChoiceResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptionChoiceConverters {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromOptionsMap(List<OptionChoiceResponse> options) {
        if (options == null) {
            return null;
        }
        return gson.toJson(options);
    }

    @TypeConverter
    public static List<OptionChoiceResponse> toOptionsMap(String optionsString) {
        if (optionsString == null) {
            return null;
        }
        Type type = new TypeToken<List<OptionChoiceResponse>>() {}.getType();
        return gson.fromJson(optionsString, type);
    }

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}