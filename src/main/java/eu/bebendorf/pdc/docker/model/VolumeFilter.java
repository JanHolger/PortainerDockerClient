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
public class VolumeFilter implements DockerFilter {
    String key;
    List<String> values;
    public static VolumeFilter name(String... names){
        return name(Arrays.asList(names));
    }
    public static VolumeFilter name(List<String> names){
        return new VolumeFilter("name", names);
    }
    public static VolumeFilter label(String... labels){
        return label(Arrays.asList(labels));
    }
    public static VolumeFilter label(List<String> labels){
        return new VolumeFilter("label", labels);
    }
}
