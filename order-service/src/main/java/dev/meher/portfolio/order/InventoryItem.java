package dev.meher.portfolio.order;

import java.math.BigDecimal;

record InventoryItem(String sku, String name, BigDecimal unitPrice, int availableQuantity) {
}
