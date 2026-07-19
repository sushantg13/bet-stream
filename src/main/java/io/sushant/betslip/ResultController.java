package io.sushant.betslip;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/results")
public class ResultController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public ResultController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    public String publishResult(@RequestParam String market, @RequestParam String winner) {
        String event = market + "|" + winner;
        kafkaTemplate.send("match.results", event);
        return "Result published: " + event;
    }
}