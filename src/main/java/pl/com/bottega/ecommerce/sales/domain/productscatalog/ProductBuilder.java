package pl.com.bottega.ecommerce.sales.domain.productscatalog;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sharedkernel.Money;

public class ProductBuilder {
    private Id id = Id.generate();
    private Money price = new Money(50.0);
    private String name = "Default Product";
    private ProductType productType = ProductType.STANDARD;

    public ProductBuilder() {
    }

    public ProductBuilder(Id aggregateId, Money price, String name, ProductType productType){
        this.id = aggregateId;
        this.price = price;
        this.name = name;
        this.productType = productType;
    }

    public ProductBuilder withAggregateId(Id aggregateId){
        this.id = aggregateId;
        return this;
    }

    public ProductBuilder withPrice(Money price){
        this.price = price;
        return this;
    }

    public ProductBuilder withName(String name){
        this.name = name;
        return this;
    }

    public ProductBuilder withProductType(ProductType productType){
        this.productType = productType;
        return this;
    }

    public Product build(){
        return new Product(id, price, name, productType);
    }
}
