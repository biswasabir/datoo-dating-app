package com.angopapo.datooapp.modules.merlin;

import java.io.IOException;

class RequestException extends RuntimeException {

    RequestException(Throwable e) {
        super(e);
    }

    boolean causedByIO() {
        return getCause() instanceof IOException;
    }
}
