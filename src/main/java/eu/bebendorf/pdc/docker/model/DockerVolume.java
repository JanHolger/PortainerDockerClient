package eu.bebendorf.pdc.docker.model;

import com.google.gson.annotations.SerializedName;
import eu.bebendorf.pdc.docker.DockerClient;
import eu.bebendorf.pdc.exception.RequestException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class DockerVolume {
    @Setter
    private DockerClient client;
    @SerializedName("CreatedAt")
    String createdAt;
    @SerializedName("Name")
    String name;
    @SerializedName("Driver")
    String driver;
    @SerializedName("Mountpoint")
    String mountpoint;
    @SerializedName("Labels")
    Map<String, String> labels;
    @SerializedName("Scope")
    String scope;
    @SerializedName("Options")
    Map<String, String> options;
    public void remove() throws RequestException {
        remove(false);
    }
    public void remove(boolean force) throws RequestException {
        client.removeVolume(name, force);
    }
}
