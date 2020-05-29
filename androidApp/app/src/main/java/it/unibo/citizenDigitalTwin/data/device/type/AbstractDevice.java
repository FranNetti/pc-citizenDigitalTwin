package it.unibo.citizenDigitalTwin.data.device.type;

import java.util.List;
import java.util.Objects;

import it.unibo.citizenDigitalTwin.data.category.LeafCategory;

public abstract class AbstractDevice implements Device {

    private final String name;
    private final List<LeafCategory> categories;

    AbstractDevice(final String name, final List<LeafCategory> categories) {
        this.name = name;
        this.categories = categories;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<LeafCategory> getCategories() {
        return categories;
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
