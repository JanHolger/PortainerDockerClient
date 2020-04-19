package eu.bebendorf.pdc.model;

import com.google.gson.annotations.SerializedName;
import eu.bebendorf.pdc.PortainerClient;
import eu.bebendorf.pdc.exception.RequestException;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PortainerUser {
    @Setter
    PortainerClient client;
    @SerializedName("Id")
    Integer id;
    @SerializedName("Username")
    String username;
    @SerializedName("Password")
    String password;
    @SerializedName("Role")
    Integer roleId;
    public void delete() throws RequestException {
        client.getHttpClient().request("DELETE", "/users/"+id, null);
    }
    public void changePassword(String password) throws RequestException {
        PortainerUser user = new PortainerUser();
        user.password = password;
        client.getHttpClient().request("PUT", "/users/"+id, user, null);
    }
    public void changeRole(int roleId) throws RequestException {
        PortainerUser user = new PortainerUser();
        user.roleId = roleId;
        client.getHttpClient().request("PUT", "/users/"+id, user, null);
        this.roleId = roleId;
    }
    public List<PortainerTeamMembership> getTeamMemberships() throws RequestException {
        List<PortainerTeamMembership> memberships = new ArrayList<>();
        for(PortainerTeamMembership membership : client.getHttpClient().request("GET", "/users/"+id+"/memberships", PortainerTeamMembership[].class)){
            membership.setClient(client);
            memberships.add(membership);
        }
        return memberships;
    }
}
