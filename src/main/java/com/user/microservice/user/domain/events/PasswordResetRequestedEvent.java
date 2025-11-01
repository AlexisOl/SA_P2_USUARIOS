package com.user.microservice.user.domain.events;

import java.time.Instant;

public record PasswordResetRequestedEvent(
    String userId,
    String email,
    String nombre,
    String token,
    String link,
    Instant expiresAt
) {}