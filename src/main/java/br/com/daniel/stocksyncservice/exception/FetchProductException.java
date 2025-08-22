package br.com.daniel.stocksyncservice.exception;

import br.com.daniel.stocksyncservice.exception.general.marker.InternalServerErrorException;
import br.com.daniel.stocksyncservice.exception.general.StockSyncApplicationException;

public class FetchProductException extends StockSyncApplicationException implements InternalServerErrorException {

    public FetchProductException(String message, Throwable cause) {
        super(message, cause);
    }

}
