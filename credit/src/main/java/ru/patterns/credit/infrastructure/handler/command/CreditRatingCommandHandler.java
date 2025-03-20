package ru.patterns.credit.infrastructure.handler.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.patterns.credit.application.command.CreateCreditRatingCommand;
import ru.patterns.credit.application.command.DecreaseCreditRatingCommand;
import ru.patterns.credit.application.command.IncreaseCreditRatingCommand;
import ru.patterns.credit.domain.model.CreditRating;
import ru.patterns.credit.domain.repository.CreditRatingRepository;
import ru.patterns.credit.shared.response.credit.rating.CreditRatingResponse;

import java.math.RoundingMode;

import static ru.patterns.credit.shared.constant.CreditRatingConstants.DEFAULT_CREDIT_RATING;
import static ru.patterns.credit.shared.constant.CreditRatingConstants.MAX_CREDIT_RATING;
import static ru.patterns.credit.shared.constant.CreditRatingConstants.MIN_CREDIT_RATING;
import static ru.patterns.credit.shared.constant.CreditRatingConstants.PENALTY_DECREASE;

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

    @Transactional
    public void handle(IncreaseCreditRatingCommand command) {
        creditRatingRepository.findByClientId(command.clientId()).ifPresent(creditRating -> {
            var percentagePaid = command.paidAmount().divide(command.totalLoan(), 2, RoundingMode.HALF_UP);
            float increaseAmount = percentagePaid.floatValue() * 2;
            float newRating = Math.min(creditRating.getRating() + increaseAmount, MAX_CREDIT_RATING);

            creditRating.setRating((float)((int) newRating));
            creditRatingRepository.save(creditRating);
        });
    }

    @Transactional
    public void handle(DecreaseCreditRatingCommand command) {
        creditRatingRepository.findByClientId(command.clientId()).ifPresent(creditRating -> {
            float newRating = Math.max(creditRating.getRating() - PENALTY_DECREASE, MIN_CREDIT_RATING);

            creditRating.setRating((float)((int) newRating));
            creditRatingRepository.save(creditRating);
        });
    }

    private CreditRating createCreditRating(CreateCreditRatingCommand command) {
        var creditRating = new CreditRating();
        creditRating.setClientId(command.clientId());
        creditRating.setRating(DEFAULT_CREDIT_RATING);
        return creditRatingRepository.save(creditRating);
    }
}
