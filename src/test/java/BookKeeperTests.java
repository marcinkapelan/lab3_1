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
    ProductData productData1;
    RequestItem requestItem1;
    ProductData productData2;
    RequestItem requestItem2;

    @Before
    public void setUp() {
        bookKeeper = new BookKeeper(new InvoiceFactory());

        clientData = new ClientData(Id.generate(), "Test Client");
        invoiceRequest = new InvoiceRequest(clientData);

        productData1 = new ProductDataBuilder()
                .withName("Test Product 1")
                .build();
        requestItem1 = new RequestItemBuilder()
                .withProductData(productData1)
                .build();
        productData2 = new ProductDataBuilder()
                .withName("Test Product 2")
                .build();
        requestItem2 = new RequestItemBuilder()
                .withProductData(productData2)
                .build();
    }

    @Test
    public void requestingInvoiceWithOneElementShouldReturnInvoiceWithOneElement() {
        invoiceRequest.add(requestItem1);

        when(taxPolicyMock.calculateTax(Mockito.<ProductType>any(), Mockito.<Money>any())).thenReturn(new Tax(new Money(75.0), "Mock"));
        assertThat(bookKeeper.issuance(invoiceRequest, taxPolicyMock).getItems().size(), is(1));
    }

    @Test
    public void requestingInvoiceWithTwoElementsShouldCallCalculateTaxMethodTwice() {
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
