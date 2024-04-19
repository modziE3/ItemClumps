package net.dev.itemclumps.util;

public class Clump {
    private String name, tag_id;
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public String getTagId() {
        return tag_id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "{"+tag_id+": "+enabled+"}";
    }
}
