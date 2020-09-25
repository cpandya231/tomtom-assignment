import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.chintan.handler.ProductPostRequestHandler;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class ProductPostRequestHandlerTest {
    private ProductPostRequestHandler handler;

    @Test
    public void handleTest() {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("sellerId", "s_id");
        request.setPathParameters(pathParams);
        request.setBody("{\"productType\":\"shirt\"," +
                "\"productName\":\"Red shirt\"," +
                "\"description\":\"Red shirt\"," +
                "\"sku\":\"ABCDEG\"," +
                "\"imageUrl\":\"temp\"," +
                "\"price\":\"100\"," +
                "\"attributes\":{\"size\":\"M\"" + "}" +
                "}");
        this.handler = new ProductPostRequestHandler();
        Object output = this.handler.handleRequest(request, null);

    }
}
