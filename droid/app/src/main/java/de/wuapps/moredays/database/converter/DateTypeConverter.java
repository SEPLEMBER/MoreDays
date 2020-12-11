package de.wuapps.moredays.database.converter;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTypeConverter {
    @TypeConverter
    public static long convertDateToLong(Date date) {
        return date.getTime();
    }

    @TypeConverter
    public static Date convertLongToDate(long time) {
        return new Date(time);
    }

    @TypeConverter
    public static String convertDateToString(Date date) {
        return (new SimpleDateFormat("yyMMdd").format(date));
    }

    @TypeConverter
    public static Date convertStringToDate(String strDate) {
        Date date = new Date();
        try {
            date = (new SimpleDateFormat("yyMMdd").parse(strDate));
        }
        catch(ParseException ex){
            //@todo log
        }
        return date;
    }
}
