/*package cn.microclass.cached.impl;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.microclass.bean.Token;
import cn.microclass.bean.UserRegInfo;
import cn.microclass.bean.WechatUserInfoBean;
import cn.microclass.cached.IAccountCached;
import cn.microclass.config.IConfig;
import cn.sdk.cache.ICacheManger;
import cn.sdk.serialization.ISerializeManager;



@Service
public class IAccountCachedImpl implements IAccountCached{
	protected Logger log = Logger.getLogger(this.getClass());
	
	
	
    
    @Value("${refreshTokenTime}")
    private int refreshTokenTime;
    
    @Value("${encyptAccessTokenTime}")
    private int encyptAccessTokenTime;
    
    @Value("${accessTokentime}")
    private int accessTokenTime;
    
	@Autowired
	@Qualifier("jedisCacheManagerImpl")
	private ICacheManger<String> cacheManger;
	
	@Autowired
	@Qualifier("jedisCacheManagerImpl")
	private ICacheManger<Object> objectcacheManger;
	
	@Autowired
	private ISerializeManager< Map<String, String> > serializeManager;
	
    public static final String arrayToString(byte[] bytes)
    {
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < bytes.length; i++)
        {
            buff.append(bytes[i] + " ");
        }
        return buff.toString();
    }
    
    
    @Override
    public boolean setWechatUserInfoBean(long id, WechatUserInfoBean wechatUserInfoBean){
    	String userRedisKey = USER_WECHAT_INFO_REDIS_KEY + id;
		return objectcacheManger.setByKryo(userRedisKey, wechatUserInfoBean, exprieTime);
    }
	
    @Override
	public WechatUserInfoBean getWechatUserInfoBean(long id){
    	String userRedisKey = USER_WECHAT_INFO_REDIS_KEY + id;
    	return (WechatUserInfoBean) objectcacheManger.getByKryo(userRedisKey, WechatUserInfoBean.class);
	}
	
	

	@Override
	public boolean setUser(long userId, UserRegInfo user) {
		String userRedisKey = USER_REDIS_KEY + userId;
		return objectcacheManger.setByKryo(userRedisKey, user, exprieTime);
	}

	@Override
	public UserRegInfo getUser(long userId) {
		String userRedisKey = USER_REDIS_KEY + userId;
		return (UserRegInfo) objectcacheManger.getByKryo(userRedisKey, UserRegInfo.class);
	}
	
	public Token insertToken(Token token) {
	    cacheManger.set(IConfig.USER_ACCOUNT_ACCESS_TOKEN_REDIS + token.getUserId(), token.getAccessToken(), accessTokenTime);
        cacheManger.set(IConfig.USER_ACCOUNT_REFRESH_TOKEN_REDIS + token.getUserId(), token.getRefreshToken(), refreshTokenTime);
        return token;
    }
    
    public String deleteToken(String userId) {
        cacheManger.del(IConfig.USER_ACCOUNT_ACCESS_TOKEN_REDIS + userId);
        cacheManger.del(IConfig.USER_ACCOUNT_REFRESH_TOKEN_REDIS + userId);
        return "success";
    }   
    
    public Token getToken(String userId)
    {   
        Token token = new Token();
        String accessToken = cacheManger.get(IConfig.USER_ACCOUNT_ACCESS_TOKEN_REDIS + userId);
        String refreshToken = cacheManger.get(IConfig.USER_ACCOUNT_REFRESH_TOKEN_REDIS + userId);
        token.setUserId(userId);
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        return token;
    }
    
    public Token updateAllToken(String userId)
    {
        Token token = getToken(userId);
        cacheManger.set(IConfig.USER_ACCOUNT_ACCESS_TOKEN_REDIS + token.getUserId(), token.getAccessToken(), accessTokenTime);
        cacheManger.set(IConfig.USER_ACCOUNT_REFRESH_TOKEN_REDIS + token.getUserId(), token.getRefreshToken(), refreshTokenTime);      
        return token;
    }
    
    public void updateAccessToken(String userId, String accessToken)
    {
        cacheManger.set(IConfig.USER_ACCOUNT_ACCESS_TOKEN_REDIS + userId, accessToken, accessTokenTime);
    }
    public void updateRefreshToken(String userId, String refreshToken)
    {
        cacheManger.set(IConfig.USER_ACCOUNT_REFRESH_TOKEN_REDIS + userId, refreshToken, refreshTokenTime);        
    }
    
    @Override
    public void insertEncyptAccessToken(String encyptAccessToken, String AccessToken) {
        if(StringUtils.isNotBlank(encyptAccessToken) && StringUtils.isNotBlank(AccessToken)){
            cacheManger.set(String.format(IConfig.ENCYPT_ACCESS_TOKEN_REDIS_KEY, encyptAccessToken), AccessToken, encyptAccessTokenTime);          
        }
    }

    @Override
    public String getAccessTokenFromEncypt(String encyptAccessToken) {
        if(StringUtils.isNotBlank(encyptAccessToken)){
            return cacheManger.get(String.format(IConfig.ENCYPT_ACCESS_TOKEN_REDIS_KEY, encyptAccessToken), encyptAccessTokenTime);            
        }else{
            return null;
        }
    }


	public Logger getLog() {
		return log;
	}


	public void setLog(Logger log) {
		this.log = log;
	}


	


	public int getRefreshTokenTime() {
		return refreshTokenTime;
	}


	public void setRefreshTokenTime(int refreshTokenTime) {
		this.refreshTokenTime = refreshTokenTime;
	}


	public int getEncyptAccessTokenTime() {
		return encyptAccessTokenTime;
	}


	public void setEncyptAccessTokenTime(int encyptAccessTokenTime) {
		this.encyptAccessTokenTime = encyptAccessTokenTime;
	}


	public int getAccessTokenTime() {
		return accessTokenTime;
	}


	public void setAccessTokenTime(int accessTokenTime) {
		this.accessTokenTime = accessTokenTime;
	}


	public ICacheManger<String> getCacheManger() {
		return cacheManger;
	}


	public void setCacheManger(ICacheManger<String> cacheManger) {
		this.cacheManger = cacheManger;
	}


	public ICacheManger<Object> getObjectcacheManger() {
		return objectcacheManger;
	}


	public void setObjectcacheManger(ICacheManger<Object> objectcacheManger) {
		this.objectcacheManger = objectcacheManger;
	}


	public ISerializeManager<Map<String, String>> getSerializeManager() {
		return serializeManager;
	}


	public void setSerializeManager(ISerializeManager<Map<String, String>> serializeManager) {
		this.serializeManager = serializeManager;
	}

}
*/