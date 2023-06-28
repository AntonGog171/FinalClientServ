package proj;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private String name;
    private String description;

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
    public Category(){}

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Category{" +
                "\"name\":\"" + name + '\"' +
                ", \"description\":\"" + description + '\"' +
                '}';
    }

    public String getDescription() {
        return description;
    }
}

