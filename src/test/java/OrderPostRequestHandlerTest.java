import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.chintan.handler.OrderPostRequestHandler;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class OrderPostRequestHandlerTest {
    private OrderPostRequestHandler handler;

    @Test
    public void handleTest() {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("userId", "s_id");
        request.setPathParameters(pathParams);
        request.setBody("{\"transactionId\":\"t_id\"," +
                "\"status\":\"Placed\"," +
                "\"productInfo\":{\"productType\":\"Shirt\"," +
                "\"productName\":\"Red shirt\"," +
                "\"productId\":\"p_id\"," +
                "\"quantity\":1," +
                "}" +
                "}");
        this.handler = new OrderPostRequestHandler();
        Object output = this.handler.handleRequest(request, null);

    }
}
