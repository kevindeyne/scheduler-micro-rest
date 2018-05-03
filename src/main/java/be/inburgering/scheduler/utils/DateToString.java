package be.inburgering.scheduler.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public enum DateToString {

    INSTANCE;

    public String format(Date date){
        LocalDateTime local = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return local.format(formatter);
    }
}
