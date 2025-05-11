package com.example.crabfood.helpers;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringHelper {

    public static String removeDiacritics(String input) {
        // Chuẩn hóa Unicode dạng NFD (Normalization Form Decomposition)
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

        // Loại bỏ các ký tự dấu (có mã Unicode trong nhóm Mn - Mark, Nonspacing)
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }
}

