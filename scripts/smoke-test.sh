#!/usr/bin/env bash
set -euo pipefail

portfolio_gateway_url="${PORTFOLIO_GATEWAY_URL:-http://localhost:8080}"
portfolio_discovery_url="${PORTFOLIO_DISCOVERY_URL:-http://localhost:8761}"
portfolio_inventory_url="${PORTFOLIO_INVENTORY_URL:-http://localhost:8081}"
portfolio_order_url="${PORTFOLIO_ORDER_URL:-http://localhost:8082}"

wait_for_url() {
  local label="$1"
  local url="$2"

  for attempt in $(seq 1 60); do
    if curl --fail --silent --show-error "$url" >/dev/null 2>&1; then
      echo "$label is ready"
      return 0
    fi
    sleep 2
  done

  echo "$label did not become ready: $url" >&2
  return 1
}

wait_for_content() {
  local label="$1"
  local url="$2"
  local expected="$3"
  local method="${4:-GET}"
  local body="${5:-}"
  local curl_args=(--fail --silent --show-error --request "$method")

  if [[ -n "$body" ]]; then
    curl_args+=(--header 'Content-Type: application/json' --data "$body")
  fi

  for attempt in $(seq 1 60); do
    if response=$(curl "${curl_args[@]}" "$url" 2>/dev/null) \
      && [[ "$response" == *"$expected"* ]]; then
      echo "$label is working"
      printf '%s' "$response"
      return 0
    fi
    sleep 2
  done

  echo "$label did not return expected content: $url" >&2
  return 1
}

wait_for_url "discovery-server" "$portfolio_discovery_url/actuator/health"
wait_for_url "inventory-service" "$portfolio_inventory_url/actuator/health"
wait_for_url "order-service" "$portfolio_order_url/actuator/health"
wait_for_url "api-gateway" "$portfolio_gateway_url/actuator/health"

inventory_response=$(wait_for_content \
  "gateway inventory route" \
  "$portfolio_gateway_url/api/inventory/items/sku-123" \
  '"sku":"sku-123"')
quote_response=$(wait_for_content \
  "gateway order route" \
  "$portfolio_gateway_url/api/orders/quote" \
  '"total":258.00' \
  POST \
  '{"sku":"sku-123","quantity":2}')

echo "Smoke test passed: discovery, gateway routing, inventory, and service-to-service lookup are working"
