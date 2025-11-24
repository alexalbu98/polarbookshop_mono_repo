package com.polarbookshop.quote_function.domain;

public record Quote (
        String content,
        String author,
        Genre genre
){}