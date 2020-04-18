package eu.bebendorf.pdc.response;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class PortainerAuthResponse {
    @SerializedName("jwt")
    String token;
}
