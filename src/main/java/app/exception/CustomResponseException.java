package app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

public class CustomResponseException extends RuntimeException {

    private static final long serialVersionUID = -7247945937653260877L;

    private final ResponseEntity<?> responseEntity;

    public CustomResponseException(ResponseEntity<?> responseEntity) {
        this.responseEntity = responseEntity;
    }

    public CustomResponseException(HttpStatus status) {
        this.responseEntity = new ResponseEntity<Object>(status);
    }

    public <T> CustomResponseException(@Nullable T body, HttpStatus status) {
        this.responseEntity = new ResponseEntity<T>(body, status);
    }

    public CustomResponseException(MultiValueMap<String, String> headers, HttpStatus status) {
        this.responseEntity = new ResponseEntity<Object>(headers, status);
    }

    public <T> CustomResponseException(@Nullable T body, @Nullable MultiValueMap<String, String> headers,
            HttpStatus status) {
        this.responseEntity = new ResponseEntity<T>(body, headers, status);
    }

    public <T> CustomResponseException(@Nullable T body, @Nullable MultiValueMap<String, String> headers,
            int rawStatus) {
        this.responseEntity = new ResponseEntity<T>(body, headers, rawStatus);
    }

    public ResponseEntity<?> getResponseEntity() {
        return responseEntity;
    }

}
