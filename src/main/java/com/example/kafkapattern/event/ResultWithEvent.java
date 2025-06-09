package com.example.kafkapattern.event;

public record ResultWithEvent<T, E>(T result, E event) {
}
