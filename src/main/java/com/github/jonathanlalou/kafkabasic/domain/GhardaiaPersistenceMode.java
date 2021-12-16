package com.github.jonathanlalou.kafkabasic.domain;

public enum GhardaiaPersistenceMode {
    /**
     * Entities are persisted on the fly, via a direct call to DAOs
     */
    SYNCHRONOUS,
    /**
     * Entities are persisted asynchronously: they are sent to the message broker (Kafka), then consumed by a Consumer and persisted in DB
     */
    ASYNCHRONOUS
}
