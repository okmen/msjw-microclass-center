/*package cn.microclass.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.microclass.bean.DeviceBean;
import cn.microclass.bean.Token;
import cn.microclass.bean.UserOpenidBean;
import cn.microclass.bean.UserRegInfo;
import cn.microclass.bean.WechatUserInfoBean;
import cn.microclass.bean.studyclassroom.Study;
import cn.microclass.cached.impl.IAccountCachedImpl;
import cn.microclass.config.IConfig;
import cn.microclass.dao.IAccountDao;
import cn.microclass.orm.DeviceORM;
import cn.microclass.orm.UsernameORM;
import cn.microclass.service.IMicroclassServer;
import cn.microclass.utils.RandomUtils;
import cn.microclass.utils.TokenGenerater;
import cn.sdk.util.StringUtil;

import com.alibaba.dubbo.common.utils.StringUtils;*/

//@Service("microclassService")
/*public class IAccountServiceImpl implements IMicroclassServer {
    private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private IAccountDao microclassDao;

	@Autowired
	private IAccountCachedImpl microclassCache;
	
	
	@Override
	public int insertWechatUserInfo(WechatUserInfoBean wechatUserInfo) {
		int result = 0;
		
		try {
			result = microclassDao.insertWechatUserInfo(wechatUserInfo);
		} catch (Exception e) {
			logger.error("插入wechatUserInfo失败，错误 ＝ ", e);
			throw e;
		}
		
		return result;
	}


	@Override
	public WechatUserInfoBean getWechatUserInfoById(int id) {
		WechatUserInfoBean result = null;
		
		try {
			logger.debug("debug");
			logger.info("info");
			logger.error("error");
			result = microclassDao.getWechatUserInfoById(id);
		} catch(Exception e) {
			logger.error("获取wechatUserInfoById失败， 错误 ＝ ", e);
			throw e;
		}
		return result;
	}
	
	@Override
	public List<WechatUserInfoBean> getAllWechatUserInfoBeanList(){
		List<WechatUserInfoBean> list = null;
		logger.debug("debug");
		logger.info("info");
		logger.error("error");
        try {
        	list = microclassDao.getAllWechatUserInfoBeanList();
        } catch (Exception e) {
        	logger.error("获取列表失败, 错误  = ", e);
            throw e;
        }
        return list;
	}



	@Override
	public List<Study> findUser(Study study) {
		// TODO Auto-generated method stub
		return null;
	};*/

	
