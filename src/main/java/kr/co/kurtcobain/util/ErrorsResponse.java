package kr.co.kurtcobain.util;

import lombok.Data;
import org.springframework.validation.Errors;

@Data
public class ErrorsResponse {
    private Object errors;
    public ErrorsResponse(Errors errors) {
        this.errors = errors.getAllErrors();
    }
}
