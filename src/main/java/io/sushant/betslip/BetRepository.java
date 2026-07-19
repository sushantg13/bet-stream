package io.sushant.betslip;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BetRepository extends JpaRepository<Bet, Long> {

    List<Bet> findByMarketAndStatus(String market, String status);
}