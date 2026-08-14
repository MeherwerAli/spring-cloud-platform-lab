package dev.meher.portfolio.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class InventoryControllerTest {

    private final InventoryController controller = new InventoryController();

    @Test
    void listsInventoryInStableSkuOrder() {
        assertEquals(3, controller.items().size());
        assertEquals("sku-123", controller.items().get(0).sku());
    }

    @Test
    void returnsNotFoundForUnknownSku() {
        assertTrue(controller.item("missing").getStatusCode().is4xxClientError());
    }
}
