package com.bank.authservice.dto.intergration.exception;

import com.bank.authservice.dto.intergration.response.ErrorResponse;
import com.bank.authservice.exception.SystemException;
import com.bank.authservice.util.ErrorUtil;
import java.util.HashMap;


public class ExternalServiceException extends SystemException {
    public ExternalServiceException(ErrorResponse errorResponse) {
        super(errorResponse.getMessage(),
                errorResponse.getStatus(),
                ErrorUtil.getMetadata(errorResponse)
         );
    }
}
