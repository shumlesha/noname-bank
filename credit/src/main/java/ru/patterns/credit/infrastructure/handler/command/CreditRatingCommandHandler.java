package ru.patterns.credit.infrastructure.handler.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.patterns.credit.application.command.CreateCreditRatingCommand;
import ru.patterns.credit.domain.model.CreditRating;
import ru.patterns.credit.domain.repository.CreditRatingRepository;
import ru.patterns.credit.shared.response.credit.rating.CreditRatingResponse;

import static ru.patterns.credit.shared.constant.CreditRatingConstants.DEFAULT_CREDIT_RATING;

@Service
@RequiredArgsConstructor
public class CreditRatingCommandHandler {
    private final CreditRatingRepository creditRatingRepository;

    @Transactional
    public CreditRatingResponse handle(CreateCreditRatingCommand command) {
        var existingRating = creditRatingRepository.findByClientId(command.clientId())
                .map(creditRating -> new CreditRatingResponse(
                        creditRating.getRating(),
                        command.clientId()
                ));

        if (existingRating.isPresent()) {
            return existingRating.get();
        }

        var createdRating = createCreditRating(command);
        return new CreditRatingResponse(createdRating.getRating(), createdRating.getClientId());
    }

    private CreditRating createCreditRating(CreateCreditRatingCommand command) {
        var creditRating = new CreditRating();
        creditRating.setClientId(command.clientId());
        creditRating.setRating(DEFAULT_CREDIT_RATING);
        return creditRatingRepository.save(creditRating);
    }
}
