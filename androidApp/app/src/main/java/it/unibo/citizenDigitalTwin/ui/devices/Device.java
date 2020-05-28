package it.unibo.citizenDigitalTwin.ui.devices;

import java.util.List;

import it.unibo.citizenDigitalTwin.data.category.LeafCategory;

class Device {

    private final String name;
    private final List<LeafCategory> categories;

    Device(final String name, final List<LeafCategory> categories){
        this.name = name;
        this.categories = categories;
    }

    public List<LeafCategory> getCategories() {
        return categories;
    }

    public String getName() {
        return name;
    }
}
