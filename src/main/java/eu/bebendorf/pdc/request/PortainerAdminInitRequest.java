package eu.bebendorf.pdc.request;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PortainerAdminInitRequest {
    @SerializedName("Username")
    String username;
    @SerializedName("Password")
    String password;
}
