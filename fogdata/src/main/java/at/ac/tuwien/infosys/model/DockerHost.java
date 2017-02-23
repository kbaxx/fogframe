package at.ac.tuwien.infosys.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DockerHost {

    private String name;
    private String url;
    private String flavor;

    public DockerHost() {
        this.flavor = "m1.micro";
    }

    public DockerHost(String url) {
        this.url = url;
        this.flavor = "m1.micro";
    }

    public DockerHost(String name, String flavor) {
        this.name = name;
        this.flavor = flavor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DockerHost that = (DockerHost) o;

        return url != null ? url.equals(that.url) : that.url == null;
    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DockerHost{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
