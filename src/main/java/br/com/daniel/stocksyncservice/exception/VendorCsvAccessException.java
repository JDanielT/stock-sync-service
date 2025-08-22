package br.com.daniel.stocksyncservice.exception;

import br.com.daniel.stocksyncservice.exception.general.StockSyncApplicationException;
import br.com.daniel.stocksyncservice.exception.general.marker.InternalServerErrorException;

public class VendorCsvAccessException extends StockSyncApplicationException implements InternalServerErrorException {

    public VendorCsvAccessException(String message, Throwable cause) {
        super(message, cause);
    }

}
