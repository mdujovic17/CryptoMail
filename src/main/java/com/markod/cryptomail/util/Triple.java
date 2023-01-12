package com.markod.cryptomail.util;

public record Triple<T, U, V>(T algorithm, U digest, V name) {
}
