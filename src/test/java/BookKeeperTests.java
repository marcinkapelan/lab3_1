import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.invoicing.*;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductDataBuilder;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BookKeeperTests {

    @Mock
    TaxPolicy taxPolicyMock;

    BookKeeper bookKeeper;
    ClientData clientData;
    InvoiceRequest invoiceRequest;

    @Before
    public void setUp() {
        bookKeeper = new BookKeeper(new InvoiceFactory());

        clientData = new ClientData(Id.generate(), "Test Client");
        invoiceRequest = new InvoiceRequest(clientData);
    }

    @Test
    public void requestingInvoiceWithOneElementShouldReturnInvoiceWithOneElement() {
        ProductData productData = new ProductDataBuilder()
                .productId(Id.generate())
                .price(new Money(50.0))
                .name("Test Product")
                .type(ProductType.STANDARD)
                .snapshotDate(new Date())
                .build();

        RequestItem requestItem = new RequestItemBuilder()
                .productData(productData)
                .quantity(1)
                .totalCost(productData.getPrice())
                .build();

        invoiceRequest.add(requestItem);

        when(taxPolicyMock.calculateTax(Mockito.<ProductType>any(), Mockito.<Money>any())).thenReturn(new Tax(new Money(75.0), "Mock"));
        assertThat(bookKeeper.issuance(invoiceRequest, taxPolicyMock).getItems().size(), is(1));
    }

    @Test
    public void requestingInvoiceWithTwoElementsShouldCallCalculateTaxMethodTwice() {
        ProductData productData1 = new ProductDataBuilder()
                .productId(Id.generate())
                .price(new Money(50.0))
                .name("Test Product1")
                .type(ProductType.STANDARD)
                .snapshotDate(new Date())
                .build();

        RequestItem requestItem1 = new RequestItemBuilder()
                .productData(productData1)
                .quantity(1)
                .totalCost(productData1.getPrice())
                .build();

        ProductData productData2 = new ProductDataBuilder()
                .productId(Id.generate())
                .price(new Money(35.0))
                .name("Test Product2")
                .type(ProductType.STANDARD)
                .snapshotDate(new Date())
                .build();

        RequestItem requestItem2 = new RequestItemBuilder()
                .productData(productData2)
                .quantity(1)
                .totalCost(productData2.getPrice())
                .build();

        invoiceRequest.add(requestItem1);
        invoiceRequest.add(requestItem2);

        when(taxPolicyMock.calculateTax(Mockito.<ProductType>any(), Mockito.<Money>any())).thenReturn(new Tax(new Money(75.0), "Mock"));

        bookKeeper.issuance(invoiceRequest, taxPolicyMock);

        verify(taxPolicyMock, times(2)).calculateTax(Mockito.<ProductType>any(), Mockito.<Money>any());
    }

    @Test
    public void requestingInvoiceWithoutElementsShouldReturnInvoiceWithoutElements() {
        when(taxPolicyMock.calculateTax(Mockito.<ProductType>any(), Mockito.<Money>any())).thenReturn(new Tax(new Money(75.0), "Mock"));
        assertThat(bookKeeper.issuance(invoiceRequest, taxPolicyMock).getItems().size(), is(0));
    }

    @Test
    public void requestingInvoiceWithoutElementsShouldNotCallCalculateTaxMethod() {
        when(taxPolicyMock.calculateTax(Mockito.<ProductType>any(), Mockito.<Money>any())).thenReturn(new Tax(new Money(75.0), "Mock"));

        bookKeeper.issuance(invoiceRequest, taxPolicyMock);

        verify(taxPolicyMock, times(0)).calculateTax(Mockito.<ProductType>any(), Mockito.<Money>any());
    }
}
