package com.example.opportunity;

public class ScreenItem {
    String screenItemTitle;
    String screenItemDescription;
    int screenItemImage;

    public ScreenItem(String screenItemTitle, String screenItemDescription, int screenItemImage) {
        this.screenItemTitle = screenItemTitle;
        this.screenItemDescription = screenItemDescription;
        this.screenItemImage = screenItemImage;
    }

    public String getScreenItemTitle() {
        return screenItemTitle;
    }

    public void setScreenItemTitle(String screenItemTitle) {
        this.screenItemTitle = screenItemTitle;
    }

    public String getScreenItemDescription() {
        return screenItemDescription;
    }

    public void setScreenItemDescription(String screenItemDescription) {
        this.screenItemDescription = screenItemDescription;
    }

    public int getScreenItemImage() {
        return screenItemImage;
    }

    public void setScreenItemImage(int screenItemImage) {
        this.screenItemImage = screenItemImage;
    }
}
