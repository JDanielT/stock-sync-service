package br.com.daniel.stocksyncservice.exception.advisor;

import br.com.daniel.stocksyncservice.exception.general.StockSyncApplicationException;
import br.com.daniel.stocksyncservice.exception.general.marker.InternalServerErrorException;
import br.com.daniel.stocksyncservice.response.dto.ErrorResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Log4j2
@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(StockSyncApplicationException.class)
    public ResponseEntity<Object> oneRiskApplicationException(StockSyncApplicationException ex) {

        // add other exceptions here
        // BadRequest, Conflict, NotFound ....

        if (ex instanceof InternalServerErrorException) {
            return new ResponseEntity<>(
                    ErrorResponse.builder().message(ex.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return null;

    }

}
