package kielce.tu.weaii.telelearn.configuration;

import kielce.tu.weaii.telelearn.exceptions.AuthorizationException;
import kielce.tu.weaii.telelearn.exceptions.BusinessLogicException;
import kielce.tu.weaii.telelearn.exceptions.NotFoundException;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler extends DefaultHandlerExceptionResolver {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> badCredentialsExceptionHandler(BadCredentialsException ex) {
        ErrorResponse errors = new ErrorResponse(LocalDateTime.now(), "Nieporawne dane logowania");
        return new ResponseEntity<>(errors, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> badCredentialsExceptionHandler(DisabledException ex) {
        ErrorResponse errors = new ErrorResponse(LocalDateTime.now(), "Konto zostało zablokowane");
        return new ResponseEntity<>(errors, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorResponse> authorizationExceptionHandler(AuthorizationException ex) {
        ErrorResponse errors = new ErrorResponse(LocalDateTime.now(), ex.getMessage());
        log.error(String.format("User %s has not permission to resource %s with id %s", ex.getUserId(), ex.getResourceName(), ex.getResourceId()));
        return new ResponseEntity<>(errors, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundExceptionHandler(NotFoundException ex) {
        ErrorResponse errors = new ErrorResponse(LocalDateTime.now(), ex.getMessage());
        log.error(ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ErrorResponse> businessLogicExceptionHandler(BusinessLogicException ex) {
        ErrorResponse errors = new ErrorResponse(LocalDateTime.now(), ex.getMessage());
        log.error(ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> radiationException(MethodArgumentNotValidException ex) {
        String errorMessage = getFieldErrorMessages(ex);
        return new ResponseEntity<>(new ErrorResponse(LocalDateTime.now(),
                errorMessage),
                HttpStatus.BAD_REQUEST);
    }

    private String getFieldErrorMessages(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(". ", "", "."));
    }

    @Value
    private static class ErrorResponse {
        LocalDateTime timestamp;
        String message;
    }
}
