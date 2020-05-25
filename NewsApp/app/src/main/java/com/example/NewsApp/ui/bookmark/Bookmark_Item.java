package com.example.NewsApp.ui.bookmark;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Bookmark_Item {
    private String imageUrl;
    private String title;
    private String time;
    private String section;
    private String id;

    public Bookmark_Item(String imageUrl, String title, String time, String section, String id) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.section = section;
        this.id = id;
        this.time = time;

        ZonedDateTime zonedDateTimeInUTC = ZonedDateTime.parse(time.substring(0, 19)+"+00:00");
        ZonedDateTime zonedDateTimeInPST = zonedDateTimeInUTC.withZoneSameInstant(ZoneId.of("America/Los_Angeles"));
        this.time = time.substring(5, 7) + " " + zonedDateTimeInPST.getMonth().getDisplayName(TextStyle.SHORT, Locale.US);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getSection() {
        return section;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
