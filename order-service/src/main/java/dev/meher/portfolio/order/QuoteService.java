package dev.meher.portfolio.order;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
final class QuoteService {

    private final InventoryClient inventoryClient;

    QuoteService(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    Quote createQuote(QuoteRequest request) {
        InventoryItem item = inventoryClient.findBySku(request.sku());
        if (item == null) {
            throw new InventoryUnavailableException(request.sku());
        }
        if (item.availableQuantity() < request.quantity()) {
            throw new InsufficientInventoryException(item.availableQuantity());
        }

        BigDecimal total = item.unitPrice().multiply(BigDecimal.valueOf(request.quantity()));
        return new Quote(
                item.sku(),
                item.name(),
                request.quantity(),
                item.unitPrice(),
                total,
                "available");
    }
}
final class InventoryUnavailableException extends RuntimeException {

    InventoryUnavailableException(String sku) {
        super("Inventory is unavailable for " + sku);
    }

    InventoryUnavailableException(String sku, Throwable cause) {
        super("Inventory is unavailable for " + sku, cause);
    }
}

final class UnknownSkuException extends RuntimeException {

    UnknownSkuException(String sku) {
        super("Unknown inventory item: " + sku);
    }
}

final class InsufficientInventoryException extends RuntimeException {

    private final int availableQuantity;

    InsufficientInventoryException(int availableQuantity) {
        super("Requested quantity exceeds available inventory");
        this.availableQuantity = availableQuantity;
    }

    int availableQuantity() {
        return availableQuantity;
    }
}
