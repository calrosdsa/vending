package com.tcn.sdk.springdemo.data;

import android.arch.persistence.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

public class Converters {
    @TypeConverter
    public LocalDateTime fromLocalDateTime(Long value) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(value),
                TimeZone.getDefault().toZoneId());
    }

    @TypeConverter
    public Long localDateTimeToLong(LocalDateTime datetime) {
        return datetime.toEpochSecond(OffsetDateTime.now().getOffset());
    }
}
