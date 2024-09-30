package no.elixir.fega.ltp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Set;
import no.elixir.fega.ltp.dto.HeartbeatStatus;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class HeartbeatService {

  private final RedisTemplate<String, String> redisTemplate;

  public HeartbeatService(RedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public HeartbeatStatus getHeartbeatStatus() throws JsonProcessingException {
    HeartbeatStatus heartbeatStatus = new HeartbeatStatus();
    // Get all keys from Redis related to queues_status and services_status
    Set<String> keys = redisTemplate.keys("*");
    // Iterate over keys and fetch values
    if (keys != null) {
      for (String key : keys) {
        String value = redisTemplate.opsForValue().get(key);
        if (key.startsWith("queue_status:")) {
          // Handle queue status
          HeartbeatStatus.QueueStatus queueStatus = new HeartbeatStatus.QueueStatus();
          queueStatus.setName(key.replace("queue_status:", ""));
          queueStatus.setStatus(value);
          heartbeatStatus.getQueuesStatus().add(queueStatus);
        } else if (key.startsWith("service_status:")) {
          // Handle service status
          HeartbeatStatus.ServiceStatus serviceStatus = new HeartbeatStatus.ServiceStatus();
          serviceStatus.setName(key.replace("service_status:", ""));
          serviceStatus.setStatus(value);
          heartbeatStatus.getServicesStatus().add(serviceStatus);
        }
      }
    }
    return heartbeatStatus;
  }
}
