package cn.microclass.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.microclass.bean.WechatUserInfoBean;
import cn.microclass.bean.studyclassroom.Answeroptions;
import cn.microclass.bean.studyclassroom.Study;
import cn.microclass.cached.impl.IMicroclassCachedImpl;
import cn.microclass.service.IMicroclassServer;
import cn.sdk.bean.BaseBean;
import cn.sdk.webservice.WebServiceClient;
@Service("microclassService")
@SuppressWarnings(value="all")
public class IMicroclassServiceImpl implements IMicroclassServer {

	@Autowired
	private IMicroclassCachedImpl  iMicroclassCached;
	
	
	
	/**
	 * 消分业务相关信息  接口 exam003
	 */
	public List<BaseBean> xfStudyQuery(Study s) {
		List<BaseBean>list=new ArrayList<>();
		BaseBean base=new BaseBean();
		List<Study>studyList=new ArrayList<>();
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><request><head><sfzmhm>"+s.getIdentityCard()+"</sfzmhm>"
				+ "<sjhm>"+s.getMobilephone()+"</sjhm><ip>"+s.getIpAddress()+"</ip><yhly>"+s.getUserSource()+"</yhly></head></request>";
		try {
			String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(), iMicroclassCached.getMethod(), 
					interfaceNumber,xml,iMicroclassCached.getUserid(),iMicroclassCached.getUserpwd(),iMicroclassCached.getKey());
			System.out.println("center实现层次返回=="+respStr);
			JSONObject body=respStr.getJSONObject("body");
			JSONObject head=respStr.getJSONObject("head");
			base.setCode(head.get("fhz").toString());
			if(base.getCode().equals("0000")){  //返回0000代表成功 
				s.setAnswerCorrect(Integer.valueOf(body.get("ddts").toString())); //答题题数
				if(s.getAnswerCorrect()>=10){ //当答题对数等于第10题的时候
					s.setAnswerState(2); //1.代表可以答题,2.代表今天消分学习已答对10题，请明天继续
				}else{
					s.setAnswerState(1);
				}
				s.setScoreStartDate(body.get("jfzq_start").toString());
				s.setScoreEndDate(body.get("jfzq_end").toString());
				s.setIntegral(body.get("ljjf").toString()); //累计积分
				s.setUserName(body.getString("true_name").toString());
				base.setMsg(head.get("fhz-msg").toString());	
			}else{
				base.setMsg(head.get("fhz-msg").toString());
			}
			studyList.add(s);
			base.setData(studyList);
			list.add(base);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 消分学习随机取题接口 exam001
	 */
	@Override
	public List<BaseBean> xfStudyAnswer(Study s) {
		BaseBean base=new BaseBean();
		List<BaseBean>list=new ArrayList<>();
		List<Study>studyList=new ArrayList<>();
		String x = "<?xml version=\"1.0\" encoding=\"utf-8\"?><request><head><sfzmhm>431022199612250036</sfzmhm><sjhm>17708404197</sjhm><ip>123.56.180.216</ip><yhly>C</yhly></head></request>";
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><request><head><sfzmhm>"+s.getIdentityCard()+"</sfzmhm>"
				+ "<sjhm>"+s.getMobilephone()+"</sjhm><ip>"+s.getIpAddress()+"</ip><yhly>"+s.getUserSource()+"</yhly></head></request>";

		String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
		try {		
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(), iMicroclassCached.getMethod(), 
					interfaceNumber,xml,iMicroclassCached.getUserid(),iMicroclassCached.getUserpwd(),iMicroclassCached.getKey());
			System.out.println("serviceImplXML=="+xml);
			System.out.println("center实现层次返回=="+respStr);
			JSONObject body=respStr.getJSONObject("body");
			JSONObject head=respStr.getJSONObject("head");
			base.setCode(head.get("fhz").toString());
			//s.setCode(body.get("xx_a").toString());
			if(base.getCode().equals("0000")){  //返回0000代表成功 
				s.setSubjectId(body.get("id").toString());
				s.setScoreStartDate(body.get("jfzq_start").toString());
				s.setScoreEndDate(body.get("jfzq_end").toString());
				s.setAnswerDateTime(body.get("qt_sj").toString());
				s.setTestQuestionsType(body.get("qt_stlx").toString());
				//Answeroptions answer=new Answeroptions();	
				s.setSubjectName(body.getString("qt_tm").toString());
				s.setUserName(body.getString("true_name").toString());
				if(body.getString("qt_tmlb").toString().equals("文字题")){ //1.代表文字题，2代表图片题
					s.setSubjecttype(1);
				}else{
					s.setSubjecttype(2);
				}
				base.setMsg(head.get("fhz-msg").toString());	
			}else{
				base.setMsg(head.get("fhz-msg").toString());
			}
			System.out.println("取题答案"+s.getCode());
			System.out.println("积分周期开始时间=="+s.getScoreStartDate());
			System.out.println("记分周期结束时间=="+s.getScoreEndDate());
			studyList.add(s);
			base.setData(studyList);
			list.add(base);
		} catch (Exception e) {
		
			e.printStackTrace();
		}
		return list;
	}

	
	int answererror=0; //答题错数
	int surplusAnswe=0; //答题对数
	int answerCount=20; //一共就20题目,答完就没了
	/**
	 *消分学习答题： 接口exam002
	 */
	@Override
	public List<BaseBean> xfAnswerQuey(Study s) {
		List<BaseBean>list=new ArrayList<>();
		BaseBean base=new BaseBean();
		List<Study>studyList=new ArrayList<>();
	
		String xml="<?xml version=\"1.0\" encoding=\"utf-8\"?><request><head><id>3622517</id><true_name>"+s.getUserName()+"</true_name>"
				+ "<sfzmhm>"+s.getIdentityCard()+"</sfzmhm><sjhm>"+s.getMobilephone()+"</sjhm><ip>"+s.getIpAddress()+"</ip><dt_qd>C</dt_qd>"
				+ "<dt_sj>"+s.getAnswerDateTime()+"</dt_sj><dt_da>"+s.getSubjectAnswer()+"</dt_da><jfzq_start>"+s.getScoreStartDate()+"</jfzq_start>"
				+ "<jfzq_end>"+s.getScoreEndDate()+"</jfzq_end></head></request>";
		try {
			String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(), iMicroclassCached.getMethod(), 
					interfaceNumber,xml,iMicroclassCached.getUserid(),iMicroclassCached.getUserpwd(),iMicroclassCached.getKey());
			System.out.println("center实现层次返回=="+respStr);
			JSONObject body=respStr.getJSONObject("body");
			JSONObject head=respStr.getJSONObject("head");
			base.setCode(head.get("fhz").toString());
			if(answerCount>=1){
				if(base.getCode().equals("0000")){
					surplusAnswe++;   //答题对的时候 答题对数+1；
					answerCount--;    //答对的后 剩余题数-1;
					base.setMsg(head.get("fhz-msg").toString());
				}else if(base.getCode().equals("0001")){
					answererror++;   //答题错误的时候 答题错数+1；
					answerCount--;   //答题错误的时候 剩余题数-1;
					base.setMsg(head.get("fhz-msg").toString());
				}else{
					base.setMsg(head.get("fhz-msg").toString());
				}
				s.setSurplusAnswe(answerCount);  //剩余题数
				s.setAnswererror(answererror);   //答题错数
				s.setAnswerCorrect(surplusAnswe); //答题对数
			}else{
				s.setAnswerState(3); //已经答满20题了,不能继续答题了
				answererror=0;
				surplusAnswe=0;
				answerCount=20;
			}
			
			studyList.add(s);
			base.setData(studyList);
			list.add(base);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	
	

	/**
	 * 6.34.3	行人、非机动车驾驶人道路交通安全学习查询接口 
	 */
	@Override
	public List<BaseBean> xrStudyQuery(Study s) {
		List<BaseBean>list=new ArrayList<>();
		BaseBean base=new BaseBean();
		List<Study>studyList=new ArrayList<>();
		List<Study>studyLists=new ArrayList<>();
		
	
		String xfxx="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<request><head><sfzmhm>"+s.getIdentityCard()+"</sfzmhm>"
				+ "<sjhm>"+s.getMobilephone()+"</sjhm><ywlx>"+s.getServiceType()+"</ywlx><ip>"+s.getIpAddress()+"</ip><yhly>"+s.getUserSource()+"</yhly></head></request>";
		
		try {
			String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(), iMicroclassCached.getMethod(), 
					interfaceNumber,xfxx,iMicroclassCached.getUserid(),iMicroclassCached.getUserpwd(),iMicroclassCached.getKey());
			System.out.println("center实现层次返回=="+respStr);
			JSONObject body=respStr.getJSONObject("body"); //获取json中body
			JSONObject head=respStr.getJSONObject("head"); //获取json中head
			base.setCode(head.get("fhz").toString());
			JSONArray examlist= body.getJSONArray("examlist");  //从json中body获取examlist这个集合
			System.out.println(examlist);
			for(int i=0;i<examlist.size();i++){
				JSONObject ob= examlist.getJSONObject(i);
				System.out.println("ket=="+ob);
				String ddts=ob.get("ddts").toString();
				System.out.println("ddts=="+ddts);
				s.setAnswerCorrect(Integer.valueOf(ob.get("ddts").toString()));
				s.setAnswerDate(ob.get("dtrq").toString());
				studyLists.add(s);
				
			}
			
			if(base.getCode().equals("0000")){  //返回0000代表成功 
				s.setAnswerState(1);
				s.setIdentityCard(body.get("jszhm").toString());
				s.setUserName(body.getString("true_name").toString());
				base.setMsg(head.get("fhz-msg").toString());	
			}else{
				base.setMsg(head.get("fhz-msg").toString());
			}
			studyList.add(s);
			base.setData(studyList);
			base.setData(studyLists);
			list.add(base);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 6.34.3	行人、非机动车驾驶人道路交通安全学习随机取题
	 */
	@Override
	public List<BaseBean> xrStudyAnswer(Study s) {
		List<BaseBean>list=new ArrayList<>();
		BaseBean base=new BaseBean();
		List<Study>studyList=new ArrayList<>();
		
		
		return null;
	}
	
	/**
	 * 6.34.3	行人、非机动车驾驶人道路交通安全学习答题方法
	 */
	@Override
	public List<BaseBean> xrAnswerQuey(Study s) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	
	
	
	

	
	
	public  void timer4(Study s) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.HOUR_OF_DAY, 12); // 控制时
	    calendar.set(Calendar.MINUTE, 0);    // 控制分
	    calendar.set(Calendar.SECOND, 0);    // 控制秒
	 
	    Date time = calendar.getTime();     // 得出执行任务的时间,此处为今天的12：00：00
	 
	    Timer timer = new Timer();
	    timer.scheduleAtFixedRate(new TimerTask() {
	      public void run() {
	    	 
	      }
	    }, time, 1000 * 60 * 60 * 24);// 这里设定将延时每天固定执行
	  }

	
	

	

	}