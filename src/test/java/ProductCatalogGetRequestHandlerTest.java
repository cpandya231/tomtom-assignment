import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.chintan.handler.ProductCatalogGetRequestHandler;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;


public class ProductCatalogGetRequestHandlerTest {
    private ProductCatalogGetRequestHandler handler;

    @Test
    public void handleTest() {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("searchQuery", "echo");
        request.setQueryStringParameters(queryParam);
        this.handler = new ProductCatalogGetRequestHandler();
        Object output = this.handler.handleRequest(request, null);

    }
}
