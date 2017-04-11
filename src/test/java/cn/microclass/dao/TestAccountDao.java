package cn.microclass.dao;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.microclass.bean.UserOpenidBean;
import cn.microclass.dao.IAccountDao;
import cn.microclass.dao.mapper.AccountMapper;
import cn.microclass.orm.DeviceORM;
import cn.microclass.orm.UsernameORM;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:junit-test.xml" })
public class TestAccountDao {

	@Autowired
	private IAccountDao microclassDao;

	@Autowired
	private AccountMapper userMapper;

	// 测试新增用户
	@Test
	public void testAddUser() {

//		UserRegInfo ss2 = userDao.getUserByUserId(2);
//		Date date = new Date();
//		String username = "jerrycai " + date.getTime();
//		long result = userDao.addUser(date.getTime(), username, 1, 2);
//		assertEquals(1, result);
//		Map<String, Object> map = new HashMap<>();
//		map.put("username", username);
//		Map<String, String> dbuserinfo = userMapper.getUserByUsername(map);
//		assertEquals(dbuserinfo.get("mobilephone"), date.getTime());
	}
	
	@Test
	public void testCreateUsername(){
 	    UsernameORM orm = microclassDao.createUsername();
	    System.out.println(orm.getId());
	};
	
	@Test 
	public void testAddBindOpenid() {
	    UserOpenidBean userOpenidBean = new UserOpenidBean();
	    userOpenidBean.setOpenid("wwww");
	    userOpenidBean.setUserId(111111);
	    userOpenidBean.setBindTime(new Date().getTime()/1000);
	    userOpenidBean.setStatus(1);
	    long flag = microclassDao.addBindOpenid(userOpenidBean);
	    Assert.assertEquals(flag, 1);
	}
	
	@Test
	public void testUpdateBindOpenidStatus() {
	    UserOpenidBean userOpenidBean = new UserOpenidBean();
        userOpenidBean.setUserId(111111);
        userOpenidBean.setUnBindTime(new Date().getTime()/1000);
        userOpenidBean.setStatus(2);
        long falg = microclassDao.updateBindOpenidStatus(userOpenidBean);
        Assert.assertEquals(falg, 1);
	}
	
	@Test
	public void testGetUserIdByOpenid() {
	    long userId = microclassDao.getUserIdByOpenid("dddd");
	    System.out.println(userId);
	    Assert.assertNotEquals(userId, 0);
	}
	
	@Test
	public void testGetOpenidByUserId() {
	    String openid  = microclassDao.getOpenidByUserId(878669);
	    System.out.println(openid);
	    Assert.assertNotNull(openid);
	}

	
	@Test
	public void testGetDevice() {
	   DeviceORM deviceORM = microclassDao.getDevice("w345wrgqery42562346arqgf", 1);
	   System.out.println(deviceORM.getUserId());
	   System.out.println(deviceORM.getAddTime());
	   Assert.assertNotNull(deviceORM);
	}
	
	
	@Test 
	public void testAddDevice() {
	    microclassDao.addDevice("w345wrgqery42562346arqgf", 1, 738789,System.currentTimeMillis()/1000);
	}
	
	@Test 
	public void testUpdateDevice() {
	    boolean flag  = microclassDao.updateDevice("w345wrgqery42562346arqgf", 1, 888888);
	    System.out.println(flag);
	    Assert.assertTrue(flag);
	}
}
