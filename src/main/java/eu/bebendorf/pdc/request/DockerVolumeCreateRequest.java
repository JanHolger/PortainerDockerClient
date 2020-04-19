package eu.bebendorf.pdc.request;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class DockerVolumeCreateRequest {
    public DockerVolumeCreateRequest(String name){
        this.name = name;
    }
    @SerializedName("Name")
    String name;
    @SerializedName("Driver")
    String driver;
    @SerializedName("Labels")
    Map<String, String> labels = new HashMap<>();
    public DockerVolumeCreateRequest setDriver(String driver){
        this.driver = driver;
        return this;
    }
    public DockerVolumeCreateRequest label(String key){
        return label(key, "");
    }
    public DockerVolumeCreateRequest label(String key, String value){
        this.labels.put(key, value);
        return this;
    }
}
