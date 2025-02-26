package ru.patterns.credit.shared.response;

import ru.patterns.credit.shared.dto.AccountDto;

import java.util.List;

public record AccountListResponse(List<AccountDto> data) { }
