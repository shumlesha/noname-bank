package ru.patterns.credit.infrastructure.handler.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.credit.application.query.GetCreditRatingByClientQuery;
import ru.patterns.credit.domain.model.CreditRating;
import ru.patterns.credit.domain.repository.CreditRatingRepository;
import ru.patterns.credit.shared.response.credit.rating.CreditRatingResponse;

import static ru.patterns.credit.shared.constant.CreditRatingConstants.DEFAULT_CREDIT_RATING;

@Service
@RequiredArgsConstructor
public class CreditRatingQueryHandler {
    private final CreditRatingRepository creditRatingRepository;

    public CreditRatingResponse handle(GetCreditRatingByClientQuery query) {
        var creditRating = creditRatingRepository.findByClientId(query.clientId());

        float rating = creditRating.map(CreditRating::getRating).orElse(DEFAULT_CREDIT_RATING);

        return new CreditRatingResponse(rating, query.clientId());
    }
}
