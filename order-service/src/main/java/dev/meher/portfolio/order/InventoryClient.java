package dev.meher.portfolio.order;

interface InventoryClient {

    InventoryItem findBySku(String sku);
}
