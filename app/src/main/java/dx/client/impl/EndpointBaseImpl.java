package dx.client.impl;

import dx.client.api.IEndpoint;
import dx.client.api.IEndpointListener;

public abstract class EndpointBaseImpl implements IEndpoint{

    protected IEndpointListener listener = null;
    protected String eid = null; // end point id, an unique id for an end point = key.id
	
	@Override
    public void registerEndpointListener(IEndpointListener listener) {
		this.listener = listener;
	}


	@Override
	public abstract void open(String address) throws Exception;


	@Override
	public void send(byte[] data) throws Exception {
        sendBinary(data);
	}

	@Override
	public void send(String message) throws Exception {
		sendText(message);
	}

	@Override
	public abstract void close() throws Exception ;
	
	////////////////////////////////////////////////////////////////////////////
	protected abstract void sendText(String json) throws Exception;

	protected abstract void sendBinary(byte[] data)  throws Exception;
    
}
