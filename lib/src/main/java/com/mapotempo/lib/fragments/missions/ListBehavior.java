package com.mapotempo.lib.fragments.missions;

public enum ListBehavior {
    FOCUS("FOCUS"),
    SIMPLE("SIMPLE");

    final public String mStringType;

    ListBehavior(String stringType) {
        mStringType = stringType;
    }

    public static ListBehavior fromString(String stringType) {
        return valueOf(stringType.toUpperCase());
    }

    public static ListBehavior fromInteger(int intType) {
        return ListBehavior.values()[intType];
    }
}
