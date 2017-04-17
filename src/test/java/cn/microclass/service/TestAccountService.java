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
		 * 消分业务随机取题
		 */
		@Test
		public void testXfStudyAnswer() {
			Study  s=new Study();
			s.setInterfaceId("exam001");
			s.setIdentityCard("431022199612250036");
			s.setMobilephone("17708404197");
			s.setIpAddress("123.56.180.216");
			s.setUserSource("C");
			 microclassService.xfStudyAnswer(s);
		}
		/**
		 * 消分业务答题
		 */
		@Test
		public void xfAnswerQuey(){
			Study  s=new Study();
			s.setInterfaceId("exam002");
			s.setSubjectId("3861909");  //取题ID
			s.setUserName("曾令成");		
			s.setIdentityCard("431022199612250036"); //身份证号码
			s.setMobilephone("17708404197"); //手机号码
			s.setIpAddress("123.56.180.216"); //答题IP地址
			s.setSubjectAnswer("A"); //答题答案 
			s.setAnswerDateTime("2017-04-17 10:59:49");
			s.setScoreStartDate("2016-07-09"); //取题时的计分周期始
			s.setScoreEndDate("2017-07-09"); //取题时的计分周期末
			s.setUserSource("C");
			 microclassService.xfAnswerQuey(s);
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
	
	/**
	 * 6.34.3	行人、非机动车驾驶人道路交通安全学习随机取题
	 */
	@Test
	public void xrStudyAnswer(){
		Study  s=new Study();
		s.setInterfaceId("DDC3001");
		s.setIdentityCard("431022199612250036");
		s.setMobilephone("17708404197");
		s.setServiceType("AQ");
		s.setIpAddress("123.56.180.216");
		s.setUserSource("C");
		microclassService.xrStudyAnswer(s);
	}
	/**
	 * 行人、非机动车驾驶人道路交通安全学习 答题接口
	 */
	@Test
	public void xrAnswerQuey(){
		Study  s=new Study();
		s.setInterfaceId("DDC3002");
		s.setSubjectId("6910099");
		s.setIdentityCard("431022199612250036");
		s.setMobilephone("17708404197");
		s.setServiceType("AQ");
		s.setIpAddress("123.56.180.216");
		s.setUserSource("C");
		s.setSubjectAnswer("A");
		microclassService.xrAnswerQuey(s);
	}
    
}
