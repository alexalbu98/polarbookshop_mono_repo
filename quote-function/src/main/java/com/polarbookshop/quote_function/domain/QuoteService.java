package com.polarbookshop.quote_function.domain;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;

@Service
public class QuoteService {
    private static final Random RANDOM = new Random();
    private static final List<Quote> QUOTES = List.of(
            new Quote("Content A", "Abigail", Genre.ADVENTURE),
            new Quote("Content B", "Beatrix", Genre.ADVENTURE),
            new Quote("Content C", "Casper", Genre.FANTASY),
            new Quote("Content D", "Dobby", Genre.FANTASY),
            new Quote("Content E", "Eileen", Genre.SCIENCE_FICTION),
            new Quote("Content F", "Flora", Genre.SCIENCE_FICTION)
    );

    public Flux<Quote> getAllQuotes() {
        return Flux.fromIterable(QUOTES);
    }

    public Mono<Quote> getRandomQuote() {
        return Mono.just(QUOTES.get(RANDOM.nextInt(QUOTES.size() - 1)));
    }

    public Mono<Quote> getRandomQuoteByGenre(Genre genre) {
        var quotesForGenre = QUOTES.stream()
                .filter(q -> q.genre().equals(genre))
                .toList();
        return Mono.just(quotesForGenre.get(
                RANDOM.nextInt(quotesForGenre.size() - 1)));
    }
}