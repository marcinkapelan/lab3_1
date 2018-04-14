import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.application.api.handler.AddProductCommandHandler;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddProductCommandHandlerTests {

    @Mock
    ReservationRepository reservationRepositoryMock;
    @Mock
    ProductRepository productRepositoryMock;
    @Mock
    SuggestionService suggestionServiceMock;

    private AddProductCommandHandler addProductCommandHandler;
    private AddProductCommand addProductCommand;

    private ClientData clientData;
    private Reservation reservation;
    private Product product;

    @Before
    public void setUp() {
        addProductCommandHandler = new AddProductCommandHandler();
        Whitebox.setInternalState(addProductCommandHandler, "reservationRepository", reservationRepositoryMock);
        Whitebox.setInternalState(addProductCommandHandler, "productRepository", productRepositoryMock);
        Whitebox.setInternalState(addProductCommandHandler, "suggestionService", suggestionServiceMock);

        addProductCommand = new AddProductCommand(Id.generate(), Id.generate(), 1);
        clientData = new ClientData(Id.generate(), "Test Client");
        reservation = new Reservation(Id.generate(), Reservation.ReservationStatus.OPENED, clientData, new Date());
        product = new Product(Id.generate(), new Money(50.0), "Test Product", ProductType.STANDARD);
    }

    @Test
    public void handleShouldInvokeLoadMethodOfReservationRepository() {
        when(reservationRepositoryMock.load(Mockito.<Id>any())).thenReturn(reservation);
        when(productRepositoryMock.load(Mockito.<Id>any())).thenReturn(product);
        when(suggestionServiceMock.suggestEquivalent(Mockito.<Product>any(), Mockito.<Client>any())).thenReturn(product);

        addProductCommandHandler.handle(addProductCommand);

        verify(reservationRepositoryMock, times(1)).load(Mockito.<Id>any());
    }

    @Test
    public void handleShouldInvokeLoadMethodOfProductRepository() {
        when(reservationRepositoryMock.load(Mockito.<Id>any())).thenReturn(reservation);
        when(productRepositoryMock.load(Mockito.<Id>any())).thenReturn(product);
        when(suggestionServiceMock.suggestEquivalent(Mockito.<Product>any(), Mockito.<Client>any())).thenReturn(product);

        addProductCommandHandler.handle(addProductCommand);

        verify(productRepositoryMock, times(1)).load(Mockito.<Id>any());
    }

    @Test
    public void handleForAvailableProductShouldNotInvokeLoadMethodOfSuggestionService() {
        when(reservationRepositoryMock.load(Mockito.<Id>any())).thenReturn(reservation);
        when(productRepositoryMock.load(Mockito.<Id>any())).thenReturn(product);
        when(suggestionServiceMock.suggestEquivalent(Mockito.<Product>any(), Mockito.<Client>any())).thenReturn(product);

        addProductCommandHandler.handle(addProductCommand);

        verify(suggestionServiceMock, times(0)).suggestEquivalent(Mockito.<Product>any(), Mockito.<Client>any());
    }

    @Test
    public void reservationShouldContainProductFromCommandAfterInvokingHandle() {
        when(reservationRepositoryMock.load(Mockito.<Id>any())).thenReturn(reservation);
        when(productRepositoryMock.load(Mockito.<Id>any())).thenReturn(product);
        when(suggestionServiceMock.suggestEquivalent(Mockito.<Product>any(), Mockito.<Client>any())).thenReturn(product);

        addProductCommandHandler.handle(addProductCommand);

        assertThat(reservation.contains(product), is(true));
    }


    @Test
    public void handleShouldInvokeSaveMethodOfReservationRepository() {
        when(reservationRepositoryMock.load(Mockito.<Id>any())).thenReturn(reservation);
        when(productRepositoryMock.load(Mockito.<Id>any())).thenReturn(product);

        addProductCommandHandler.handle(addProductCommand);

        verify(reservationRepositoryMock, times(1)).save(Mockito.<Reservation>any());
    }

}
