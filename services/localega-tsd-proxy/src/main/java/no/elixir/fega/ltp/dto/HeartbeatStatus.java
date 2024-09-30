package no.elixir.fega.ltp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class HeartbeatStatus {

    @JsonProperty
    private List<QueueStatus> queuesStatus;
    @JsonProperty
    private List<ServiceStatus> servicesStatus;

    // Inner class for Queue Status
    @Data
    public static class QueueStatus {
        @JsonProperty
        private String name;
        @JsonProperty
        private String status;
    }


    @Data
    public static class ServiceStatus {
        @JsonProperty
        private String name;
        @JsonProperty
        private String status;
    }

}
