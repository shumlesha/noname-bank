package ru.patterns.credit.shared.response.credit.rating;

import java.util.UUID;

public record CreditRatingResponse(
        Float rating,
        UUID clientId
) {
}