//	@Override
//	public UserRegInfo addNewUser(UserRegInfo userRegInfo) {
//		long addSuccess = 0;
//		try {
//		    UsernameORM usernameORM = microclassDao.createUsername();
//		    String username = usernameORM.getId() + "";
//			userRegInfo.setNickname(this.creatNickName());
//			userRegInfo.setUsername(username);
//			addSuccess = microclassDao.addNewUser(userRegInfo);
//		} catch (Exception e) {
//			logger.error("添加注册新用户失败，用户 手机号码 = " + userRegInfo.getMobilePhone(), e);
//			throw e;
//		}
//		if (addSuccess > 0) {
//			return userRegInfo;
//		} else {
//			return null;
//		}	
//	}
//
//	public String creatNickName() {
//		String headStr = IConfig.Nickname_Head_List[(int) (Math.random() * IConfig.Nickname_Head_List.length)];
//		String endStr = IConfig.Nickname_End_List[(int) (Math.random() * IConfig.Nickname_End_List.length)];
//		return headStr + endStr;
//	}
//	
//	public String getAccessTokenByUserId(long userId) {
//        Token token = microclassCache.getToken(userId + "");
//        String accessToken = token.getAccessToken();
//        if (StringUtils.isBlank(accessToken)) {
//            logger.error("getAccessTokenByUserId,userId=" + userId + ",accessToken is null");
//        }
//        return accessToken;
//    }
//
//    public String getAccessTokenFromEncypt(String encyptAccessToken) {
//        return microclassCache.getAccessTokenFromEncypt(encyptAccessToken);
//    }
//
//    public void insertEncyptAccessToken(String encyptAccessToken, String accessToken) {
//        microclassCache.insertEncyptAccessToken(encyptAccessToken, accessToken);
//    }
//    
//    public Token getAccessToken(long userId) {
//        String userIdStr = userId + "";
//        Token token = new Token();
//        String accessToken = TokenGenerater.generateAccessToken(userIdStr);
//        token.setAccessToken(accessToken);
//        String refreshToken = TokenGenerater.generateRefreshToken(userIdStr);
//        token.setRefreshToken(refreshToken);
//        token.setUserId(userIdStr);
//        if (isUserHasLogin(userId)) {
//            token.setIsLogin("true");
//            logger.warn(userId + "二次登陆" + "accessToken" + "-" + accessToken);
//        } else {
//            token.setIsLogin("false");
//        }
//        microclassCache.insertToken(token);
//        return token;
//    }
//    
//    private boolean isUserHasLogin(long userId) {
//        boolean result = false;
//        Token token = microclassCache.getToken(Long.toString(userId));
//        if (token.getAccessToken() != null) {
//            result = true;
//        }
//        return result;
//    }
//    
//    /**
//     * 检查accessToken是否过期
//     */
//    public boolean isAccessTokenValidate(String accessToken, String userId) {
//        if (StringUtils.isBlank(accessToken) || StringUtils.isBlank(userId)) {
//            return false;
//        }
//        boolean result = true;
//        Token token = microclassCache.getToken(userId);
//        if (!accessToken.equals(token.getAccessToken())) {
//            logger.error("accessToken验证失败----isAccessTokenValidate----" + "accessToken:" + accessToken + "---" + "userId:" + userId);
//            result = false;
//        } else {
//            if(RandomUtils.isNeedExpire()){
//                microclassCache.updateAllToken(userId);
//            }
//        }
//        return result;
//    }
//    
//    /**
//     * 通过refreshToken获取accessToken
//     */
//    public Map<String, String> getAccessTokenByRefreshToken(String userId, String refreshToken) {
//        Map<String, String> resultMap = new HashMap<String, String>();
//        Token token = microclassCache.getToken(userId);
//        if (token.getRefreshToken() == null || !token.getRefreshToken().equals(refreshToken)) {
//            logger.error("refreshToken失效----getAccessTokenByRefreshToken----" + "refreshToken:" + refreshToken + "userId:" + userId);
//            resultMap.put("ERR", IConfig.ERR_REFRESH_TOKEN_INVALIDATE);
//        } else {
//            String accessToken = TokenGenerater.generateAccessToken(userId);
//            microclassCache.updateAccessToken(userId, accessToken);
//            microclassCache.updateRefreshToken(userId, refreshToken);
//            resultMap.put("accessToken", accessToken);
//        }
//        return resultMap;
//    }
//
//    /**
//     * 绑定微信
//     * @param userOpenidBean
//     * @author shengfenglai
//     * @return long
//     */
//    @Override
//    public long addBindOpenid(UserOpenidBean userOpenidBean) {
//        long addSuccess = 0;
//        if(userOpenidBean == null) {
//            logger.error("addBindOpenid接收参数有误");
//            return addSuccess;
//        }
//        try {
//            userOpenidBean.setStatus(1);
//            addSuccess = microclassDao.addBindOpenid(userOpenidBean);
//        } catch (Exception e) {
//            logger.error("addBindOpenid插入数据失败，addSuccess=" + addSuccess);
//            throw e;
//        }
//        return addSuccess;
//    }
//
//    /**
//     * 取消绑定微信
//     * @param userOpenidBean
//     * @author shengfenglai
//     * @return long 
//     */
//    @Override
//    public long cancelBindOpenid(UserOpenidBean userOpenidBean) {
//        long cancelSuccess = 0;
//        if(userOpenidBean == null) {
//            logger.error("cancelBindOpenid接收参数有误");
//            return cancelSuccess;
//        }
//        try {
//            userOpenidBean.setStatus(2);
//            cancelSuccess = microclassDao.updateBindOpenidStatus(userOpenidBean);
//        } catch (Exception e) {
//            logger.error("更新绑定状态失败，cancelSuccess = " + cancelSuccess);
//            throw e;
//        }
//        return cancelSuccess;
//    }
//
//    /**
//     * 通过openid拿到userId
//     * @param openid
//     * @return userId
//     * @author shengfenglai
//     */
//    @Override
//    public long getUserIdByOpenid(String openid) {
//        Long userId = 0L;
//        if(StringUtil.isBlank(openid)) {
//            logger.error("getUserIdByOpenid接收参数为空 ，openid=" + openid);
//            return userId.longValue();
//        } 
//        try {
//            userId = microclassDao.getUserIdByOpenid(openid);
//        } catch (Exception e) {
//            logger.error("通过openid获取userId失败",e);
//            throw e;
//        }
//        return userId == null ? 0 : userId.longValue();
//    }
//
//    @Override
//    public String getOpenidByUserId(long userId) {
//        
//        String openid = null;
//        if(userId < 0) {
//            logger.error("getOpenidByUserId接收到的userId不对,userId=" + userId);
//            return "";
//        }
//        
//        try {
//            openid = microclassDao.getOpenidByUserId(userId);
//        } catch (Exception e) {
//            logger.error("通过userId获取openid失败",e);
//            throw e;
//        }
//        
//        return openid == null ? "" : openid;
//    }
//
//    @Override
//    public DeviceBean getDevice(String deviceUuid, int osType) {
//        
//        if(StringUtils.isBlank(deviceUuid) || osType < 0) {
//            logger.error("getDevice：获取的参数不正确 ,deviceUuid:{},osType:{}",deviceUuid,osType);
//            return null;
//        }
//        
//        DeviceBean deviceBean = new DeviceBean();
//        try {
//            DeviceORM deviceORM = microclassDao.getDevice(deviceUuid, osType);
//            if(deviceORM == null) {
//                deviceBean = null;
//            } else {
//                BeanUtils.copyProperties(deviceORM, deviceBean);
//            }
//        } catch (Exception e) {
//            logger.error("获取device信息失败",e);
//            throw e;
//        }
//        
//        return deviceBean;
//    }
//
//    @Override
//    public void addDevice(String deviceUuid, int osType, long userId) {
//        
//        if(StringUtils.isBlank(deviceUuid) || osType < 0 || userId < 0) {
//            logger.error("addDevice：获取的参数不正确 ,deviceUuid:{},osType:{}",deviceUuid,osType);
//            return ;
//        }
//        
//        try {
//            long addTime = System.currentTimeMillis() / 1000;//系统当前时间，单位:秒
//            microclassDao.addDevice(deviceUuid, osType, userId, addTime);
//        } catch (Exception e) {
//            logger.error("添加设备号失败",e);
//            throw e;
//        }
//    }
//
//    
//    @Override
//    public boolean updateDevice(String deviceUuid,int osType,long userId) {
//        
//        if(StringUtils.isBlank(deviceUuid) || osType < 0 || userId < 0) {
//            logger.error("addDevice：获取的参数不正确 ,deviceUuid:{},osType:{}",deviceUuid,osType);
//            return false;
//        }
//        
//        boolean updateSuccess = false;
//        try {
//            updateSuccess = microclassDao.updateDevice(deviceUuid, osType, userId);
//        } catch (Exception e) {
//            logger.error("更新device失败",e);
//            throw e;
//        }
//        return updateSuccess;
//    }

//}
