package eu.bebendorf.pdc.docker.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import eu.bebendorf.pdc.request.DockerContainerCreateRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class DockerHostConfig {
    @SerializedName("Binds")
    List<String> volumeBinds = new ArrayList<>();
    @SerializedName("Links")
    List<String> links = new ArrayList<>();
    @SerializedName("Memory") @Setter @Getter
    Long memory;
    @SerializedName("CpuShares") @Setter @Getter
    Integer cpuShares;
    @SerializedName("PortBindings")
    Map<String, List<JsonObject>> portBindings = new HashMap<>();
    @SerializedName("RestartPolicy")
    Map<String, Object> restartPolicy;
    @SerializedName("AutoRemove") @Setter @Getter
    Boolean autoRemove;
    public void bindPort(String protocol, int port, String hostAddress, int hostPort){
        List<JsonObject> binding = new ArrayList<>();
        if(portBindings.containsKey(port+"/"+protocol))
            binding = portBindings.get(port+"/"+protocol);
        JsonObject bind = new JsonObject();
        if(hostAddress != null)
            bind.addProperty("HostIp", hostAddress);
        bind.addProperty("HostPort", String.valueOf(hostPort));
        binding.add(bind);
        portBindings.put(port+"/"+protocol, binding);
    }
    public void bindPort(int port, String hostAddress, int hostPort){
        bindPort("tcp", port, hostAddress, hostPort);
    }
    public void bindPort(String protocol, int port, int hostPort){
        bindPort(protocol, port, null, hostPort);
    }
    public void bindPort(int port, int hostPort){
        bindPort("tcp", port, null, hostPort);
    }
    public void bindVolume(String containerPath, String volumeName){
        bindVolume(containerPath, volumeName, false);
    }
    public void bindVolume(String containerPath, String volumeName, boolean readOnly){
        volumeBinds.add(volumeName+":"+containerPath+(readOnly?":ro":":rw"));
    }
}
