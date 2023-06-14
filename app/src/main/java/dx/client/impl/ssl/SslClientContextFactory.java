package dx.client.impl.ssl;

import android.content.res.Resources;

import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;


public final class SslClientContextFactory {

    public static String CLIENT_KEY_PASSWORD = null;//
    public static String CLIENT_TRUST_PASSWORD = null;//
    private static final String CLIENT_AGREEMENT = "TLS";//
    private static final String CLIENT_KEY_MANAGER = "X509";//
    private static final String CLIENT_TRUST_MANAGER = "X509";//
    private static final String CLIENT_KEY_KEYSTORE = "BKS";//
    private static final String CLIENT_TRUST_KEYSTORE = "BKS";//

    public static volatile String key_store_file_path="dxclient_k.bks";
    public static volatile String trust_store_file_path="dxclient_t.bks";

    private final SSLContext _secureContext;

    private static SslClientContextFactory instance = null;
    public static void initInstance(Resources res) {
        if (instance==null) {
            instance = new SslClientContextFactory(res);
        }
    }

    public static SslClientContextFactory getInstance() {
        return instance;
    }

    private SslClientContextFactory(Resources res) {
        SSLContext secureContext = null;
        try {
            try {
                //ȡ��SSL��SSLContextʵ��
                secureContext = SSLContext.getInstance(CLIENT_AGREEMENT);
//                //ȡ��BKS�ܿ�ʵ��
//                KeyStore kks= KeyStore.getInstance(CLIENT_KEY_KEYSTORE);
//                //�ӿͻ�����֤���˽Կ,ͨ���ȡ��Դ�ļ��ķ�ʽ��ȡ��Կ������֤��
//                kks.load(res.getAssets().open(key_store_file_path), CLIENT_KEY_PASSWORD.toCharArray());
//                //ȡ��KeyManagerFactory��X509��Կ������ʵ��
//                KeyManagerFactory kmf = KeyManagerFactory.getInstance(CLIENT_KEY_MANAGER);
//                //��ʼ����Կ������
//                kmf.init(kks, CLIENT_KEY_PASSWORD.toCharArray());

                KeyStore tks = KeyStore.getInstance(CLIENT_TRUST_KEYSTORE);
                tks.load(res.getAssets().open(trust_store_file_path),SslClientUtils.getClientTrustPwd());
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(CLIENT_TRUST_MANAGER);
                tmf.init(tks);

                //��ʼ��SSLContext
                secureContext.init(null, tmf.getTrustManagers(), null);//kmf.getKeyManagers()
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
