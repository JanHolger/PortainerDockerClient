package eu.bebendorf.pdc.docker.model;

import eu.bebendorf.pdc.http.HttpClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface DockerFilter {
    String getKey();
    List<String> getValues();
    static String json(DockerFilter... filters){
        Map<String, List<String>> f = new HashMap<>();
        for(DockerFilter filter : filters)
            f.put(filter.getKey(), filter.getValues());
        return HttpClient.jsonEncode(f);
    }
}
