import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommandBuilder;
import pl.com.bottega.ecommerce.sales.application.api.handler.AddProductCommandHandler;
import pl.com.bottega.ecommerce.sales.application.api.handler.AddProductCommandHandlerBuilder;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.system.application.SystemContext;

import java.util.Date;

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
    private Reservation.ReservationStatus reservationStatus;
    private ClientData clientData;
    private ArgumentCaptor<Reservation> argumentCaptor;

    @Before
    public void setup() {
        addProductCommand = new AddProductCommandBuilder().setOrderId(new Id("1")).setProductId(new Id("1")).setQuantity(3).build();
        reservationStatus=Reservation.ReservationStatus.OPENED;
        clientData=new ClientData(new Id("1"),"client");
        reservation = new Reservation(new Id("1"), reservationStatus,clientData, new Date());

        product = new Product(new Id("1"),new Money(100),"product", ProductType.STANDARD);

        argumentCaptor=ArgumentCaptor.forClass(Reservation.class);
        reservationRepository = mock(ReservationRepository.class);
        when(reservationRepository.load(new Id("1"))).thenReturn(reservation);


        productRepository = mock(ProductRepository.class);
        when(productRepository.load(any())).thenReturn(product);

        systemContext = mock(SystemContext.class);

        suggestionService = mock(SuggestionService.class);
        when(suggestionService.suggestEquivalent(product, client)).thenReturn(product);

        addProductCommandHandler = new AddProductCommandHandlerBuilder().setReservationRepository(reservationRepository).setProductRepository(productRepository).setSuggestionService(suggestionService).setClientRepository(clientRepository).setSystemContext(systemContext).build();
    }


    @Test
    public void reservationRepositoryCalledTwoTimesTest() {

        addProductCommandHandler.handle(addProductCommand);
        addProductCommandHandler.handle(addProductCommand);

        verify(reservationRepository, Mockito.times(2)).load(new Id("1"));

    }

    @Test
    public void productRepositoryCalledTwoTimesTest() {

        addProductCommandHandler.handle(addProductCommand);
        addProductCommandHandler.handle(addProductCommand);

        verify(productRepository, Mockito.times(2)).load(new Id("1"));

    }


    @Test
    public void productAvailableTest() {

        Assert.assertTrue(product.isAvailable());

    }

    @Test
    public void resrevationRepositoryGiveReservationTest(){
        addProductCommandHandler.handle(addProductCommand);

        verify(reservationRepository).save(argumentCaptor.capture());

        Assert.assertEquals(reservation,argumentCaptor.getValue());
    }
}
