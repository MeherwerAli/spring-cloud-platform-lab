package dev.meher.portfolio.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

record QuoteRequest(@NotBlank String sku, @Min(1) int quantity) {
}
