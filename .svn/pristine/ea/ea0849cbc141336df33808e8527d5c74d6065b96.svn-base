package org.java_websocket.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.charset.StandardCharsets;

import org.java_websocket.AbstractWrappedByteChannel;

public abstract class AbstractClientProxyChannel extends AbstractWrappedByteChannel {
	protected final ByteBuffer proxyHandshake;


	/**
	 * @param towrap
	 *            The channel to the proxy server
	 **/
	public AbstractClientProxyChannel( ByteChannel towrap ) {
		super( towrap );
        proxyHandshake = ByteBuffer.wrap( buildHandShake().getBytes(StandardCharsets.US_ASCII) );
    }

	@Override
	public int write( ByteBuffer src ) throws IOException {
		if( !proxyHandshake.hasRemaining() ) {
			return super.write( src );
		} else {
			return super.write( proxyHandshake );
		}
	}

	public abstract String buildHandShake();

}
