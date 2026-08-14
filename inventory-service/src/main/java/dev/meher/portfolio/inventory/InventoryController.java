package dev.meher.portfolio.inventory;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory/items")
final class InventoryController {

    private final Map<String, InventoryItem> items = Map.of(
            "sku-123", new InventoryItem("sku-123", "Mechanical Keyboard", new BigDecimal("129.00"), 12),
            "sku-456", new InventoryItem("sku-456", "USB-C Dock", new BigDecimal("89.00"), 4),
            "sku-789", new InventoryItem("sku-789", "Studio Headphones", new BigDecimal("179.00"), 0));

    @GetMapping
    List<InventoryItem> items() {
        return items.values().stream()
                .sorted(Comparator.comparing(InventoryItem::sku))
                .toList();
    }

    @GetMapping("/{sku}")
    ResponseEntity<InventoryItem> item(@PathVariable String sku) {
        InventoryItem item = items.get(sku);
        return item == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(item);
    }
}

record InventoryItem(String sku, String name, BigDecimal unitPrice, int availableQuantity) {
}
