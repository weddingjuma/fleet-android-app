package com.mapotempo.lib.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.IllegalFormatException;

public class DateHelpers {

    public enum DateStyle {
        FULLDATE,
        SHORTDATE,
        HOURMINUTES
    }

    private enum Days {
        MONDAY("Monday", "Lundi"),
        TUESDAY("Tuesday", "Mardi"),
        WEDNESDAY("Wednesday", "Mercredi"),
        THURSDAY("Thursday", "Jeudi"),
        FRIDAY("Friday", "Vendredi"),
        SATURDAY("Saturday", "Samedi"),
        SUNDAY("Sunday", "Dimanche");

        private String mEnglishTag;

        private String mFrenchTag;

        Days(String englishTag, String frenchTag) {
          mEnglishTag = englishTag;
          mFrenchTag = frenchTag;
        }

        public static Days fromEnglishString(String englishTag) {
          Days day = MONDAY;
          try
          {
            day = Days.valueOf(englishTag.toUpperCase());
          } catch (IllegalFormatException e) {
            e.printStackTrace();
          } finally
          {
            return day;
          }
        }

        public String getEnglishString()
        {
          return mEnglishTag;
        }

        public String getFrenchString()
        {
          return mFrenchTag;
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

        formatter = new SimpleDateFormat("EEEE");
        //day = Days.fromEnglishString( formatter.format(entryDate) ).getFrenchString();
        day = formatter.format(entryDate);
        // Uppecase the first char
        if(day != null)
            day = day.substring(0,1).toUpperCase() + day.substring(1).toLowerCase();

        formatter = new SimpleDateFormat("d");
        dayNumberInMonth = formatter.format(entryDate);

        formatter = new SimpleDateFormat("M");
        month = Months.getStringFromIndex( Integer.parseInt( formatter.format(entryDate) ) );

        formatter = new SimpleDateFormat("yyyy '-' kk':'mm");
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
        formatter = new SimpleDateFormat("kk':'mm");

        return formatter.format(entryDate);
    }

    /**
     * Parse a date depending on the format wanted.
     * @param entryDate The date to be parsed
     * @param format FULLDATE | SHORTDATE | HOURMINUTES
     * @return
     */
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

    /**
     * Build an array containing the date min and max from today's date.
     * @param timeToAdd number of day to add from current date
     * @return An array that contain 2 Date object, before(0) and after(1).
     */
    public static Date[] generateTimeWindowDate(int timeToAdd) {
        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(currentDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        currentDate = calendar.getTime();

        calendar.add(calendar.DATE, timeToAdd);
        Date furtherDate = calendar.getTime();

        return new Date[] { currentDate, furtherDate };
    }
}
