import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.chintan.handler.CartPostRequestHandler;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class CartPostRequestHandlerTest {
    private CartPostRequestHandler handler;

    @Test
    public void handleTest() {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("userId", "s_id");
        request.setPathParameters(pathParams);
        request.setBody("{\"products\": [{\"productId\": \"productId\"" +
                ",\"productType\": \"type\"" +
                ",\"productName\": \"Name\"" +
                ",\"quantity\": 1" +
                "}]}");
        this.handler = new CartPostRequestHandler();
        Object output = this.handler.handleRequest(request, null);

    }
}
