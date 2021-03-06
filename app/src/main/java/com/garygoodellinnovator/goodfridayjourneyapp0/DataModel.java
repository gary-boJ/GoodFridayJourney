package com.garygoodellinnovator.goodfridayjourneyapp0;

public class DataModel {
    String name;
    String timeOfDay;
    Integer icon;
    Integer picture;
    Integer sound;
    String heading;
    String scripture;
    String prayer;
    String snackbar;
    Integer _id;

    public DataModel(String name, String timeOfDay, Integer icon,
                     Integer picture, Integer sound,
                     String heading, String scripture, String prayer,
                     String snackbar, Integer _id) {
        this.name = name;
        this.timeOfDay = timeOfDay;
        this.icon = icon;
        this.picture = picture;
        this.sound = sound;
        this.heading = heading;
        this.scripture = scripture;
        this.prayer = prayer;
        this.snackbar = snackbar;
        this._id = _id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }
    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public Integer get_id() {
        return _id;
    }
    public void set_id(Integer _id) {
        this._id = _id;
    }

    public Integer getIcon() { return icon; }
    public void setIcon(Integer image) { this.icon  = icon; }

    public Integer getPicture() { return picture; }
    public void setPicture(Integer picture) { this.picture = picture; }

    public Integer getSound() { return sound; }
    public void setSound(Integer sound) { this.sound = sound; }

    public String getHeading() { return heading; }
    public void setHeading(String heading) { this.heading = heading; }

    public String getScripture() { return scripture; }
    public void setScripture(String scripture) { this.scripture = scripture; }

    public String getPrayer() { return prayer; }
    public void setPrayer(String prayer) { this.prayer = prayer; }

    public String getSnackbar() { return snackbar; }
    public void setSnackbar(String snackbar) { this.snackbar = snackbar; }

}
