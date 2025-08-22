package br.com.daniel.stocksyncservice.exception;

import br.com.daniel.stocksyncservice.exception.general.StockSyncApplicationException;
import br.com.daniel.stocksyncservice.exception.general.marker.InternalServerErrorException;

public class ProductPersistenceException extends StockSyncApplicationException implements InternalServerErrorException {

    public ProductPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

}
