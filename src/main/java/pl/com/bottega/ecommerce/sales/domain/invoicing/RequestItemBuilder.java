package pl.com.bottega.ecommerce.sales.domain.invoicing;

import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sharedkernel.Money;

public class RequestItemBuilder {

    private ProductData productData;
    private int quantity;
    private Money totalCost;

    public RequestItemBuilder() {
    }

    public RequestItemBuilder(ProductData productData, int quantity, Money totalCost) {
        this.productData = productData;
        this.quantity = quantity;
        this.totalCost = totalCost;
    }

    public RequestItemBuilder productData(ProductData productData) {
        this.productData = productData;
        return this;
    }

    public RequestItemBuilder quantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public RequestItemBuilder totalCost(Money totalCost) {
        this.totalCost = totalCost;
        return this;
    }

    public RequestItem build() {
        return new RequestItem(productData, quantity, totalCost);
    }
}
