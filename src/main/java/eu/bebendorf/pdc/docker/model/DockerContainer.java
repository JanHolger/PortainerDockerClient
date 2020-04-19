package eu.bebendorf.pdc.docker.model;

import com.google.gson.annotations.SerializedName;
import eu.bebendorf.pdc.docker.DockerClient;
import eu.bebendorf.pdc.exception.RequestException;
import eu.bebendorf.pdc.utils.WebSocket;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DockerContainer {
    @Setter
    private DockerClient client;
    @SerializedName("Id")
    String id;
    @SerializedName("Names")
    List<String> names;
    @SerializedName("Image")
    String image;
    @SerializedName("ImageID")
    String imageId;
    @SerializedName("Command")
    String command;
    @SerializedName("Created")
    Long created;
    @SerializedName("State")
    String state;
    @SerializedName("Status")
    String status;
    @SerializedName("Labels")
    Map<String, String> labels;
    public void start() throws RequestException {
        client.startContainer(id);
    }
    public void kill() throws RequestException {
        client.killContainer(id);
    }
    public void kill(String signal) throws RequestException {
        client.killContainer(id, signal);
    }
    public void stop() throws RequestException {
        client.stopContainer(id);
    }
    public void stop(int timeout) throws RequestException {
        client.stopContainer(id, timeout);
    }
    public void restart() throws RequestException {
        client.restartContainer(id);
    }
    public void restart(int timeout) throws RequestException {
        client.restartContainer(id, timeout);
    }
    public void rename(String name) throws RequestException {
        client.renameContainer(id, name);
    }
    public void remove() throws RequestException {
        remove(false, false, false);
    }
    public void remove(boolean force) throws RequestException {
        remove(false, force, false);
    }
    public void remove(boolean removeVolumes, boolean force, boolean removeLink) throws RequestException {
        client.removeContainer(id, removeVolumes, force, removeLink);
    }
    public WebSocket attach(){
        return client.attachContainer(id);
    }
    public String logs() throws RequestException {
        return client.containerLogs(id);
    }
    public String logs(Integer since, Integer until) throws RequestException {
        return client.containerLogs(id, since, until);
    }
    public String logs(Integer limit) throws RequestException {
        return client.containerLogs(id, limit);
    }
    public String logs(Integer since, Integer until, Integer limit) throws RequestException {
        return client.containerLogs(id, since, until, limit);
    }
}
