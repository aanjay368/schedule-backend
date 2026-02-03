package id.my.schedule.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import id.my.schedule.model.WebResponse;
import com.fasterxml.jackson.core.JsonParseException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class CustomErrorController  {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<WebResponse<MultiValueMap<String, String>>> constraintViolationException(ConstraintViolationException exceptions) {
        MultiValueMap<String, String> message = new LinkedMultiValueMap<>();

        exceptions.getConstraintViolations().forEach(exception -> {
            message.add(exception.getPropertyPath().toString(), exception.getMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        WebResponse.<MultiValueMap<String, String>>builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .errors(message)
                                .build()
                );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<WebResponse<String>> responseStatusException(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        WebResponse.<String>builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .errors(exception.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<WebResponse<String>> responseStatusException(ResponseStatusException exception) {
        System.out.println(exception.getMessage());
        return ResponseEntity.status(exception.getStatusCode())
                .body(
                        WebResponse.<String>builder()
                                .status(exception.getStatusCode().value())
                                .errors(exception.getReason())
                                .build()
                );
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<WebResponse<String>> jsonParserException(JsonParseException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        WebResponse.<String>builder()
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .errors("Internal Server Error")
                                .build()
                );
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<WebResponse<String>> missingServletRequestPartException(MissingServletRequestPartException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        WebResponse.<String>builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .errors(exception.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<WebResponse<String>> maxUploadSizeExceededException(MaxUploadSizeExceededException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        WebResponse.<String>builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .errors(exception.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<WebResponse<String>> iOException(IOException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        WebResponse.<String>builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .errors(exception.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<WebResponse<Map<String, List<String>>>> httpMessageNotReadableException(HttpMessageNotReadableException exception) {
        Map<String, List<String>> errors = new HashMap<>();

        String message = "Format data tidak valid";

        // Cek jika error disebabkan oleh Enum yang salah
        if (exception.getCause() instanceof InvalidFormatException ifx) {
            String fieldName = ifx.getPath().get(0).getFieldName();
            message = "Pilihan tidak tersedia dalam sistem";
            if (ifx.getTargetType().isEnum()) {
                errors.put(fieldName, List.of(message));
            } else if (ifx.getTargetType().equals(LocalDate.class)) {
                errors.put(fieldName, List.of("Format tanggal tidak valid "));
            } else {
                errors.put(fieldName, List.of("Format data tidak sesuai"));
            }
        } else {
            errors.put("general", List.of("Permintaan JSON tidak dapat dibaca"));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(WebResponse.<Map<String, List<String>>>builder()
                        .errors(errors)
                        .build());
    }
}

