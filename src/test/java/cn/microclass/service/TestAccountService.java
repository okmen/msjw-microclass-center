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

import cn.microclass.bean.AppVersion;
import cn.microclass.bean.DeviceBean;
import cn.microclass.bean.Token;
import cn.microclass.bean.UserOpenidBean;
import cn.microclass.bean.UserRegInfo;
import cn.microclass.bean.WechatUserInfoBean;
import cn.microclass.bean.studyclassroom.Study;
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
    private IMicroclassService microclassService;
    
    /**
     * 获取最新app版本
     * @throws Exception
     */
	@Test
	public void testqueryNewestAppVersion() throws Exception{
		AppVersion appVersion = microclassService.queryNewestAppVersion("Android");
		System.out.println(appVersion);
	}
 
	/**
	 * 消分业务相关查询
	 * @throws Exception 
	 */
	@Test
	public void xfStudyQuery() throws Exception{
		Study  s=new Study();
		s.setClassroomId("1");
		s.setInterfaceId("exam003");
		s.setIdentityCard("431022199612250036");
		s.setMobilephone("17708404197");
		s.setIpAddress("123.56.180.216");
		s.setUserSource("A");
		List<BaseBean>list= microclassService.xfStudyQuery(s);
		for(BaseBean b:list){
			System.out.println(b.getCode());
			System.out.println(b.getMsg());
			System.out.println(b.getData());
		}
	}
	    /**
		 * 消分业务随机取题
	     * @throws Exception 
		 */
		@Test
		public void testXfStudyAnswer() throws Exception {
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
		 * @throws Exception 
		 */
		@Test
		public void xfAnswerQuey() throws Exception{
			Study  s=new Study();
			s.setInterfaceId("exam002");
			s.setSubjectId("3900792");  //取题ID
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
	 * @throws Exception 
	 */
	@Test
	public void  xrStudyQuery() throws Exception{
		Study  s=new Study();
		s.setInterfaceId("DDC3003");
		/*s.setIdentityCard("440301199002101119");
		s.setMobilephone("18603017278");*/
		
	/*	s.setIdentityCard("420881198302280017");
		s.setMobilephone("18601174358");*/
		s.setIdentityCard("622822198502074110");
		s.setMobilephone("15920071829");
		
		s.setServiceType("AQ");
		s.setIpAddress("123.56.180.216");
		s.setUserSource("C");
	    microclassService.xrStudyQuery(s);
	}
	
	/**
	 * 6.34.3	行人、非机动车驾驶人道路交通安全学习随机取题
	 * @throws Exception 
	 */
	@Test
	public void xrStudyAnswer() throws Exception{
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
	 * @throws Exception 
	 */
	@Test		
	public void xrAnswerQuey() throws Exception{
		Study  s=new Study();
		s.setInterfaceId("DDC3002");
		s.setSubjectId("6967784");
		/*s.setIdentityCard("431022199612250036");
		s.setMobilephone("17708404197");*/	
		s.setIdentityCard("440301199002101119");
		s.setMobilephone("18603017278");
		s.setServiceType("AQ");
		s.setIpAddress("123.56.180.216");
		s.setUserSource("C");
		s.setSubjectAnswer("ABCD");
		microclassService.xrAnswerQuey(s);
	}
	
	/**
	 * 电动车违法查询
	 * @throws Exception 
	 */
	@Test
	public void  ddcStudyQuery() throws Exception{
		Study  s=new Study();
		s.setInterfaceId("DDC2003");
		s.setIdentityCard("431022199612250036");
		s.setMobilephone("17708404197");
		s.setClassroomId("4");
		s.setServiceType("BA");
		s.setDecisionId("12345");
		s.setIpAddress("123.56.180.216");
		s.setUserSource("C");
		microclassService.xrStudyQuery(s);
	}
	
	
	/**
	 * 电动车违法随机取题
	 * @throws Exception 
	 */
	@Test
	public void ddcStudyAnswer() throws Exception{
		Study  s=new Study();
		s.setInterfaceId("DDC2001");
		s.setSubjectId("6910089");
		s.setIdentityCard("431022199612250036");
		s.setMobilephone("17708404197");
		s.setServiceType("BA");
		s.setDecisionId("12345");
		s.setIpAddress("123.56.180.216");
		s.setUserSource("C");
		microclassService.ddcStudyAnswer(s);
	}
	
	/**
	 * 电动车违法答题
	 * @throws Exception 
	 */
	@Test
	public  void ddcAnswerQuey() throws Exception{
		Study  s=new Study();
		s.setInterfaceId("DDC2002");
		s.setSubjectId("6923386");
		s.setIdentityCard("431022199612250036");
		s.setMobilephone("17708404197");
		s.setServiceType("BA");
		s.setIpAddress("123.56.180.216");
		s.setUserSource("C");
		s.setSubjectAnswer("A");
		s.setDecisionId("12345");
		microclassService.ddcAnswerQuey(s);
		
	}
	
	
	
	
	/**
	 * 满分
	 * @throws Exception 
	 */
	@Test
	public void  mfStudyQuery() throws Exception{
		Study  s=new Study();
		s.setInterfaceId("mfyydtjgcx");
		/*s.setIdentityCard("431022199612250036");
		s.setMobilephone("17708404197");*/
		s.setClassroomId("2");
		s.setIdentityCard("440301199002101119");
		s.setMobilephone("18603017278");
		s.setIpAddress("123.56.180.216");
		s.setUserSource("C");
		//microclassService.mfStudyQuery(s);
		microclassService.xrStudyQuery(s);
	}
	//满分取题
	@Test
	public void mfqt() throws Exception{
		Study  s=new Study();
		s.setInterfaceId("mfyyqt");
		s.setClassroomId("2");
		s.setIdentityCard("440301199002101119");
		s.setMobilephone("18603017278");
		s.setIpAddress("123.56.180.216");
		s.setUserSource("C");
		microclassService.xfStudyAnswer(s);
	}
	
	//满分答题
	@Test
	public void mfdt() throws Exception{
		Study  s=new Study();
		s.setInterfaceId("mfyydt");
		s.setSubjectId("709672");  //取题ID
		s.setUserName("杨明畅");		
		s.setIdentityCard("440301199002101119"); //身份证号码
		s.setMobilephone("18603017278"); //手机号码
		s.setIpAddress("123.56.180.216"); //答题IP地址
		s.setSubjectAnswer("A"); //答题答案 
		s.setAnswerDateTime("2017-04-17 10:59:49");
		s.setScoreStartDate("2016-08-04"); //取题时的计分周期始
		s.setScoreEndDate("2017-08-04"); //取题时的计分周期末
		s.setUserSource("C");
		microclassService.xfAnswerQuey(s);
	}
	
	
	/**
	 * B类驾照查询
	 * @throws Exception 
	 */
	@Test
	public void  blStudyQuery() throws Exception{
		Study  s=new Study();
		s.setInterfaceId("blyydtjgcx");
		/*s.setIdentityCard("431022199612250036");
		s.setMobilephone("17708404197");*/
		s.setClassroomId("3");
		s.setIdentityCard("440301199002101119");
		s.setMobilephone("18603017278");
		s.setIpAddress("123.56.180.216");
		s.setUserSource("C");
		//microclassService.mfStudyQuery(s);
		microclassService.xrStudyQuery(s);
	}
	//B类取题
	@Test
	public void blqt() throws Exception{
		Study  s=new Study();
	    s.setInterfaceId("blyyqt");
	    s.setClassroomId("3");
		s.setIdentityCard("440301199002101119");
		s.setMobilephone("18603017278");
	   /* s.setIdentityCard("431022199612250036");
		s.setMobilephone("17708404197");*/
		s.setIpAddress("123.56.180.216");
		s.setUserSource("C");
		microclassService.xfStudyAnswer(s);
	}
	
	//B类答题
	@Test
	public void bldt() throws Exception{
		Study  s=new Study();
		s.setInterfaceId("blyydt");
		s.setSubjectId("712149");  //取题ID
		s.setUserName("杨明畅");		
		s.setIdentityCard("440301199002101119"); //身份证号码
		s.setMobilephone("18603017278"); //手机号码
		s.setIpAddress("123.56.180.216"); //答题IP地址
		s.setSubjectAnswer("A"); //答题答案 
		s.setAnswerDateTime("2017-04-17 10:59:49");
		s.setScoreStartDate("2016-08-04"); //取题时的计分周期始
		s.setScoreEndDate("2017-08-04"); //取题时的计分周期末
		s.setUserSource("C");
		microclassService.xfAnswerQuey(s);
	}
    
}
