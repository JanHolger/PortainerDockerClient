package eu.bebendorf.pdc.docker.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public class ContainerFilter implements DockerFilter {
    String key;
    List<String> values;
    public static ContainerFilter id(String... ids){
        return id(Arrays.asList(ids));
    }
    public static ContainerFilter id(List<String> ids){
        return new ContainerFilter("id", ids);
    }
    public static ContainerFilter name(String... names){
        return name(Arrays.asList(names));
    }
    public static ContainerFilter name(List<String> names){
        return new ContainerFilter("name", names);
    }
    public static ContainerFilter label(String... labels){
        return label(Arrays.asList(labels));
    }
    public static ContainerFilter label(List<String> labels){
        return new ContainerFilter("label", labels);
    }
}
