package cn.microclass.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.microclass.bean.DeviceBean;
import cn.microclass.bean.Token;
import cn.microclass.bean.UserOpenidBean;
import cn.microclass.bean.UserRegInfo;
import cn.microclass.bean.WechatUserInfoBean;
import cn.microclass.bean.studyclassroom.Study;
import cn.microclass.service.IMicroclassServer;
import cn.sdk.bean.BaseBean;
import cn.sdk.util.AESNewUtils;
import cn.sdk.util.DESUtils;
import cn.sdk.util.HttpClientUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:junit-test.xml" })
public class TestAccountService {

    @Autowired
    @Qualifier("microclassService")
    private IMicroclassServer microclassService;
    
	
	@Test
	public void testXfStudyAnswer() {
		Study  s=new Study();
		s.setInterfaceId("exam001");
		s.setIdentityCard("622822198502074110");
		s.setMobilephone("15920071829");
		s.setIpAddress("123.56.180.216");
		s.setUserSource("C");
		 microclassService.xfStudyAnswer(s);
	}
	/**
	 * 消分业务相关查询
	 */
	@Test
	public void xfStudyQuery(){
		Study  s=new Study();
		s.setInterfaceId("exam003");
		s.setIdentityCard("431022199612250036");
		s.setMobilephone("17708404197");
		s.setIpAddress("123.56.180.216");
		s.setUserSource("C");
		List<BaseBean>list= microclassService.xfStudyQuery(s);
		for(BaseBean b:list){
			System.out.println(b.getCode());
			System.out.println(b.getMsg());
			System.out.println(b.getData());
		}
	}
	/**
	 * 6.34.3	行人、非机动车驾驶人道路交通安全学习查询接口 
	 */
	@Test
	public void  xrStudyQuery(){
		Study  s=new Study();
		s.setInterfaceId("DDC3003");
		s.setIdentityCard("431022199612250036");
		s.setMobilephone("17708404197");
		s.setServiceType("AQ");
		s.setIpAddress("123.56.180.216");
		s.setUserSource("C");
	    microclassService.xrStudyQuery(s);
	}
	



    
}
