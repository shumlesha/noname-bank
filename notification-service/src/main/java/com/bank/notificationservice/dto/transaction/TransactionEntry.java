package com.bank.notificationservice.dto.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEntry {
    private UUID id;

    private LocalDateTime transactionTimestamp;

    private UUID accountFrom;
    private UUID accountTo;
    private BigDecimal amount;

    private UUID clientId;
}
