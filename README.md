# bet-stream

A small event-driven bet settlement service, built to understand how bet
placement and settlement work under the hood. Java / Spring Boot / Kafka.

You place a bet, the app records it and emits an event. When a match result
comes in, a Kafka consumer picks it up and settles the matching bets as
WON or LOST.

## Why

I wanted to learn event-driven architecture properly rather than just read
about it, so I built a slice of a real betting system on the stack that
companies like Sportsbet actually use.

## How it works

There are two things you can do: place a bet, and report a match result.

When you place a bet (`POST /bets`), the app saves it to Postgres with a
status of PENDING and then publishes a "bet placed" event to Kafka. The
request finishes there, and it doesn't wait around for anything else to happen.

Separately, when a match result comes in (`POST /results`), the app
publishes that to a second Kafka topic. A settlement consumer is listening
on that topic. It wakes up, looks at which market the result is for, finds
all the PENDING bets on that market, and settles each one. WON if the bet
backed the winner, LOST otherwise.

The point of doing it this way: the part that takes bets and the part that
settles them are completely separate. They only communicate through events.
The bet-placing side doesn't call the settlement side or wait for it. It
just announces "a bet was placed" and moves on. That's what makes it
event-driven, and it's why a real betting system can handle a flood of bets
without everything grinding to a halt.sumer.

## Stack

- Java 21, Spring Boot 4
- Apache Kafka (producer + consumer)
- PostgreSQL via Spring Data JPA
- Docker Compose for Kafka and Postgres

## Endpoints

- `POST /bets` — place a bet (JSON: market, selection, stake, odds)
- `GET /bets` — list all bets
- `POST /results?market=...&winner=...` — publish a match result

## Running it

Start Kafka and Postgres:

```bash
docker compose up -d
```

Run the app:

```bash
./mvnw spring-boot:run
```

Place a bet:

```bash
curl -X POST http://localhost:8080/bets \
  -H "Content-Type: application/json" \
  -d '{"market": "Wimbledon Final", "selection": "Alcaraz", "stake": 100.0, "odds": 1.8}'
```

Settle it by publishing the result:

```bash
curl -X POST "http://localhost:8080/results?market=Wimbledon%20Final&winner=Alcaraz"
```

Check the bet is now WON:

```bash
curl http://localhost:8080/bets
```

## Notes / what I'd do next

- Events are sent as plain strings to keep it simple. Production would use
  structured JSON with a schema.
- Bets are matched to results by exact market name. A real system would key
  off market and selection IDs.
- Kafka's default idempotent producer is on, so retries won't duplicate
  events. A dead-letter topic and consumer retry/backoff would be the next
  reliability step.
- Not yet deployed. Next step is containerising the app and running it on
  AWS (ECS/RDS/MSK).