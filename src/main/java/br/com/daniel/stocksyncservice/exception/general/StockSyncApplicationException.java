package br.com.daniel.stocksyncservice.exception.general;

public class StockSyncApplicationException extends RuntimeException {

    public StockSyncApplicationException() {}

    public StockSyncApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

}
