package dx.client.api;

import dx.client.impl.EndpointHttpImpl;
import dx.client.impl.EndpointTcpImpl;
import dx.client.impl.EndpointWebsocketImpl;

/**
 * End point instance factory.
 * @author Sean Du
 */
public enum EndpointFactory {
    INSTANCE;


    
    /**
     * Get an endpoint instance.
     * @param serviceProtocol support IEndpoint.SERVICE_PROTOCOL_TCP, IEndpoint.SERVICE_PROTOCOL_TCP_SSL, IEndpoint.SERVICE_PROTOCOL_WEBSOCKET or IEndpoint.SERVICE_PROTOCOL_WEBSOCKET_SSL
     * @return IEndpoint instance.
     * @throws Exception
     */
    public IEndpoint getEndpoint(int serviceProtocol) throws Exception {
        IEndpoint endpoint = null;

        switch (serviceProtocol) {
        case IEndpoint.SERVICE_PROTOCOL_TCP:
            endpoint = new EndpointTcpImpl(false);
            break;
        case IEndpoint.SERVICE_PROTOCOL_TCP_SSL:
        	endpoint = new EndpointTcpImpl(true);
            break;
        case IEndpoint.SERVICE_PROTOCOL_HTTP:
            endpoint = new EndpointHttpImpl(false);
            break;
        case IEndpoint.SERVICE_PROTOCOL_HTTPS:
            endpoint = new EndpointHttpImpl(true);
            break;
        case IEndpoint.SERVICE_PROTOCOL_WEBSOCKET:
            endpoint = new EndpointWebsocketImpl(false);
            break;
        case IEndpoint.SERVICE_PROTOCOL_WEBSOCKET_SSL:
        	endpoint = new EndpointWebsocketImpl(true);
            break;
        default:
            throw new Exception("Not an available service protocol " + serviceProtocol + "!");
        }

        return endpoint;
    }
}
