package advice;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import exception.ApiException;
import exception.ApiRequestException;

@ControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(value = {ApiRequestException.class})
  public ResponseEntity<Object> handleApiRequestException(ApiRequestException e) {
    HttpStatus badRequest = HttpStatus.BAD_REQUEST;
    ApiException apiException =
        new ApiException(e.getMessage(), badRequest, ZonedDateTime.now(ZoneId.of("Z")));

    return new ResponseEntity<>(apiException, badRequest);
  }
}
