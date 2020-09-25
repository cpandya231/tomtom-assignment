import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.chintan.handler.ProductPostRequestHandler;
import org.junit.Test;


public class ProductPostRequestHandlerTest {
    private ProductPostRequestHandler handler;

    @Test
    public void handleTest() {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setBody("{\"productType\":\"shirt\"," +
                "\"productName\":\"Red shirt\"}");
        this.handler = new ProductPostRequestHandler();
        Object output = this.handler.handleRequest(request, null);

    }
}
