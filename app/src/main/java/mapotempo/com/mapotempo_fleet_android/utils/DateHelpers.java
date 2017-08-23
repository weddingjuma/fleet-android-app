package mapotempo.com.mapotempo_fleet_android.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelpers {

    public enum DateStyle {
        FULLDATE,
        SHORTDATE,
        HOURMINUTES
    }

    private enum Days {
        LUNDI(1, "Lundi"),
        MARDI(2, "Mardi"),
        MERCREDI(3, "Mercredi"),
        JEUDI(4, "Jeudi"),
        VENDREDI(5, "Vendredi"),
        SAMEDI(6, "Samedi"),
        DIMANCHE(7, "Dimanche");

        private String mVal;
        private int mIndex;

        Days(int index, String val) {
            mVal = val;
            mIndex = index;
        }

        public static String getStringFromIndex(int i) {
            String rlt = "";
            for (DateHelpers.Days d : DateHelpers.Days.values()) {
                if (d.getIndex() == i) {
                    rlt = d.toString();
                }
            }

            return rlt;
        }

        private int getIndex() {
            return mIndex;
        }

        @Override
        public String toString() {
            return mVal;
        }
    }

    private enum Months {

        JANVIER(1, "Janvier"),
        FEVRIER(2, "Février"),
        MARSE(3, "Mars"),
        AVRIL(4, "Avril"),
        MAI(5, "Mai"),
        JUIN(6, "Juin"),
        JUILLET(7, "Juillet"),
        AOUT(8, "Août"),
        SEPTEMBRE(9, "Septembre"),
        OCTOBRE(10, "Octobre"),
        NOVEMBRE(11, "Novembre"),
        DECEMBRE(12, "Décembre");

        private String mVal;
        private int mIndex;

        Months(int index, String val) {
            mVal = val;
            mIndex = index;
        }

        public static String getStringFromIndex(int i) {
            String rlt = "";
            for (DateHelpers.Months m : DateHelpers.Months.values()) {
                if (m.getIndex() == i) {
                    rlt = m.toString();
                }
            }

            return rlt;
        }

        public int getIndex() {
            return mIndex;
        }

        @Override
        public String toString() {
            return mVal;
        }
    }


    private DateHelpers() { }


    // #########################
    // FULL DATE PARSER
    // #########################
    private static String fullDateParsing(Date entryDate) {
        SimpleDateFormat formatter;
        String dayNumberInMonth;
        String month;
        String date;
        String day;

        formatter = new SimpleDateFormat("u");
        day = Days.getStringFromIndex( Integer.parseInt( formatter.format(entryDate) ) );

        formatter = new SimpleDateFormat("d");
        dayNumberInMonth = formatter.format(entryDate);

        formatter = new SimpleDateFormat("M");
        month = Months.getStringFromIndex( Integer.parseInt( formatter.format(entryDate) ) );

        formatter = new SimpleDateFormat("yyyy '-' hh':'mm");
        date = day + " " + dayNumberInMonth + ", " + month + " " + formatter.format(entryDate);

        return date;
    }

    private static String shortDateParsing(Date entryDate) {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("d'/'MM'/'yyyy");

        return formatter.format(entryDate);
    }

    private static String onlyHour(Date entryDate) {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("hh':'mm");

        return formatter.format(entryDate);
    }


    // #########################
    // Base function of parsing
    // #########################
    public static String parse(Date entryDate, DateStyle format) {
        String dateFormatted = "";

        switch (format) {
            case FULLDATE:
                dateFormatted = fullDateParsing(entryDate);
                break;

            case SHORTDATE:
                dateFormatted = shortDateParsing(entryDate);
                break;

            case HOURMINUTES:
                dateFormatted = onlyHour(entryDate);
                break;
        }

        return dateFormatted;
    }
}
