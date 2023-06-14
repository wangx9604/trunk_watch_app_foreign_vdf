package dx.client.impl.ssl;

import com.xiaoxun.xun.utils.StrUtil;

/**
 * Created by huangyouyang on 2016/8/17.
 */
public class SslClientUtils {

    public static char[] getClientTrustPwd(){

        return getClientTrustPwdStr().toCharArray();
    }

    public static String getClientTrustPwdStr(){

        String clientTrustPwd= StrUtil.getLeftPwd().trim()+StrUtil.getRightPwd().trim();

        return clientTrustPwd;
    }

}
