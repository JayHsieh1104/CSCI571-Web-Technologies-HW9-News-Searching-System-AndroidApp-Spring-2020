package com.example.NewsApp.ui.search;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

public class News_Item {
    private String imageUrl;
    private String title;
    private String time;
    private String originalTime;
    private String section;
    private String id;

    public News_Item(String imageUrl, String title, String time, String section, String id) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.section = section;
        this.id = id;
        this.originalTime = time;

        ZonedDateTime From = ZonedDateTime.parse(time.substring(0, 19)+"+00:00");
        ZonedDateTime To = ZonedDateTime.now( ZoneId.of("UTC") );
        Duration duration = Duration.between(From, To);
        long duration_second = TimeUnit.MILLISECONDS.toSeconds(duration.toMillis());
        if(duration_second < 60) {
            this.time = duration_second + "s ago";
        }
        else if(duration_second < 3600) {
            this.time = (int) (duration_second/60) + "m ago";
        }
        else if(duration_second < 86400) {
            this.time = (int) (duration_second/ 3660) + "h ago";
        }
        else {
            this.time = (int) (duration_second / 86400) + "d ago";
        }
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

    public String getOriginalTime() {
        return originalTime;
    }
}
