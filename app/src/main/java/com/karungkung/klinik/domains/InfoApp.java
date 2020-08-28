package com.karungkung.klinik.domains;

public class InfoApp {
    private String title;
    private String subtitle;
    private int icon;

    public InfoApp(String title, String subtitle, int icon) {
        this.title = title;
        this.subtitle = subtitle;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getIcon() {
        return icon;
    }
}
