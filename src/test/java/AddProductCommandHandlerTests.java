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
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductBuilder;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationBuilder;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.system.application.SystemContext;

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
    @Mock
    ClientRepository clientRepository;

    private AddProductCommandHandler addProductCommandHandler;
    private AddProductCommand addProductCommand;

    private ClientData clientData;
    private Reservation reservation;
    private Product availableProduct;
    private Product unavailableProduct;
    private SystemContext systemContext;

    @Before
    public void setUp() {
        addProductCommandHandler = new AddProductCommandHandler();
        systemContext = new SystemContext();
        Whitebox.setInternalState(addProductCommandHandler, "reservationRepository", reservationRepositoryMock);
        Whitebox.setInternalState(addProductCommandHandler, "productRepository", productRepositoryMock);
        Whitebox.setInternalState(addProductCommandHandler, "suggestionService", suggestionServiceMock);
        Whitebox.setInternalState(addProductCommandHandler, "clientRepository", clientRepository);
        Whitebox.setInternalState(addProductCommandHandler, "systemContext", systemContext);

        addProductCommand = new AddProductCommand(Id.generate(), Id.generate(), 1);
        clientData = new ClientData(Id.generate(), "Test Client");
        reservation = new ReservationBuilder()
                .aggregateId(Id.generate())
                .status(Reservation.ReservationStatus.OPENED)
                .clientData(clientData)
                .createDate(new Date())
                .build();
        availableProduct = new ProductBuilder()
                .aggregateId(Id.generate())
                .price(new Money(50.0))
                .name("Test Product")
                .productType(ProductType.STANDARD)
                .build();
        unavailableProduct = new ProductBuilder()
                .aggregateId(Id.generate())
                .price(new Money(50.0))
                .name("Test Product")
                .productType(ProductType.STANDARD)
                .build();
        unavailableProduct.markAsRemoved();

        when(reservationRepositoryMock.load(Mockito.<Id>any())).thenReturn(reservation);
        when(suggestionServiceMock.suggestEquivalent(Mockito.<Product>any(), Mockito.<Client>any())).thenReturn(availableProduct);
        when(clientRepository.load(Mockito.<Id>any())).thenReturn(new Client());
    }

    @Test
    public void handleShouldInvokeLoadMethodOfReservationRepository() {
        when(productRepositoryMock.load(Mockito.<Id>any())).thenReturn(availableProduct);

        addProductCommandHandler.handle(addProductCommand);

        verify(reservationRepositoryMock, times(1)).load(Mockito.<Id>any());
    }

    @Test
    public void handleShouldInvokeLoadMethodOfProductRepository() {
        when(productRepositoryMock.load(Mockito.<Id>any())).thenReturn(availableProduct);

        addProductCommandHandler.handle(addProductCommand);

        verify(productRepositoryMock, times(1)).load(Mockito.<Id>any());
    }

    @Test
    public void handleForAvailableProductShouldNotInvokeLoadMethodOfSuggestionService() {
        when(productRepositoryMock.load(Mockito.<Id>any())).thenReturn(availableProduct);

        addProductCommandHandler.handle(addProductCommand);

        verify(suggestionServiceMock, times(0)).suggestEquivalent(Mockito.<Product>any(), Mockito.<Client>any());
    }

    @Test
    public void handleForNonAvailableProductShouldInvokeLoadMethodOfSuggestionService() {
        when(productRepositoryMock.load(Mockito.<Id>any())).thenReturn(unavailableProduct);

        addProductCommandHandler.handle(addProductCommand);

        verify(suggestionServiceMock, times(1)).suggestEquivalent(Mockito.<Product>any(), Mockito.<Client>any());
    }

    @Test
    public void reservationShouldContainProductFromCommandAfterInvokingHandle() {
        when(productRepositoryMock.load(Mockito.<Id>any())).thenReturn(availableProduct);

        addProductCommandHandler.handle(addProductCommand);

        assertThat(reservation.contains(availableProduct), is(true));
    }


    @Test
    public void handleShouldInvokeSaveMethodOfReservationRepository() {
        when(productRepositoryMock.load(Mockito.<Id>any())).thenReturn(availableProduct);

        addProductCommandHandler.handle(addProductCommand);

        verify(reservationRepositoryMock, times(1)).save(Mockito.<Reservation>any());
    }

}
