package cn.microclass.cached.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.microclass.bean.AppVersion;
import cn.microclass.bean.studyclassroom.Study;
import cn.microclass.cached.ICacheKey;
import cn.microclass.cached.IMicroclassCached;
import cn.sdk.cache.ICacheManger;


@Service
public class IMicroclassCachedImpl implements IMicroclassCached {

	/**
	 * 用户id
	 */
	@Value("${useridAlipay}")
    private String useridAlipay;
	/**
	 * 用户密码
	 */
    @Value("${userpwdAlipay}")
    private String userpwdAlipay;
    /**
     * 请求地址
     */
    @Value("${urlAlipay}")
    private String urlAlipay;
    /**
     * 方法
     */
    @Value("${methodAlipay}")
    private String methodAlipay;
    /**
     * 秘钥
     */
    @Value("${keyAlipay}")
    private String keyAlipay;
    /**
	 * 用户id
	 */
	@Value("${useridApp}")
    private String useridApp;
	/**
	 * 用户密码
	 */
    @Value("${userpwdApp}")
    private String userpwdApp;
    /**
     * 请求地址
     */
    @Value("${urlApp}")
    private String urlApp;
    /**
     * 方法
     */
    @Value("${methodApp}")
    private String methodApp;
    /**
     * 秘钥
     */
    @Value("${keyApp}")
    private String keyApp;
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
    
    @Autowired
	@Qualifier("jedisCacheManagerImpl")
	private ICacheManger<Object> objectcacheManger;
	
	@Override
	public List<Study> findUser(Study study) {
		return null;
	}
	public String getUserid(String sourceOfCertification) {
		String string = "";
		if("C".equals(sourceOfCertification)){
			string = userid;
		}else if("Z".equals(sourceOfCertification)){
			string = useridAlipay;
		}else if("A".equals(sourceOfCertification)){
			string = useridApp;
		}else {
			string = userid;
		}
		return string;
	}


	public String getUserpwd(String sourceOfCertification) {
		String string = "";
		if("C".equals(sourceOfCertification)){
			string = userpwd;
		}else if("Z".equals(sourceOfCertification)){
			string = userpwdAlipay;
		}else if("A".equals(sourceOfCertification)){
			string = userpwdApp;
		}else {
			string = userpwd;
		}
		return string;
	}


	public String getUrl(String sourceOfCertification) {
		String string = "";
		if("C".equals(sourceOfCertification)){
			string = url;
		}else if("Z".equals(sourceOfCertification)){
			string = urlAlipay;
		}else if("A".equals(sourceOfCertification)){
			string = urlApp;
		}else {
			string = url;
		}
		return string;
	}

	public String getMethod(String sourceOfCertification) {
		String string = "";
		if("C".equals(sourceOfCertification)){
			string = method;
		}else  if("Z".equals(sourceOfCertification)){
			string = methodAlipay;
		}else  if("A".equals(sourceOfCertification)){
			string = methodApp;
		}else {
			string = method;
		}
		return string;
	}


	public String getKey(String sourceOfCertification) {
		String string = "";
		if("C".equals(sourceOfCertification)){
			string = key;
		}else if("Z".equals(sourceOfCertification)){
			string = keyAlipay;
		}else if("A".equals(sourceOfCertification)){
			string = keyApp;
		}else {
			string = key;
		}
		return string;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	public void setUserpwd(String userpwd) {
		this.userpwd = userpwd;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public void setKey(String key) {
		this.key = key;
	}
	@Override
	public void setAppVersion(String system, AppVersion appVersion) {
		objectcacheManger.set(ICacheKey.APP_NEWEST_VERSION + system, appVersion, ICacheKey.exprieTime);
	}
	@Override
	public AppVersion getAppVersion(String system) {
		return (AppVersion) objectcacheManger.get(ICacheKey.APP_NEWEST_VERSION + system);
	}
	
}
