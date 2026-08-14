package dev.meher.portfolio.order;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
final class OrderController {

    private final QuoteService quoteService;

    OrderController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @PostMapping("/quote")
    Quote quote(@Valid @RequestBody QuoteRequest request) {
        return quoteService.createQuote(request);
    }

    @ExceptionHandler(InsufficientInventoryException.class)
    ResponseEntity<Map<String, Object>> insufficientInventory(InsufficientInventoryException exception) {
        return error(HttpStatus.CONFLICT, exception.getMessage(), Map.of(
                "availableQuantity", exception.availableQuantity()));
    }

    @ExceptionHandler(InventoryUnavailableException.class)
    ResponseEntity<Map<String, Object>> inventoryUnavailable(InventoryUnavailableException exception) {
        return error(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(UnknownSkuException.class)
    ResponseEntity<Map<String, Object>> unknownSku(UnknownSkuException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> invalidRequest(MethodArgumentNotValidException exception) {
        return error(HttpStatus.BAD_REQUEST, "sku is required and quantity must be at least 1", Map.of());
    }

    private ResponseEntity<Map<String, Object>> error(
            HttpStatus status,
            String message,
            Map<String, Object> details) {
        return ResponseEntity.status(status).body(Map.of(
                "status", status.value(),
                "message", message,
                "details", details,
                "timestamp", Instant.now().toString()));
    }
}
