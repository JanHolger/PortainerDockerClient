package eu.bebendorf.pdc.request;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import eu.bebendorf.pdc.docker.model.DockerHostConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class DockerContainerCreateRequest {
    @SerializedName("Hostname")
    String hostname;
    @SerializedName("Domainname")
    String domainName;
    @SerializedName("User")
    String user;
    @SerializedName("AttachStdin")
    Boolean attachStdin;
    @SerializedName("AttachStdout")
    Boolean attachStdout;
    @SerializedName("AttachStderr")
    Boolean attachStderr;
    @SerializedName("ExposedPorts")
    Map<String, JsonObject> exposedPorts = new HashMap<>();
    @SerializedName("Tty")
    Boolean tty;
    @SerializedName("OpenStdin")
    Boolean openStdin;
    @SerializedName("StdinOnce")
    Boolean stdinOnce;
    @SerializedName("Env")
    List<String> env = new ArrayList<>();
    @SerializedName("Cmd")
    String[] cmd;
    @SerializedName("Image")
    String image;
    @SerializedName("WorkingDir")
    String workingDir;
    @SerializedName("Entrypoint")
    String[] entrypoint;
    @SerializedName("NetworkDisabled")
    Boolean networkDisabled;
    @SerializedName("MacAddress")
    String macAddress;
    @SerializedName("OnBuild")
    String[] onBuild;
    @SerializedName("Labels")
    Map<String, String> labels = new HashMap<>();
    @SerializedName("StopSignal")
    String stopSignal;
    @SerializedName("StopTimeout")
    Integer stopTimeout;
    @SerializedName("Shell")
    String[] shell;
    @SerializedName("HostConfig")
    DockerHostConfig hostConfig = new DockerHostConfig();
    public DockerContainerCreateRequest user(String user){
        this.user = user;
        return this;
    }
    public DockerContainerCreateRequest image(String image){
        this.image = image;
        return this;
    }
    public DockerContainerCreateRequest macAddress(String macAddress){
        this.macAddress = macAddress;
        return this;
    }
    public DockerContainerCreateRequest label(String key){
        return label(key, "");
    }
    public DockerContainerCreateRequest label(String key, String value){
        this.labels.put(key, value);
        return this;
    }
    public DockerContainerCreateRequest env(String key, String value){
        this.env.add(key+"="+value);
        return this;
    }
    public DockerContainerCreateRequest exposePort(int port){
        return exposePort("tcp", port);
    }
    public DockerContainerCreateRequest exposePort(String protocol, int port){
        this.exposedPorts.put(port+"/"+protocol, new JsonObject());
        return this;
    }
    public DockerContainerCreateRequest bindPort(String protocol, int port, String hostAddress, int hostPort){
        hostConfig.bindPort(protocol, port, hostAddress, hostPort);
        return this;
    }
    public DockerContainerCreateRequest bindPort(int port, String hostAddress, int hostPort){
        hostConfig.bindPort(port, hostAddress, hostPort);
        return this;
    }
    public DockerContainerCreateRequest bindPort(String protocol, int port, int hostPort){
        hostConfig.bindPort(protocol, port, hostPort);
        return this;
    }
    public DockerContainerCreateRequest bindPort(int port, int hostPort){
        hostConfig.bindPort(port, hostPort);
        return this;
    }
    public DockerContainerCreateRequest bindVolume(String containerPath, String volumeName){
        hostConfig.bindVolume(containerPath, volumeName);
        return this;
    }
    public DockerContainerCreateRequest bindVolume(String containerPath, String volumeName, boolean readOnly){
        hostConfig.bindVolume(containerPath, volumeName, readOnly);
        return this;
    }
    public DockerContainerCreateRequest stopSignal(String stopSignal){
        this.stopSignal = stopSignal;
        return this;
    }
    public DockerContainerCreateRequest stopTimeout(int stopTimeout){
        this.stopTimeout = stopTimeout;
        return this;
    }
    public DockerContainerCreateRequest workingDir(String workingDir){
        this.workingDir = workingDir;
        return this;
    }
    public DockerContainerCreateRequest attachStdin(boolean attachStdin){
        this.attachStdin = attachStdin;
        return this;
    }
    public DockerContainerCreateRequest attachStdout(boolean attachStdout){
        this.attachStdout = attachStdout;
        return this;
    }
    public DockerContainerCreateRequest attachStderr(boolean attachStderr){
        this.attachStderr = attachStderr;
        return this;
    }
    public DockerContainerCreateRequest setTty(boolean tty){
        this.tty = tty;
        return this;
    }
}
