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
public class VolumePruneFilter implements DockerFilter {
    String key;
    List<String> values;
    public static VolumePruneFilter label(String... labels){
        return label(Arrays.asList(labels));
    }
    public static VolumePruneFilter label(List<String> labels){
        return new VolumePruneFilter("label", labels);
    }
}
