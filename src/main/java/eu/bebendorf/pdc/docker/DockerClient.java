package eu.bebendorf.pdc.docker;

import eu.bebendorf.pdc.PortainerClient;
import eu.bebendorf.pdc.docker.model.*;
import eu.bebendorf.pdc.exception.RequestException;
import eu.bebendorf.pdc.http.HttpClient;
import eu.bebendorf.pdc.model.PortainerEndpoint;
import eu.bebendorf.pdc.request.DockerContainerCreateRequest;
import eu.bebendorf.pdc.request.DockerVolumeCreateRequest;
import eu.bebendorf.pdc.response.DockerContainerCreateResponse;
import eu.bebendorf.pdc.response.DockerVolumePruneResponse;
import eu.bebendorf.pdc.utils.WebSocket;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

}
