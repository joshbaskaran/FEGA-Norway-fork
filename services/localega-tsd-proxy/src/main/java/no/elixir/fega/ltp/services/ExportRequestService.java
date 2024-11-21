package no.elixir.fega.ltp.services;

import lombok.extern.slf4j.Slf4j;
import no.elixir.fega.ltp.dto.ExportRequest;
import org.apache.http.entity.ContentType;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class ExportRequestService {

    private final RabbitTemplate tsdRabbitTemplate;

    @Value("${mq.tsd.exchange}")
    private String exchange;
    @Value("${mq.tsd.routing-key}")
    private String routingKey;

    @Autowired
    public ExportRequestService(RabbitTemplate tsdRabbitTemplate) {
        this.tsdRabbitTemplate = tsdRabbitTemplate;
    }

    public void exportRequest(ExportRequest exportRequest) {
        log.info("Export request: {} | Exchange: {} | Routing-key: {}", exportRequest, exchange, routingKey);
        tsdRabbitTemplate.convertAndSend(
                exchange,
                routingKey,
                exportRequest.toJson(),
                m -> {
                    m.getMessageProperties().setContentType(ContentType.APPLICATION_JSON.getMimeType());
                    m.getMessageProperties().setCorrelationId(UUID.randomUUID().toString());
                    return m;
                });
    }

}
