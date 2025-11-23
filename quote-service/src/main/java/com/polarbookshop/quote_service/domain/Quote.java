package com.polarbookshop.quote_service.domain;

public record Quote (
        String content,
        String author,
        Genre genre
){}