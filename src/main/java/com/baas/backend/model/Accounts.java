package com.baas.backend.model;

import java.util.UUID;

public record Accounts(UUID sourceAccountId, UUID targetAccountId) {
}
