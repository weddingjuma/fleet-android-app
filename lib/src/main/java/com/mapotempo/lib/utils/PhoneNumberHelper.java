package com.mapotempo.lib.utils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneNumberHelper {

    public static String intenationalPhoneNumber(String phone) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber frenchNumberProto = phoneUtil.parse(phone, "FR");
            return "+" + frenchNumberProto.getCountryCode() + frenchNumberProto.getNationalNumber();
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
            return phone;
        }
    }
}
