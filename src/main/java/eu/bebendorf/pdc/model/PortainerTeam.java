package eu.bebendorf.pdc.model;

import com.google.gson.annotations.SerializedName;
import eu.bebendorf.pdc.PortainerClient;
import eu.bebendorf.pdc.exception.RequestException;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class PortainerTeam {
    @Setter
    PortainerClient client;
    @SerializedName("Id")
    Integer id;
    @SerializedName("Name")
    String name;
    public void delete() throws RequestException {
        client.getHttpClient().request("DELETE", "/teams/"+id, null);
    }
    public void changeName(String name) throws RequestException {
        PortainerTeam team = new PortainerTeam();
        team.name = name;
        client.getHttpClient().request("PUT", "/teams/"+id, team, null);
        this.name = name;
    }
    public List<PortainerTeamMembership> getTeamMemberships() throws RequestException {
        List<PortainerTeamMembership> memberships = new ArrayList<>();
        for(PortainerTeamMembership membership : client.getHttpClient().request("GET", "/teams/"+id+"/memberships", PortainerTeamMembership[].class)){
            membership.setClient(client);
            memberships.add(membership);
        }
        return memberships;
    }
    public PortainerTeamMembership addUser(PortainerUser user, int roleId) throws RequestException {
        return client.createTeamMembership(id, user.getId(), roleId);
    }
}
