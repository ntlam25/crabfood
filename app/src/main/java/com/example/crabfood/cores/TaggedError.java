package com.example.crabfood.cores;

import com.example.crabfood.cores.enums.ErrorSource;

public class TaggedError {
    public final ErrorSource source;
    public final String message;

    public TaggedError(ErrorSource source, String message) {
        this.source = source;
        this.message = message;
    }
}
