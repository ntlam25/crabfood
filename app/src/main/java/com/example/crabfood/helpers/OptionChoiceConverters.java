package com.example.crabfood.helpers;

import androidx.room.TypeConverter;

import com.example.crabfood.model.OptionChoiceResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptionChoiceConverters {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromOptionsMap(Map<Long, List<OptionChoiceResponse>> options) {
        if (options == null) {
            return null;
        }
        return gson.toJson(options);
    }

    @TypeConverter
    public static Map<Long, List<OptionChoiceResponse>> toOptionsMap(String optionsString) {
        if (optionsString == null) {
            return null;
        }
        Type type = new TypeToken<HashMap<Long, List<OptionChoiceResponse>>>() {}.getType();
        return gson.fromJson(optionsString, type);
    }
}