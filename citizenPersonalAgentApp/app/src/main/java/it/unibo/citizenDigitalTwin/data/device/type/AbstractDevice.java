package it.unibo.citizenDigitalTwin.data.device.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import it.unibo.citizenDigitalTwin.data.category.LeafCategory;

abstract class AbstractDevice implements Device {

    private final String name;
    private final List<LeafCategory> categories;

    AbstractDevice(final String name) {
        this.name = name;
        this.categories = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<LeafCategory> getCategories() {
        return categories;
    }

    @Override
    public void setCategories(final Collection<LeafCategory> categories){
        this.categories.clear();
        this.categories.addAll(categories);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractDevice that = (AbstractDevice) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
