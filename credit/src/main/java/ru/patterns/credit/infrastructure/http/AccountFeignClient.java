package ru.patterns.credit.infrastructure.http;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.patterns.credit.shared.request.AccountListRequest;
import ru.patterns.credit.shared.response.AccountListResponse;

@FeignClient(name = "core-service", path = "/api/account/query")
public interface AccountFeignClient {
    @PostMapping("/list")
    AccountListResponse getAccountList(@RequestBody AccountListRequest request);
}
