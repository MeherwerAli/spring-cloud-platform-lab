package dev.meher.portfolio.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class QuoteServiceTest {

    @Test
    void calculatesAQuoteFromDiscoveredInventory() {
        InventoryClient inventory = sku -> new InventoryItem(
                sku, "Mechanical Keyboard", new BigDecimal("129.00"), 12);
        QuoteService service = new QuoteService(inventory);

        Quote quote = service.createQuote(new QuoteRequest("sku-123", 2));

        assertEquals(new BigDecimal("258.00"), quote.total());
        assertEquals("available", quote.status());
    }

    @Test
    void rejectsAQuoteWhenStockIsTooLow() {
        InventoryClient inventory = sku -> new InventoryItem(
                sku, "USB-C Dock", new BigDecimal("89.00"), 1);
        QuoteService service = new QuoteService(inventory);

        InsufficientInventoryException exception = assertThrows(
                InsufficientInventoryException.class,
                () -> service.createQuote(new QuoteRequest("sku-456", 2)));

        assertEquals(1, exception.availableQuantity());
    }
}
