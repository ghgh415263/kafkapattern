package com.example.kafkapattern.common.event;

public record ResultWithEvent<T, E>(T result, E event) {
}
