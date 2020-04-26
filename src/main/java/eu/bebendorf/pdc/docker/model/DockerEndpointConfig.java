package eu.bebendorf.pdc.docker.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DockerEndpointConfig {
    @SerializedName("IPAMConfig")
    public IPAMConfig ipam = new IPAMConfig();
    @SerializedName("Links")
    public List<String> links = null;
    @SerializedName("Aliases")
    public List<String> aliases = null;
    public static class IPAMConfig {
        @SerializedName("IPv4Address")
        public String ip = null;
        @SerializedName("IPv6Address")
        public String ipv6 = null;
        @SerializedName("LinkLocalIPs")
        public List<String> linkLocalIps = null;
    }
}
