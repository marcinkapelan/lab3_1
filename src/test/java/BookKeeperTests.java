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
        ProductData productData = new ProductData(Id.generate(), new Money(50.0), "Test Product", ProductType.STANDARD, new Date());
        RequestItem requestItem = new RequestItem(productData, 1, productData.getPrice());

        invoiceRequest.add(requestItem);

        when(taxPolicyMock.calculateTax(Mockito.<ProductType>any(), Mockito.<Money>any())).thenReturn(new Tax(new Money(75.0), "Mock"));
        assertThat(bookKeeper.issuance(invoiceRequest, taxPolicyMock).getItems().size(), is(1));
    }

    @Test
    public void requestingInvoiceWithTwoElementsShouldCallCalculateTaxMethodTwice() {
        ProductData productData1 = new ProductData(Id.generate(), new Money(50.0), "Test Product1", ProductType.STANDARD, new Date());
        RequestItem requestItem1 = new RequestItem(productData1, 1, productData1.getPrice());

        ProductData productData2 = new ProductData(Id.generate(), new Money(35.0), "Test Product2", ProductType.STANDARD, new Date());
        RequestItem requestItem2 = new RequestItem(productData2, 1, productData2.getPrice());

        invoiceRequest.add(requestItem1);
        invoiceRequest.add(requestItem2);

        when(taxPolicyMock.calculateTax(Mockito.<ProductType>any(), Mockito.<Money>any())).thenReturn(new Tax(new Money(75.0), "Mock"));

        bookKeeper.issuance(invoiceRequest, taxPolicyMock);

        verify(taxPolicyMock, times(2)).calculateTax(Mockito.<ProductType>any(), Mockito.<Money>any());
    }
}
