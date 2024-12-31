package br.com.example.examples.exception;

import br.com.example.examples.util.UniqueTrackingNumberFilter;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ControllerAdvice
public class ControlExceptionHandler {

    public static final String X_RD_TRACEID = "X-rd-traceid";
    public static final String CONSTRAINT_VALIDATION_FAILED = "Constraint validation failed";
    public static final String BIND_EXCEPTION = "A bind exception occurred - Submitted value must match a predefined value";

    @ExceptionHandler(value = { BusinessException.class})
    protected ResponseEntity<Object> handleConflict(BusinessException ex, WebRequest request) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_RD_TRACEID,this.getTraceID());
        return ResponseEntity.status(ex.getHttpStatusCode()).headers(responseHeaders).body(ex.getOnlyBody());

    }

    @ExceptionHandler({ Throwable.class })
    public ResponseEntity<Object> handleException(Throwable eThrowable) {

        BusinessException ex = BusinessException.builder()
                .httpStatusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(Optional.ofNullable(eThrowable.getMessage()).orElse(eThrowable.toString()))
                .description(ExceptionResolver.getRootException(eThrowable))
                .build();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_RD_TRACEID,this.getTraceID());

        return ResponseEntity.status(ex.getHttpStatusCode()).headers(responseHeaders).body(ex.getOnlyBody());

    }

    /**
     *
     * @param request
     * @return
     */
    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exMethod,
                                                                   WebRequest request) {

        String error = exMethod.getName() + " should be ";
        error = error.concat(Optional.ofNullable(exMethod.getRequiredType()).map(Class::getName).orElse(""));

        BusinessException ex = BusinessException.builder()
                .httpStatusCode(HttpStatus.BAD_REQUEST)
                .message(CONSTRAINT_VALIDATION_FAILED)
                .description(error)
                .build();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_RD_TRACEID, this.getTraceID());

        return ResponseEntity.status(ex.getHttpStatusCode()).headers(responseHeaders).body(ex.getOnlyBody());
    }

    /**
     *
     * @param exMethod
     * @param request
     * @return
     */
    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException exMethod, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : exMethod.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getSimpleName() + " " + violation.getPropertyPath() + ": "
                    + violation.getMessage());
        }

        BusinessException ex = BusinessException.builder()
                .httpStatusCode(HttpStatus.BAD_REQUEST)
                .message(CONSTRAINT_VALIDATION_FAILED)
                .description(errors.toString())
                .build();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_RD_TRACEID,this.getTraceID());

        return ResponseEntity.status(ex.getHttpStatusCode()).headers(responseHeaders).body(ex.getOnlyBody());
    }

    /**
     *
     * @param exMethod
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> validationError(MethodArgumentNotValidException exMethod) {

        BindingResult bindingResult = exMethod.getBindingResult();

        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        List<String> fieldErrorDtos = fieldErrors.stream()
                .map(f -> f.getField().concat(":").concat(f.getDefaultMessage())).map(String::new)
                .collect(Collectors.toList());

        BusinessException ex = BusinessException.builder()
                .httpStatusCode(HttpStatus.BAD_REQUEST)
                .message(CONSTRAINT_VALIDATION_FAILED)
                .description(fieldErrorDtos.toString())
                .build();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_RD_TRACEID,this.getTraceID());

        return ResponseEntity.status(ex.getHttpStatusCode()).headers(responseHeaders).body(ex.getOnlyBody());
    }

    /**
     *
     * @param exMethod
     * @return
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> validationError(HttpMessageNotReadableException exMethod) {

        Throwable mostSpecificCause = exMethod.getMostSpecificCause();
        BusinessException ex = BusinessException.builder()
                .httpStatusCode(HttpStatus.BAD_REQUEST)
                .message(CONSTRAINT_VALIDATION_FAILED)
                .description(mostSpecificCause.getMessage())
                .build();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_RD_TRACEID,this.getTraceID());

        return ResponseEntity.status(ex.getHttpStatusCode()).headers(responseHeaders).body(ex.getOnlyBody());
    }

    @ExceptionHandler({ MissingServletRequestParameterException.class })
    public ResponseEntity<Object> handleException(MissingServletRequestParameterException e) {

        BusinessException ex = BusinessException.builder()
                .httpStatusCode(HttpStatus.BAD_REQUEST)
                .message(Optional.ofNullable(e.getMessage()).orElse(e.toString()))
                .description(ExceptionResolver.getRootException(e))
                .build();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_RD_TRACEID,this.getTraceID());

        return ResponseEntity.status(ex.getHttpStatusCode()).headers(responseHeaders).body(ex.getOnlyBody());

    }

    @ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
    public ResponseEntity<Object> handleException(HttpRequestMethodNotSupportedException  e) {

        BusinessException ex = BusinessException.builder()
                .httpStatusCode(HttpStatus.METHOD_NOT_ALLOWED)
                .message(Optional.ofNullable(e.getMessage()).orElse(e.toString()))
                .description(ExceptionResolver.getRootException(e))
                .build();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_RD_TRACEID,this.getTraceID());

        return ResponseEntity.status(ex.getHttpStatusCode()).headers(responseHeaders).body(ex.getOnlyBody());
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<Object> handleException(final NotFoundException e) {
        BusinessException ex = BusinessException.builder()
                .httpStatusCode(HttpStatus.NOT_FOUND)
                .message(Optional.ofNullable(e.getMessage()).orElse(e.toString()))
                .description(ExceptionResolver.getRootException(e))
                .build();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_RD_TRACEID,this.getTraceID());

        return ResponseEntity.status(ex.getHttpStatusCode()).headers(responseHeaders).body(ex.getOnlyBody());
    }

    @ExceptionHandler({NonUniqueValueException.class})
    public ResponseEntity<Object> handleException(final NonUniqueValueException e) {
        BusinessException ex = BusinessException.builder()
                .httpStatusCode(HttpStatus.CONFLICT)
                .message(Optional.ofNullable(e.getMessage()).orElse(e.toString()))
                .description(ExceptionResolver.getRootException(e))
                .build();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_RD_TRACEID,this.getTraceID());

        return ResponseEntity.status(ex.getHttpStatusCode()).headers(responseHeaders).body(ex.getOnlyBody());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Object> handleBindException(BindException e) {

        BusinessException ex = BusinessException.builder()
                .httpStatusCode(HttpStatus.BAD_REQUEST)
                .message(BIND_EXCEPTION)
                .build();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(X_RD_TRACEID,this.getTraceID());

        return ResponseEntity.status(ex.getHttpStatusCode()).headers(responseHeaders).body(ex.getOnlyBody());
    }

    private String getTraceID() {
        String traceId = Optional.ofNullable(MDC.get(UniqueTrackingNumberFilter.TRACE_ID_KEY)).orElse("");
        traceId = Optional.ofNullable(traceId.trim().isEmpty()?null:traceId).orElse("not available");

        return traceId;
    }
}
