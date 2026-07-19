package io.sushant.betslip;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bets")
public class BetController {

    private final BetRepository betRepository;

    // Spring gives the repository automatically 
    public BetController(BetRepository betRepository) {
        this.betRepository = betRepository;
    }

    @PostMapping
    public Bet placeBet(@RequestBody Bet bet) {
        bet.setStatus("PENDING");        // check to see if every bet is Pending
        return betRepository.save(bet);  // save to DB & return the saved bet with id
    }
}