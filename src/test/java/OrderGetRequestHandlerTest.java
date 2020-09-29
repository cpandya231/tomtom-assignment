import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.chintan.handler.OrderGetRequestHandler;
import com.chintan.handler.ProductCatalogGetRequestHandler;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class OrderGetRequestHandlerTest {
    private OrderGetRequestHandler handler;

    @Test
    public void handleTest() {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("userId", "u_id");
        request.setPathParameters(pathParameters);
        this.handler = new OrderGetRequestHandler();
        Object output = this.handler.handleRequest(request, null);

    }
}
