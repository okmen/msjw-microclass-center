package cn.microclass.dao.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.microclass.bean.UserOpenidBean;
import cn.microclass.bean.UserRegInfo;
import cn.microclass.bean.WechatUserInfoBean;
import cn.microclass.orm.DeviceORM;
import cn.microclass.orm.UsernameORM;
import cn.microclass.orm.WechatUserInfoORM;

@Repository
public interface AccountMapper {
	
	/**
	 * 插入微信用户信息
	 * @param wechatUserInfo
	 * @return 成功则返回纪录id，失败返回0
	 */
	int insertWechatUserInfo(WechatUserInfoBean wechatUserInfo);
	
    WechatUserInfoORM getWechatUserInfoById(@Param("id") int id);
	
	List<WechatUserInfoBean> getAllWechatUserInfoBeanList();
	

	public long addNewUser(UserRegInfo userRegInfo);

	public String getMaxUsername();

	/**
	 * 注册用户获取用户名称username
	 * @author nishixiang
	 * @param usernameORM
	 * @return
	 */
    public long createUsername(UsernameORM usernameORM);
    
    
    public long addBindOpenid(UserOpenidBean userOpenidBean);
    
    public long updateBindOpenidStatus(UserOpenidBean userOpenidBean);
    
    public Long getUserIdByOpenid(String openid);
    
    public String getOpenidByUserId(long userId);
    
    public DeviceORM getDevice(Map<String,Object> map);
    
    public void addDevice(DeviceORM deviceORM);
    
    public boolean updateDevice(DeviceORM deviceORM);
    
}
