package dx.client.impl.ssl;

import javax.net.ssl.SSLContext;

public final class EasySslClientContextFactory {

    
    private final SSLContext _secureContext;

    public static EasySslClientContextFactory getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private interface SingletonHolder {
        EasySslClientContextFactory INSTANCE = new EasySslClientContextFactory();
    }

    private EasySslClientContextFactory() {
        SSLContext secureContext = null;
        try {
            try {
                javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
                javax.net.ssl.TrustManager tm = new EasyTrustManager();
                trustAllCerts[0] = tm;
                secureContext = javax.net.ssl.SSLContext.getInstance("SSL");
                secureContext.init(null, trustAllCerts, null);
            } catch (Exception e) {
                throw new Error("Failed to initialize the SSLContext", e);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        } finally {
            _secureContext = secureContext;
        }
    }

    public SSLContext sslContext() {
        return _secureContext;
    }

}

class EasyTrustManager implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return null;
    }


    public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
        return true;
    }


    public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
        return true;
    }

    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
            throws java.security.cert.CertificateException {
        return;
    }

    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
            throws java.security.cert.CertificateException {
        return;
    }
}