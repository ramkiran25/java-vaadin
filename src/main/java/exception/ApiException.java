package exception;

import java.time.ZonedDateTime;
import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiException {
  private String message;
  private HttpStatus httpStatus;
  private ZonedDateTime timeStamp;
}
