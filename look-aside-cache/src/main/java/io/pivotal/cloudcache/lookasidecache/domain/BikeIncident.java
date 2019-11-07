package io.pivotal.cloudcache.lookasidecache.domain;

public class BikeIncident {
    private String type;
    private String title;
    private String description;
    private String address;

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

}
