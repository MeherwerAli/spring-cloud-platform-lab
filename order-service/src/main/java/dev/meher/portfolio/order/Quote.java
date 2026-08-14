package dev.meher.portfolio.order;

import java.math.BigDecimal;

record Quote(
        String sku,
        String itemName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal total,
        String status) {
}
