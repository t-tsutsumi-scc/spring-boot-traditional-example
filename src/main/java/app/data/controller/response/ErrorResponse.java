package app.data.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse {

    @JsonProperty("id")
    public String id;

    @JsonProperty("message")
    public String message;

}
