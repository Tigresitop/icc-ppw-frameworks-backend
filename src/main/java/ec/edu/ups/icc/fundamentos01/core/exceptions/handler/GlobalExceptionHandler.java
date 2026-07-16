package ec.edu.ups.icc.fundamentos01.core.exceptions.handler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.BindException;

import ec.edu.ups.icc.fundamentos01.core.exceptions.base.ApplicationException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(ApplicationException.class)
        public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException ex, HttpServletRequest request) {
                ErrorResponse response = new ErrorResponse(ex.getStatus(), ex.getMessage(), request.getRequestURI());
                return ResponseEntity.status(ex.getStatus()).body(response);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors().forEach(error -> 
                        errors.put(error.getField(), error.getDefaultMessage())
                );

                ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST, "Datos de entrada inválidos", request.getRequestURI(), errors);
                return ResponseEntity.badRequest().body(response);
        }

        @ExceptionHandler(BindException.class)
        public ResponseEntity<ErrorResponse> handleBindException(
                BindException ex,
                HttpServletRequest request
        ) {
                Map<String, String> errors = new HashMap<>();

                ex.getBindingResult()
                        .getFieldErrors()
                        .forEach(error ->
                                errors.put(error.getField(), error.getDefaultMessage())
                        );

                ErrorResponse response = new ErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        "Parámetros de consulta inválidos",
                        request.getRequestURI(),
                        errors
                );

                return ResponseEntity
                        .badRequest()
                        .body(response);
        }

        @ExceptionHandler(org.springframework.security.authorization.AuthorizationDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(
                org.springframework.security.authorization.AuthorizationDeniedException ex,
                HttpServletRequest request) {
                ErrorResponse response = new ErrorResponse(
                        HttpStatus.FORBIDDEN,
                        "No tienes permisos para acceder a este recurso",
                        request.getRequestURI());

                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(response);
        }

        @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDeniedException(
                org.springframework.security.access.AccessDeniedException ex,
                HttpServletRequest request) {
                        
                String message = ex.getMessage();

                if (message == null || message.isBlank()) {
                message = "Acceso denegado";
                }

                ErrorResponse response = new ErrorResponse(
                        HttpStatus.FORBIDDEN,
                        message, 
                        request.getRequestURI());

                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(response);
        }

        @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
        public ResponseEntity<ErrorResponse> handleAuthenticationException(
                org.springframework.security.core.AuthenticationException ex,
                HttpServletRequest request) {
                ErrorResponse response = new ErrorResponse(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciales inválidas o sesión expirada",
                        request.getRequestURI());

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(response);
        }

        
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex, HttpServletRequest request) {
                logger.error("Error no controlado: {}", ex.getMessage(), ex);
                ErrorResponse response = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", request.getRequestURI());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
}