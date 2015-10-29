package com.netural.loco.base;

import java.util.Date;

public class LocoInfo {

    public String project;
    public String release;
    public Date lastUpdate;

    public LocoInfo(String project, String release, Date lastUpdate) {
        this.lastUpdate = lastUpdate;
        this.project = project;
        this.release = release;
    }

    @Override
    public String toString() {
        String string = project + "\n" + release;
        if (lastUpdate != null) {
            string += "\n" + lastUpdate.toGMTString();
        }
        return string;
    }
}
