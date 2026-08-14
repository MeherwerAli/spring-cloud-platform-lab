package dev.meher.portfolio.order;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
final class DiscoveryInventoryClient implements InventoryClient {

    private final LoadBalancerClient loadBalancerClient;
    private final RestClient restClient = RestClient.create();

    DiscoveryInventoryClient(LoadBalancerClient loadBalancerClient) {
        this.loadBalancerClient = loadBalancerClient;
    }

    @Override
    public InventoryItem findBySku(String sku) {
        ServiceInstance instance = loadBalancerClient.choose("inventory-service");
        if (instance == null) {
            throw new InventoryUnavailableException(sku);
        }

        try {
            return restClient.get()
                    .uri(instance.getUri() + "/inventory/items/{sku}", sku)
                    .retrieve()
                    .body(InventoryItem.class);
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().value() == 404) {
                throw new UnknownSkuException(sku);
            }
            throw new InventoryUnavailableException(sku, exception);
        } catch (RestClientException exception) {
            throw new InventoryUnavailableException(sku, exception);
        }
    }
}
