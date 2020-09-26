import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.chintan.handler.CartGetRequestHandler;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class CartGetRequestHandlerTest {
    private CartGetRequestHandler handler;

    @Test
    public void handleTest() {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("userId", "u_id");
        request.setQueryStringParameters(queryParam);
        this.handler = new CartGetRequestHandler();
        Object output = this.handler.handleRequest(request, null);

    }
}
