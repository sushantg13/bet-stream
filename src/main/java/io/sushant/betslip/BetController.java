package io.sushant.betslip;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bets")
public class BetController {

    private final BetRepository betRepository;

    // Spring's tool for sending events to Kafka. A message have 2 parts to it: key & value (both are string). Refer to application.properties 
    private final KafkaTemplate<String, String> kafkaTemplate;

    public BetController(BetRepository betRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.betRepository = betRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    public Bet placeBet(@RequestBody Bet bet) {
        bet.setStatus("PENDING");
        Bet savedBet = betRepository.save(bet);

        // publish an event announcing the bet was placed
        String event = "Bet placed: id=" + savedBet.getId()
                + ", selection=" + savedBet.getSelection()
                + ", stake=" + savedBet.getStake();
        kafkaTemplate.send("bets.placed", event);

        return savedBet;
    }
}