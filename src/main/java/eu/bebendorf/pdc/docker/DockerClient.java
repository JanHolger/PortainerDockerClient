package eu.bebendorf.pdc.docker;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import eu.bebendorf.pdc.docker.model.*;
import eu.bebendorf.pdc.exception.RequestException;
import eu.bebendorf.pdc.http.HttpClient;
import eu.bebendorf.pdc.model.PortainerEndpoint;
import eu.bebendorf.pdc.request.DockerContainerCreateRequest;
import eu.bebendorf.pdc.request.DockerNetworkConnectRequest;
import eu.bebendorf.pdc.request.DockerVolumeCreateRequest;
import eu.bebendorf.pdc.response.DockerContainerCreateResponse;
import eu.bebendorf.pdc.response.DockerVolumePruneResponse;
import eu.bebendorf.pdc.utils.WebSocket;
import lombok.Getter;
import lombok.Setter;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarHeader;
import org.kamranzafar.jtar.TarInputStream;
import org.kamranzafar.jtar.TarOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class DockerClient {

    @Getter @Setter
    private PortainerEndpoint portainerEndpoint;

    @Getter
    private HttpClient httpClient;

    public DockerClient(HttpClient httpClient){
        this.httpClient = httpClient;
    }

    public List<DockerContainer> getContainers(ContainerFilter... filters) throws RequestException {
        List<DockerContainer> containers = new ArrayList<>();
        Map<String, String> params = new HashMap<>();
        params.put("all", "true");
        params.put("filters", DockerFilter.json(filters));
        for(DockerContainer container : httpClient.request("GET", "/containers/json?"+HttpClient.queryParams(params), DockerContainer[].class)){
            container.setClient(this);
            containers.add(container);
        }
        return containers;
    }

    public DockerContainer getContainer(String id) throws RequestException {
        List<DockerContainer> containers = getContainers(ContainerFilter.id(id));
        if(containers.size() == 0)
            return null;
        return containers.get(0);
    }

    public void startContainer(String id) throws RequestException {
        httpClient.request("POST", "/containers/"+id+"/start", null);
    }

    public void killContainer(String id, String signal) throws RequestException {
        httpClient.request("POST", "/containers/"+id+"/kill?signal="+HttpClient.urlEncode(signal), null);
    }

    public void killContainer(String id) throws RequestException {
        httpClient.request("POST", "/containers/"+id+"/kill", null);
    }

    public void stopContainer(String id, int timeout) throws RequestException {
        httpClient.request("POST", "/containers/"+id+"/stop?t="+timeout, null);
    }

    public void stopContainer(String id) throws RequestException {
        httpClient.request("POST", "/containers/"+id+"/stop", null);
    }

    public void restartContainer(String id, int timeout) throws RequestException {
        httpClient.request("POST", "/containers/"+id+"/restart?t="+timeout, null);
    }

    public void restartContainer(String id) throws RequestException {
        httpClient.request("POST", "/containers/"+id+"/restart", null);
    }

    public void renameContainer(String id, String name) throws RequestException {
        httpClient.request("POST", "/containers/"+id+"/rename?name="+HttpClient.urlEncode(name), null);
    }

    public void removeContainer(String id) throws RequestException {
        removeContainer(id, false, false, false);
    }

    public void removeContainer(String id, boolean removeVolumes, boolean force, boolean removeLink) throws RequestException {
        Map<String, String> params = new HashMap<>();
        params.put("v", removeVolumes?"true":"false");
        params.put("force", force?"true":"false");
        params.put("link", removeLink?"true":"false");
        httpClient.request("DELETE", "/containers/"+id+"?"+HttpClient.queryParams(params), null);
    }

    public String createContainer(DockerContainerCreateRequest request) throws RequestException {
        return httpClient.request("POST", "/containers/create", request, DockerContainerCreateResponse.class).getId();
    }

    public String createContainer(String name, DockerContainerCreateRequest request) throws RequestException {
        return httpClient.request("POST", "/containers/create?name="+HttpClient.urlEncode(name), request, DockerContainerCreateResponse.class).getId();
    }

    public List<DockerVolume> getVolumes(VolumeFilter... filters) throws RequestException {
        List<DockerVolume> volumes = new ArrayList<>();
        for(DockerVolume volume : httpClient.request("GET", "/volumes?filters="+HttpClient.urlEncode(DockerFilter.json(filters)), DockerVolume[].class)){
            volume.setClient(this);
            volumes.add(volume);
        }
        return volumes;
    }

    public DockerVolume getVolume(String name) throws RequestException {
        return httpClient.request("GET", "/volumes/"+name, DockerVolume.class);
    }

    public DockerVolume createVolume(DockerVolumeCreateRequest request) throws RequestException {
        return httpClient.request("POST", "/volumes/create", request, DockerVolume.class);
    }

    public void removeVolume(String name) throws RequestException {
        removeVolume(name, false);
    }

    public void removeVolume(String name, boolean force) throws RequestException {
        httpClient.request("DELETE", "/volumes/"+name+(force?"?force=true":""), null);
    }

    public DockerVolumePruneResponse pruneVolumes(VolumePruneFilter... filters) throws RequestException {
        return httpClient.request("POST", "/volumes/prune?filters="+HttpClient.urlEncode(DockerFilter.json(filters)), DockerVolumePruneResponse.class);
    }

    public WebSocket attachContainer(String id){
        Map<String, String> params = new HashMap<>();
        if(portainerEndpoint == null){
            params.put("stdin", "true");
            params.put("stdout", "true");
            params.put("stderr", "true");
            params.put("logs", "false");
            params.put("stream", "true");
            return httpClient.webSocket("/containers/"+id+"/attach/ws?"+HttpClient.queryParams(params));
        }
        params.put("token", httpClient.getToken());
        params.put("endpointId", String.valueOf(portainerEndpoint.getId()));
        params.put("id", id);
        return portainerEndpoint.getClient().getHttpClient().webSocket("/websocket/attach?"+HttpClient.queryParams(params));
    }

    public String containerLogs(String id) throws RequestException {
        return containerLogs(id, null, null, null);
    }

    public String containerLogs(String id, Integer since, Integer until) throws RequestException {
        return containerLogs(id, since, until, null);
    }

    public String containerLogs(String id, Integer limit) throws RequestException {
        return containerLogs(id, null, null, limit);
    }

    public void pullImage(String name) throws RequestException {
        httpClient.request("POST", "/images/create?fromImage="+HttpClient.urlEncode(name), null);
    }

    public String containerLogs(String id, Integer since, Integer until, Integer limit) throws RequestException {
        Map<String, String> params = new HashMap<>();
        params.put("stdout", "true");
        params.put("stderr", "true");
        if(since != null)
            params.put("since", String.valueOf(since));
        if(until != null)
            params.put("until", String.valueOf(until));
        if(limit != null)
            params.put("limit", String.valueOf(limit));
        return httpClient.request("GET", "/containers/"+id+"/logs?"+HttpClient.queryParams(params), String.class);
    }

    public byte[] containerArchive(String id, String path) throws RequestException {
        return httpClient.request("GET", "/containers/"+id+"/archive?path="+HttpClient.urlEncode(path), byte[].class);
    }

    public byte[] containerFile(String id, String path) throws RequestException {
        try {
            TarInputStream is = new TarInputStream(new ByteArrayInputStream(containerArchive(id, path)));
            TarEntry entry = is.getNextEntry();
            if(entry == null)
                return null;
            int r;
            byte[] d = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((r = is.read(d)) != -1){
                baos.write(d, 0, r);
            }
            return baos.toByteArray();
        }catch (IOException ex){}
        return null;
    }

    public void containerArchive(String id, String path, byte[] archive) throws RequestException {
        containerArchive(id, path, archive, null, null);
    }

    public void containerArchive(String id, String path, byte[] archive, Boolean noOverwriteDirNonDir, Boolean copyUIDGID) throws RequestException {
        Map<String, String> params = new HashMap<>();
        params.put("path", path);
        if(noOverwriteDirNonDir != null)
            params.put("noOverwriteDirNonDir", noOverwriteDirNonDir?"1":"0");
        if(copyUIDGID != null)
            params.put("copyUIDGID", copyUIDGID?"1":"0");
        httpClient.request("PUT", "/containers/"+id+"/archive?"+HttpClient.queryParams(params), archive, null);
    }

    public void containerFile(String id, String path, byte[] data) throws RequestException {
        try {
            String[] spl = path.split("/");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            TarOutputStream tos = new TarOutputStream(baos);
            TarEntry entry = new TarEntry(new TarHeader());
            entry.setName(spl[spl.length-1]);
            entry.setSize(data.length);
            tos.putNextEntry(entry);
            tos.write(data);
            tos.close();
            containerArchive(id, String.join("/", Arrays.copyOfRange(spl, 0, spl.length-1)), baos.toByteArray());
        }catch (IOException ex){}
    }

    public void connectNetwork(String network, String container, String ip) throws RequestException {
        DockerNetworkConnectRequest request = new DockerNetworkConnectRequest();
        request.container = container;
        request.endpointConfig = new DockerEndpointConfig();
        request.endpointConfig.ipam = new DockerEndpointConfig.IPAMConfig();
        request.endpointConfig.ipam.ip = ip;
        connectNetwork(network, request);
    }

    public void connectNetwork(String network, DockerNetworkConnectRequest request) throws RequestException {
        httpClient.request("POST", "/networks/"+network+"/connect", request, null);
    }

    public void disconnectNetwork(String network, String container) throws RequestException {
        disconnectNetwork(network, container, false);
    }

    public void disconnectNetwork(String network, String container, boolean force) throws RequestException {
        JsonObject request = new JsonObject();
        request.addProperty("Container", container);
        request.addProperty("Force", force);
        httpClient.request("POST", "/networks/"+network+"/disconnect", request, null);
    }

    public void execCommand(String containerId, String user, String workingDir, Map<String, String> env, String... command) throws RequestException {
        JsonObject body = new JsonObject();
        if(workingDir != null)
            body.addProperty("WorkingDir", workingDir);
        if(user != null)
            body.addProperty("User", user);
        if(env != null){
            JsonArray e = new JsonArray();
            for(String k : env.keySet()){
                e.add(k+"="+env.get(k));
            }
            body.add("Env", e);
        }
        JsonArray cmd = new JsonArray();
        for(String c : command){
            cmd.add(c);
        }
        body.add("Cmd", cmd);
        JsonObject response = httpClient.request("POST", "/containers/"+containerId+"/exec", body, JsonObject.class);
        String execId = response.get("Id").getAsString();
        body = new JsonObject();
        body.addProperty("Detach", true);
        httpClient.request("POST", "/exec/"+execId+"/start", body, null);
    }

}
