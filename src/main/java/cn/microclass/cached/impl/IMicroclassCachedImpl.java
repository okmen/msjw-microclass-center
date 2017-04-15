package cn.microclass.cached.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.microclass.bean.studyclassroom.Study;
import cn.microclass.cached.IMicroclassCached;


@Service
public class IMicroclassCachedImpl implements IMicroclassCached {

	
	/**
	 * 用户id
	 */
	@Value("${userid}")
    private String userid;
	/**
	 * 用户密码
	 */
    @Value("${userpwd}")
    private String userpwd;
    /**
     * 请求地址
     */
    @Value("${url}")
    private String url;
    /**
     * 方法
     */
    @Value("${method}")
    private String method;
    /**
     * 秘钥
     */
    @Value("${key}")
    private String key;
	
	@Override
	public List<Study> findUser(Study study) {
		// TODO Auto-generated method stub
		return null;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getUserpwd() {
		return userpwd;
	}
	public void setUserpwd(String userpwd) {
		this.userpwd = userpwd;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
}
