package io.sushant.betslip;

import java.util.List;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SettlementListener {

    private final BetRepository betRepository;

    public SettlementListener(BetRepository betRepository) {
        this.betRepository = betRepository;
    }

    @KafkaListener(topics = "match.results", groupId = "settlement-group")
    public void settle(String event) {
        // event format: "market|winner"
        String[] parts = event.split("\\|");
        String market = parts[0];
        String winner = parts[1];

        System.out.println("Settling market: " + market + ", winner: " + winner);

        // find all pending bets on this market
        List<Bet> pendingBets = betRepository.findByMarketAndStatus(market, "PENDING");

        for (Bet bet : pendingBets) {
            if (bet.getSelection().equals(winner)) {
                bet.setStatus("WON");
            } else {
                bet.setStatus("LOST");
            }
            betRepository.save(bet);
            System.out.println("Bet id=" + bet.getId() + " settled as " + bet.getStatus());
        }
    }
}