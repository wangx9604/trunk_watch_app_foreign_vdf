package com.xiaoxun.xun.utils.alipayLoginUtil;

/**
 * @author cuiyufeng
 * @Description: AlipayInfoBean
 * @date 2018/11/26 17:58
 */
public class AlipayInfoBean {
    private String sign;
    private AlipayUserInfo alipay_user_info_share_response;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public AlipayUserInfo getAlipay_user_info_share_response() {
        return alipay_user_info_share_response;
    }

    public void setAlipay_user_info_share_response(AlipayUserInfo alipay_user_info_share_response) {
        this.alipay_user_info_share_response = alipay_user_info_share_response;
    }

    public class AlipayUserInfo{
        private String code;
        private String msg;
        private String city;
        private String gender;
        private String is_certified;
        private String is_student_certified;
        private String nick_name;
        private String province;
        private String user_id;
        private String user_status;
        private String user_type;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getIs_certified() {
            return is_certified;
        }

        public void setIs_certified(String is_certified) {
            this.is_certified = is_certified;
        }

        public String getIs_student_certified() {
            return is_student_certified;
        }

        public void setIs_student_certified(String is_student_certified) {
            this.is_student_certified = is_student_certified;
        }

        public String getNick_name() {
            return nick_name;
        }

        public void setNick_name(String nick_name) {
            this.nick_name = nick_name;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getUser_status() {
            return user_status;
        }

        public void setUser_status(String user_status) {
            this.user_status = user_status;
        }

        public String getUser_type() {
            return user_type;
        }

        public void setUser_type(String user_type) {
            this.user_type = user_type;
        }
    }
}
