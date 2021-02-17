package com.gis.sucursales.exceptionHandler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;


/**
 * Catch Exceptions and build a message for the REST client
 *
 */

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(value={
            Exception.class
    })
    public ResponseEntity<Object> handleCustomException(Exception ex, WebRequest request) {

        logger.error(org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(ex));

        HttpHeaders headers = new HttpHeaders();
        HttpStatus status;

        if (ex instanceof EntityNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex instanceof EntityExistsException) {
            status = HttpStatus.CONFLICT;
        } else if (ex instanceof DataIntegrityViolationException) {
            status = HttpStatus.CONFLICT;
        } else {

            status = HttpStatus.INTERNAL_SERVER_ERROR;
            return handleExceptionInternal(ex, null, headers, status, request);
        }

        return handleExceptionInternal(ex, buildRestError(ex, status), headers, status, request);
    }


    @ExceptionHandler(value = { IllegalArgumentException.class})
    protected ResponseEntity<Object> handleIllegalArgumentException(final RuntimeException ex, final WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        return handleExceptionInternal(ex, buildRestError(ex, HttpStatus.BAD_REQUEST), headers, HttpStatus.BAD_REQUEST, request);
    }


    private RestError buildRestError(Exception ex, HttpStatus status) {
        RestError.Builder builder = new RestError.Builder();
        builder.setCode(status.value())
                .setStatus(status.getReasonPhrase())
                .setMessage(ex.getMessage())
                .setDeveloperMessage(org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(ex));

        return builder.build();
    }

}