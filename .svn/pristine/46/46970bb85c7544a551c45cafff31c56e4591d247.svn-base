package dx.client.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.SSLSocketFactory;

import dx.client.api.IEndpoint;
import dx.client.api.IEndpointListener;
import dx.client.impl.ssl.EasySslClientContextFactory;
import dx.client.impl.ssl.SslClientContextFactory;
import dx.client.tool.CliCfg;

public class EndpointHttpImpl implements IEndpoint{

	private int httpTimeout = 5000;
	
	private boolean isSSL = false;
	private IEndpointListener listener = null;
    private String url;
    
    public EndpointHttpImpl(boolean _isSSL){
    	this.isSSL = _isSSL;
    }
	
	@Override
    public void registerEndpointListener(IEndpointListener listener) {
		this.listener = listener;
	}

	@Override
	public void open(String address) throws Exception {
		this.url = address;
        if(this.isSSL){
	    	SSLSocketFactory sslSocketFactory;
	    	if(CliCfg.trustAll){
	    		sslSocketFactory = EasySslClientContextFactory.getInstance().sslContext().getSocketFactory();
	    	}else{
	    		sslSocketFactory = SslClientContextFactory.getInstance().sslContext().getSocketFactory();
	    	}
	    	javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
        }
	}

	@Override
	public void send(byte[] data) throws Exception {
		String rsp = callHttpPost(this.url, new String(data));
		listener.onReceive(rsp.getBytes());
	}

	@Override
	public void send(String message) throws Exception {
		String rsp = callHttpPost(this.url, message);
		listener.onReceive(rsp,8080);
	}

	@Override
	public void ping() throws Exception{
		throw new Exception("Not available!");
	}
	
	@Override
	public void close() throws Exception {
		// Do Nothing
	}
	
	
	public String callHttpPost(String url, String req) throws Exception {
		URL postUrl = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
		return serviceIt(req, connection);
	}
	
	private String serviceIt(String req, HttpURLConnection connection) throws IOException {
		String rsp = null;
	    connection.setUseCaches(false);
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");

		connection.setInstanceFollowRedirects(true);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Content-Encoding", "utf-8");
		

		connection.setConnectTimeout(this.httpTimeout);
		connection.setReadTimeout(this.httpTimeout);

		connection.connect();
		DataOutputStream out = new DataOutputStream(connection.getOutputStream());

		String content = req;
		out.writeBytes(content);
		out.flush();
		out.close();
		

		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
		String line = null;
		StringBuffer sb = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}

		rsp = sb.toString();
		return rsp;
	}

}
