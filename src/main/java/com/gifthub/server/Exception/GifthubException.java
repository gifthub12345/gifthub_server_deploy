package com.gifthub.server.Exception;

public class GifthubException extends RuntimeException {
    protected GifthubException() {
        super();
    }

    protected GifthubException(Exception e) {
        super(e);
    }

    protected GifthubException(String message) {
        super(message);
    }
}
