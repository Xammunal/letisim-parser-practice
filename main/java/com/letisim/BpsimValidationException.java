package com.letisim;

/**
 * Кастомное исключение, которое выбрасывается при неудачной XSD-валидации
 * входящего BPSim XML-документа.
 *
 * <p>Содержит исходную причину (cause) для диагностики, а также
 * человекочитаемое сообщение об ошибке валидации.</p>
 */
public class BpsimValidationException extends RuntimeException {

    public BpsimValidationException(String message) {
        super(message);
    }

    public BpsimValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
