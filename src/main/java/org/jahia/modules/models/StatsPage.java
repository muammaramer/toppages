package org.jahia.modules.models;

import java.util.Objects;

public class StatsPage implements Comparable<StatsPage> {

    private String url;
    private String title;
    private long viewCount;

    public StatsPage(String url, String title, long viewCount) {
        this.url = url;
        this.title = title;
        this.viewCount = viewCount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    @Override
    public int compareTo(StatsPage o) {
        return (int) (this.viewCount - o.viewCount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StatsPage)) return false;
        StatsPage statsPage = (StatsPage) o;
        return viewCount == statsPage.viewCount &&
                Objects.equals(url, statsPage.url) &&
                Objects.equals(title, statsPage.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, title, viewCount);
    }
}
