import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.application.api.handler.AddProductCommandHandler;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.system.application.SystemContext;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AddProductCommandHandlerTest {

    private AddProductCommand addProductCommand;
    private AddProductCommandHandler addProductCommandHandler;
    private ReservationRepository reservationRepository;
    private ProductRepository productRepository;
    private SuggestionService suggestionService;
    private ClientRepository clientRepository;
    private SystemContext systemContext;
    private Product product;
    private Reservation reservation;
    private Client client;

    @Before
    public void setup() {
        addProductCommand = new AddProductCommand(new Id("1"), new Id("1"), 3);

        reservation = mock(Reservation.class);

        product = mock(Product.class);
        when(product.isAvailable()).thenReturn(true);

        reservationRepository = mock(ReservationRepository.class);
        when(reservationRepository.load(new Id("1"))).thenReturn(reservation);

        productRepository = mock(ProductRepository.class);
        when(productRepository.load(any())).thenReturn(product);

        systemContext = mock(SystemContext.class);

        suggestionService = mock(SuggestionService.class);
        when(suggestionService.suggestEquivalent(product, client)).thenReturn(product);

        addProductCommandHandler = new AddProductCommandHandler(reservationRepository, productRepository, suggestionService,
                clientRepository, systemContext);
    }

    @Test
    public void productAvailableCalledOneTimes() {

        addProductCommandHandler.handle(addProductCommand);

        verify(product, times(1)).isAvailable();
    }

    @Test
    public void productAvaibleCalledTwoTimes() {

        addProductCommandHandler.handle(addProductCommand);
        addProductCommandHandler.handle(addProductCommand);

        verify(product, times(2)).isAvailable();

    }

    @Test
    public void productAvaible() {

        Assert.assertTrue(product.isAvailable());

    }
}
