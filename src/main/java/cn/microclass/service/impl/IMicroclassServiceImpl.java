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
import cn.microclass.bean.studyclassroom.StudyRecord;
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
		List<Answeroptions>anserList=new ArrayList<>();
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
				s.setSubjectName(body.getString("qt_tm").toString());
				s.setUserName(body.getString("true_name").toString());
				s.setTestQuestionsType(null !=body.getString("qt_stlx")?body.getString("qt_stlx").toString():"");  //图片
				if(body.getString("qt_stlx").toString().equals("判断题")){
					s.setAnswerA("正确");
					s.setAnswerB("错误");
				}
				s.setSubjectImg(null !=body.getString("qt_tp")?body.getString("qt_tp").toString():"");
				if(body.getString("qt_stlx").toString().equals("判断题")){
					Answeroptions anser1=new Answeroptions();
					anser1.setAnswerId("A");
					anser1.setAnswerName("正确");
					anserList.add(anser1);
					Answeroptions anser2=new Answeroptions();
					anser2.setAnswerId("B");
					anser2.setAnswerName("错误");
					anserList.add(anser2);
					
				}else{				
					Object answerA=body.get("xx_a");
					Object answerB=body.get("xx_b");
					Object answerC=body.get("xx_c");
					Object answerD=body.get("xx_d");
					if(answerA!=null){
						Answeroptions anser1=new Answeroptions();
						anser1.setAnswerId("A");
						anser1.setAnswerName(null!=body.get("xx_a")?body.get("xx_a").toString():"");
						anserList.add(anser1);
					}
					if(answerB!=null){
						Answeroptions anser2=new Answeroptions();
						anser2.setAnswerId("B");
						anser2.setAnswerName(null!=body.get("xx_b")?body.get("xx_b").toString():"");
						anserList.add(anser2);
					}
					if(answerC!=null){
						Answeroptions anser3=new Answeroptions();
						anser3.setAnswerId("C");
						anser3.setAnswerName(null!=body.get("xx_c")?body.get("xx_c").toString():"");
						anserList.add(anser3);
					}
					if(answerD!=null){
						Answeroptions anser4=new Answeroptions();
						anser4.setAnswerId("D");
						anser4.setAnswerName(null!=body.get("xx_d")?body.get("xx_d").toString():"");
						anserList.add(anser4);
					}	
				}
				s.setAnsweroptions(anserList);
				base.setMsg(head.get("fhz-msg").toString());	
			}else if(base.getCode().equals("0001")){  //当返回0001的时候代表今日已经答了10题了 不能继续答题了！
				s.setAnswerState(0);
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
	 *消分学习答题： 接口exam002
	 */
	@Override
	public List<BaseBean> xfAnswerQuey(Study s) {
		List<BaseBean>list=new ArrayList<>();
		BaseBean base=new BaseBean();
		List<Study>studyList=new ArrayList<>();		
		String xx="<?xml version=\"1.0\" encoding=\"utf-8\"?><request><head><id>"+s.getSubjectId()+"</id><true_name>"+s.getUserName()+"</true_name>"
				+ "<sfzmhm>"+s.getIdentityCard()+"</sfzmhm><sjhm>"+s.getMobilephone()+"</sjhm><ip>"+s.getIpAddress()+"</ip><dt_qd>"+s.getUserSource()+"</dt_qd>"
				+ "<dt_sj>"+s.getAnswerDateTime()+"</dt_sj><dt_da>"+s.getSubjectAnswer()+"</dt_da><jfzq_start>"+s.getScoreStartDate()+"</jfzq_start><jfzq_end>"+s.getScoreEndDate()+"</jfzq_end></head></request>";
		try {
			String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(), iMicroclassCached.getMethod(), 
					interfaceNumber,xx,iMicroclassCached.getUserid(),iMicroclassCached.getUserpwd(),iMicroclassCached.getKey());
			System.out.println("center实现层次返回=="+respStr);
			JSONObject body=respStr.getJSONObject("body");
			JSONObject head=respStr.getJSONObject("head");
			base.setCode(head.get("fhz").toString());
			base.setMsg(head.get("fhz-msg").toString());
			s.setAnswerDate(s.getAnswerDate());  //答题时间 格式YYYY-MM-DD
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
		List<StudyRecord>studyrecordList=new ArrayList<>();
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
				StudyRecord studyrecord=new StudyRecord();
				JSONObject ob= examlist.getJSONObject(i);
				studyrecord.setAnsLogarithm(Integer.valueOf(ob.get("ddts").toString()));
				studyrecord.setAnswerDate(ob.get("dtrq").toString());	
				studyrecord.setAnswerBatch(ob.get("dtpc").toString());
				studyrecord.setIsComplete(null != ob.get("dtjg")?ob.get("dtjg").toString():"");	
				studyrecordList.add(studyrecord);
			}
			s.setStudyRecord(studyrecordList);
			
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
		List<Answeroptions>anserList=new ArrayList<>();
		String xml="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<request><head><sfzmhm>"+s.getIdentityCard()+"</sfzmhm>"
				+ "<sjhm>"+s.getMobilephone()+"</sjhm><ywlx>"+s.getServiceType()+"</ywlx><ip>"+s.getIpAddress()+"</ip><yhly>"+s.getUserSource()+"</yhly></head></request>";
		
		String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
		try {		
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(), iMicroclassCached.getMethod(), 
					interfaceNumber,xml,iMicroclassCached.getUserid(),iMicroclassCached.getUserpwd(),iMicroclassCached.getKey());
			System.out.println("center实现层次返回=="+respStr);
			JSONObject body=respStr.getJSONObject("body");
			JSONObject head=respStr.getJSONObject("head");
			base.setCode(head.get("fhz").toString());
			//s.setCode(body.get("xx_a").toString());
			if(base.getCode().equals("0000")){  //返回0000代表成功 
				s.setSubjectId(body.get("id").toString());
				s.setIdentityCard(body.get("jszhm").toString());
				s.setAnswerDateTime(body.get("qt_sj").toString());
				s.setTestQuestionsType(body.get("qt_stlx").toString());
				s.setSubjectName(body.get("qt_tm").toString());
				if(body.get("qt_tmlb").toString().equals("文字题")){
					s.setSubjecttype(1);
				}else{
					s.setSubjecttype(2);
				}
				s.setSubjectImg(null !=body.get("qt_tp")?body.get("qt_tp").toString():""); //取题图片
				s.setUserName(body.get("true_name").toString());
				if(body.getString("qt_stlx").toString().equals("判断题")){
					Answeroptions anser1=new Answeroptions();
					anser1.setAnswerId("A");
					anser1.setAnswerName("正确");
					anserList.add(anser1);
					Answeroptions anser2=new Answeroptions();
					anser2.setAnswerId("B");
					anser2.setAnswerName("错误");
					anserList.add(anser2);
					
				}else{
					Object answerA=body.get("xx_a");
					Object answerB=body.get("xx_b");
					Object answerC=body.get("xx_c");
					Object answerD=body.get("xx_d");
					if(answerA!=null){
						Answeroptions anser1=new Answeroptions();
						anser1.setAnswerId("A");
						anser1.setAnswerName(null!=body.get("xx_a")?body.get("xx_a").toString():"");
						anserList.add(anser1);
					}
					if(answerB!=null){
						Answeroptions anser2=new Answeroptions();
						anser2.setAnswerId("B");
						anser2.setAnswerName(null!=body.get("xx_b")?body.get("xx_b").toString():"");
						anserList.add(anser2);
					}
					if(answerC!=null){
						Answeroptions anser3=new Answeroptions();
						anser3.setAnswerId("C");
						anser3.setAnswerName(null!=body.get("xx_c")?body.get("xx_c").toString():"");
						anserList.add(anser3);
					}
					if(answerD!=null){
						Answeroptions anser4=new Answeroptions();
						anser4.setAnswerId("D");
						anser4.setAnswerName(null!=body.get("xx_d")?body.get("xx_d").toString():"");
						anserList.add(anser4);
					}	
				}
				s.setAnsweroptions(anserList);
				s.setServiceType(body.get("ywlx").toString());
				System.err.println("A=="+s.getAnswerA());
				System.err.println("B=="+s.getAnswerB());
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
	 * 6.34.3	行人、非机动车驾驶人道路交通安全学习答题方法
	 */
	@Override
	public List<BaseBean> xrAnswerQuey(Study s) {
		List<BaseBean>list=new ArrayList<>();
		BaseBean base=new BaseBean();
		List<Study>studyList=new ArrayList<>();
		String xml="<?xml version=\"1.0\" encoding=\"utf-8\"?><request><head><id>"+s.getSubjectId()+"</id>"
				+ "<sfzmhm>"+s.getIdentityCard()+"</sfzmhm><sjhm>"+s.getMobilephone()+"</sjhm><ywlx>AQ</ywlx><ip>"+s.getIpAddress()+"</ip><dt_qd>"+s.getUserSource()+"</dt_qd>"
				+ "<dt_da>"+s.getSubjectAnswer()+"</dt_da></head></request>";
		String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
		try {		
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(), iMicroclassCached.getMethod(), 
					interfaceNumber,xml,iMicroclassCached.getUserid(),iMicroclassCached.getUserpwd(),iMicroclassCached.getKey());
			System.out.println("center实现层次返回=="+respStr);
			JSONObject body=respStr.getJSONObject("body");
			JSONObject head=respStr.getJSONObject("head");
			if(head.get("fhz").toString().equals("0002")){
				base.setCode(head.get("fhz").toString());
				base.setMsg(head.get("fhz-msg").toString());   //返回答题错误  或者答题正确
			}else{
				s.setAnswererror(Integer.valueOf(head.get("dcts").toString()));  //答题错误题数
				s.setAnswerCorrect(Integer.valueOf(head.get("ddts").toString()));  //答对题数
				base.setCode(head.get("fhz").toString());
				base.setMsg(head.get("fhz-msg").toString());   //返回答题错误  或者答题正确
				s.setBatchResult(head.get("pcjg").toString());
				s.setSurplusAnswe(Integer.valueOf(head.get("syts").toString())); //剩余题数
				s.setServiceType(head.get("ywlx").toString()); //业务类型
				s.setTrainResult(null !=head.get("pxjg")?head.get("pxjg").toString():"");  //接收培训结果
				s.setAnswerDate(s.getAnswerDate());  //答题时间 格式YYYY-MM-DD
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
	 *  电动车违法学习查询
	 */
	@Override
	public List<BaseBean> ddcStudyQuery(Study s) {
		List<BaseBean>list=new ArrayList<>();
		BaseBean base=new BaseBean();
		List<Study>studyList=new ArrayList<>();
		List<StudyRecord>studyrecordList=new ArrayList<>();
		
	
		String xml="<?xml version=\"1.0\" encoding=\"utf-8\"?><request><head><sfzmhm>"+s.getIdentityCard()+"</sfzmhm><sjhm>"+s.getMobilephone()+"</sjhm>"
				+ "<ywlx>"+s.getServiceType()+"</ywlx><jdsbh>"+s.getDecisionId()+"</jdsbh><ip>"+s.getIpAddress()+"</ip><yhly>"+s.getUserSource()+"</yhly></head></request>";
		try {
			String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(), iMicroclassCached.getMethod(), 
			interfaceNumber,xml,iMicroclassCached.getUserid(),iMicroclassCached.getUserpwd(),iMicroclassCached.getKey());
			System.out.println("center实现层次返回=="+respStr);
			JSONObject body=respStr.getJSONObject("body"); //获取json中body
			JSONObject head=respStr.getJSONObject("head"); //获取json中head
			base.setCode(head.get("fhz").toString());
			JSONArray examlist= body.getJSONArray("examlist");  //从json中body获取examlist这个集合
			s.setDecisionId(null !=body.get("jdsbh")?body.get("jdsbh").toString():""); //决定书编号
			s.setUserName(body.get("true_name").toString());   //名字
			s.setIdentityCard(body.get("jszhm").toString());  //驾驶证号码 或者身份证号码
			System.out.println(examlist);
			for(int i=0;i<examlist.size();i++){
				StudyRecord studyrecord=new StudyRecord();
				JSONObject ob= examlist.getJSONObject(i);
				studyrecord.setAnsLogarithm(Integer.valueOf(ob.get("ddts").toString()));
				studyrecord.setAnswerDate(ob.get("dtrq").toString());	
				studyrecord.setAnswerBatch(ob.get("dtpc").toString());
				studyrecord.setIsComplete(null != ob.get("dtjg")?ob.get("dtjg").toString():"");	
				studyrecordList.add(studyrecord);
			}
			s.setStudyRecord(studyrecordList);
			base.setMsg(head.get("fhz-msg").toString());	
			
			studyList.add(s);
			base.setData(studyList);
			list.add(base);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return list;
	}
	/**
	 * 电动车违法随机取题
	 */
	
	@Override
	public List<BaseBean> ddcStudyAnswer(Study s) {
		List<BaseBean>list=new ArrayList<>();
		BaseBean base=new BaseBean();
		List<Study>studyList=new ArrayList<>();
		List<StudyRecord>studyrecordList=new ArrayList<>();
		List<Answeroptions>anserList=new ArrayList<>();
		String xml="<?xml version=\"1.0\" encoding=\"utf-8\"?><request><head><sfzmhm>"+s.getIdentityCard()+"</sfzmhm><sjhm>"+s.getMobilephone()+"</sjhm>"
				+ "<ywlx>"+s.getServiceType()+"</ywlx><jdsbh>"+s.getDecisionId()+"</jdsbh><ip>"+s.getIpAddress()+"</ip><yhly>"+s.getUserSource()+"</yhly></head></request>";
		try {
			String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(), iMicroclassCached.getMethod(), 
					interfaceNumber,xml,iMicroclassCached.getUserid(),iMicroclassCached.getUserpwd(),iMicroclassCached.getKey());
			System.out.println("center实现层次返回=="+respStr);
			JSONObject body=respStr.getJSONObject("body");
			JSONObject head=respStr.getJSONObject("head");
			base.setCode(head.get("fhz").toString());
			//s.setCode(body.get("xx_a").toString());
			if(base.getCode().equals("0000")){  //返回0000代表成功 
				s.setSubjectId(body.get("id").toString());
				s.setDecisionId(null !=body.get("jdsbh")?body.get("jdsbh").toString():""); //决定书编号
				s.setIdentityCard(body.get("jszhm").toString());
				s.setAnswerDateTime(body.get("qt_sj").toString());
				s.setTestQuestionsType(body.get("qt_stlx").toString());  //选择题或者判断题
				s.setSubjectName(body.get("qt_tm").toString());
				s.setSubjectImg(null !=body.get("qt_tp")?body.get("qt_tp").toString():""); //取题图片
				
				if(body.getString("qt_stlx").toString().equals("判断题")){
					Answeroptions anser1=new Answeroptions();
					anser1.setAnswerId("A");
					anser1.setAnswerName("正确");
					anserList.add(anser1);
					Answeroptions anser2=new Answeroptions();
					anser2.setAnswerId("B");
					anser2.setAnswerName("错误");
					anserList.add(anser2);
					
				}else{					
					Object answerA=body.get("xx_a");
					Object answerB=body.get("xx_b");
					Object answerC=body.get("xx_c");
					Object answerD=body.get("xx_d");
					if(answerA!=null){
						Answeroptions anser1=new Answeroptions();
						anser1.setAnswerId("A");
						anser1.setAnswerName(null!=body.get("xx_a")?body.get("xx_a").toString():"");
						anserList.add(anser1);
					}
					if(answerB!=null){
						Answeroptions anser2=new Answeroptions();
						anser2.setAnswerId("B");
						anser2.setAnswerName(null!=body.get("xx_b")?body.get("xx_b").toString():"");
						anserList.add(anser2);
					}
					if(answerC!=null){
						Answeroptions anser3=new Answeroptions();
						anser3.setAnswerId("C");
						anser3.setAnswerName(null!=body.get("xx_c")?body.get("xx_c").toString():"");
						anserList.add(anser3);
					}
					if(answerD!=null){
						Answeroptions anser4=new Answeroptions();
						anser4.setAnswerId("D");
						anser4.setAnswerName(null!=body.get("xx_d")?body.get("xx_d").toString():"");
						anserList.add(anser4);
					}	
				}
				s.setAnsweroptions(anserList);
				s.setServiceType(body.get("ywlx").toString());
				System.err.println("A=="+s.getAnswerA());
				System.err.println("B=="+s.getAnswerB());
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

	List<Study>jgList=null;
	/**
	 * 电动车违法答题方法
	 */
	@Override
	public List<BaseBean> ddcAnswerQuey(Study s) {
	
		List<BaseBean>list=new ArrayList<>();
		BaseBean base=new BaseBean();
		List<Study>studyList=new ArrayList<>();
		String xml="<?xml version=\"1.0\" encoding=\"utf-8\"?><request><head><id>"+s.getSubjectId()+"</id><sfzmhm>"+s.getIdentityCard()+"</sfzmhm><sjhm>"+s.getMobilephone()+"</sjhm>"
				+ "<ywlx>"+s.getServiceType()+"</ywlx><jdsbh>"+s.getDecisionId()+"</jdsbh><ip>"+s.getIpAddress()+"</ip><dt_qd>C</dt_qd><yhly>"+s.getUserSource()+"</yhly><dt_da>"+s.getSubjectAnswer()+"</dt_da></head></request>";
		
		String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
		try {		
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(), iMicroclassCached.getMethod(), 
					interfaceNumber,xml,iMicroclassCached.getUserid(),iMicroclassCached.getUserpwd(),iMicroclassCached.getKey());
			System.out.println("center实现层次返回=="+respStr);
			JSONObject body=respStr.getJSONObject("body");
			JSONObject head=respStr.getJSONObject("head");
			if(head.get("fhz").toString().equals("0002")){
				base.setCode(head.get("fhz").toString());
				base.setMsg(head.get("fhz-msg").toString());   //返回答题错误  或者答题正确
			}else{
				s.setAnswererror(Integer.valueOf(head.get("dcts").toString()));  //答题错误题数
				s.setAnswerCorrect(Integer.valueOf(head.get("ddts").toString()));  //答对题数
				base.setCode(head.get("fhz").toString());
				base.setMsg(head.get("fhz-msg").toString());   //返回答题错误  或者答题正确
				s.setBatchResult(head.get("pcjg").toString());
				s.setSurplusAnswe(Integer.valueOf(head.get("syts").toString())); //剩余题数
				s.setAnswerDate(s.getAnswerDate());  //答题时间 格式YYYY-MM-DD
				/*if(s.getBatchResult().equals("不合格")||s.getBatchResult().equals("合格")){
					
					return list;
				}*/
				
				s.setServiceType(head.get("ywlx").toString()); //业务类型
				/*Object jdsbh=head.get("jdsbh").toString();
				if(null!=jdsbh){
					s.setDecisionId(null !=head.get("jdsbh")?head.get("jdsbh").toString():""); //决定书编号
				}*/
				s.setTrainResult(null !=head.get("pxjg")?head.get("pxjg").toString():"");  //接收培训结果
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
	 * 满分查询
	 */
	@Override
	public List<BaseBean> mfStudyQuery(Study s) {
		List<BaseBean>list=new ArrayList<>();
		BaseBean base=new BaseBean();
		List<Study>studyList=new ArrayList<>();
		List<StudyRecord>studyrecordList=new ArrayList<>();
		String xfxx="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<request><head><sfzmhm>"+s.getIdentityCard()+"</sfzmhm>"
				+ "<sjhm>"+s.getMobilephone()+"</sjhm><ywlx>"+s.getServiceType()+"</ywlx><ip>"+s.getIpAddress()+"</ip><yhly>"+s.getUserSource()+"</yhly></head></request>";
		String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
		try {
			
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(), iMicroclassCached.getMethod(), 
			interfaceNumber,xfxx,iMicroclassCached.getUserid(),iMicroclassCached.getUserpwd(),iMicroclassCached.getKey());
			System.out.println("center实现层次返回=="+respStr);
			JSONObject body=respStr.getJSONObject("body"); //获取json中body
			JSONObject head=respStr.getJSONObject("head"); //获取json中head
			
			s.setScoreStartDate(body.get("jfzq_start").toString());
			s.setScoreEndDate(body.get("jfzq_end").toString());
			JSONArray examlist= body.getJSONArray("examlist");  //从json中body获取examlist这个集合
			System.out.println(examlist);
			for(int i=0;i<examlist.size();i++){
				StudyRecord studyrecord=new StudyRecord();
				JSONObject ob= examlist.getJSONObject(i);
				studyrecord.setAnsLogarithm(Integer.valueOf(ob.get("ddts").toString()));
				studyrecord.setAnswerDate(ob.get("dtrq").toString());	
				studyrecord.setAnswerBatch(ob.get("dtpc").toString());
				studyrecord.setIsComplete(null != ob.get("dtjg")?ob.get("dtjg").toString():"");	
				studyrecordList.add(studyrecord);
			}
			s.setStudyRecord(studyrecordList);
			base.setCode(head.get("fhz").toString());
			base.setMsg(head.get("fhz-msg").toString());				
			studyList.add(s);
			base.setData(studyList);
			list.add(base);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 满分随机取题目
	 */
	@Override
	public List<BaseBean> mfStudyAnswer(Study s) {
		BaseBean base=new BaseBean();
		List<BaseBean>list=new ArrayList<>();
		List<Study>studyList=new ArrayList<>();
		List<Answeroptions>anserList=new ArrayList<>();
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
			if(base.getCode().equals("0000")){  //返回0000代表成功 
				s.setSubjectId(body.get("id").toString());
				s.setScoreStartDate(body.get("jfzq_start").toString());
				s.setScoreEndDate(body.get("jfzq_end").toString());
				s.setAnswerDateTime(body.get("qt_sj").toString());
				s.setTestQuestionsType(body.get("qt_stlx").toString());
				s.setSubjectName(body.getString("qt_tm").toString());
				s.setUserName(body.getString("true_name").toString());
				s.setTestQuestionsType(null !=body.getString("qt_stlx")?body.getString("qt_stlx").toString():"");  //图片
				if(body.getString("qt_stlx").toString().equals("判断题")){
					Answeroptions anser1=new Answeroptions();
					anser1.setAnswerId("A");
					anser1.setAnswerName("正确");
					anserList.add(anser1);
					Answeroptions anser2=new Answeroptions();
					anser2.setAnswerId("B");
					anser2.setAnswerName("错误");
					anserList.add(anser2);
				}else{					
					Object answerA=body.get("xx_a");
					Object answerB=body.get("xx_b");
					Object answerC=body.get("xx_c");
					Object answerD=body.get("xx_d");
					if(answerA!=null){
						Answeroptions anser1=new Answeroptions();
						anser1.setAnswerId("A");
						anser1.setAnswerName(null!=body.get("xx_a")?body.get("xx_a").toString():"");
						anserList.add(anser1);
					}
					if(answerB!=null){
						Answeroptions anser2=new Answeroptions();
						anser2.setAnswerId("B");
						anser2.setAnswerName(null!=body.get("xx_b")?body.get("xx_b").toString():"");
						anserList.add(anser2);
					}
					if(answerC!=null){
						Answeroptions anser3=new Answeroptions();
						anser3.setAnswerId("C");
						anser3.setAnswerName(null!=body.get("xx_c")?body.get("xx_c").toString():"");
						anserList.add(anser3);
					}
					if(answerD!=null){
						Answeroptions anser4=new Answeroptions();
						anser4.setAnswerId("D");
						anser4.setAnswerName(null!=body.get("xx_d")?body.get("xx_d").toString():"");
						anserList.add(anser4);
					}	
				}
				s.setAnsweroptions(anserList);
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
	 * 满分随机答题
	 */
	@Override
	public List<BaseBean> mfAnswerQuey(Study s) {
		List<BaseBean>list=new ArrayList<>();
		BaseBean base=new BaseBean();
		List<Study>studyList=new ArrayList<>();		
		String xx="<?xml version=\"1.0\" encoding=\"utf-8\"?><request><head><id>"+s.getSubjectId()+"</id><true_name>"+s.getUserName()+"</true_name>"
				+ "<sfzmhm>"+s.getIdentityCard()+"</sfzmhm><sjhm>"+s.getMobilephone()+"</sjhm><ip>"+s.getIpAddress()+"</ip><dt_qd>"+s.getUserSource()+"</dt_qd>"
				+ "<dt_sj>"+s.getAnswerDateTime()+"</dt_sj><dt_da>"+s.getSubjectAnswer()+"</dt_da><jfzq_start>"+s.getScoreStartDate()+"</jfzq_start><jfzq_end>"+s.getScoreEndDate()+"</jfzq_end></head></request>";
		try {
			String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(), iMicroclassCached.getMethod(), 
					interfaceNumber,xx,iMicroclassCached.getUserid(),iMicroclassCached.getUserpwd(),iMicroclassCached.getKey());
			System.out.println("center实现层次返回=="+respStr);
			JSONObject body=respStr.getJSONObject("body");
			JSONObject head=respStr.getJSONObject("head");
			s.setTrainResult(null !=head.get("pxjg")?head.get("pxjg").toString():"");  //接收培训结果
			s.setBatchResult(null !=head.get("pcjg")?head.get("pcjg").toString():"");  //批次结果
			s.setAnswerDate(s.getAnswerDate());  //答题时间 格式YYYY-MM-DD
			base.setCode(head.get("fhz").toString());
			base.setMsg(head.get("fhz-msg").toString());
			studyList.add(s);
			base.setData(studyList);
			list.add(base);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * B类学习查询
	 */
	@Override
	public List<BaseBean> blStudyQuery(Study s) {
		List<BaseBean>list=new ArrayList<>();
		BaseBean base=new BaseBean();
		List<Study>studyList=new ArrayList<>();
		List<StudyRecord>studyrecordList=new ArrayList<>();
		String xfxx="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<request><head><sfzmhm>"+s.getIdentityCard()+"</sfzmhm>"
				+ "<sjhm>"+s.getMobilephone()+"</sjhm><ywlx>"+s.getServiceType()+"</ywlx><ip>"+s.getIpAddress()+"</ip><yhly>"+s.getUserSource()+"</yhly></head></request>";
		String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
		try {
			
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(), iMicroclassCached.getMethod(), 
			interfaceNumber,xfxx,iMicroclassCached.getUserid(),iMicroclassCached.getUserpwd(),iMicroclassCached.getKey());
			System.out.println("center实现层次返回=="+respStr);
			JSONObject body=respStr.getJSONObject("body"); //获取json中body
			JSONObject head=respStr.getJSONObject("head"); //获取json中head
			
			s.setScoreStartDate(body.get("jfzq_start").toString());
			s.setScoreEndDate(body.get("jfzq_end").toString());
			s.setUserName(body.get("true_name").toString());
			s.setIdentityCard(body.get("jszhm").toString());
			JSONArray examlist= body.getJSONArray("examlist");  //从json中body获取examlist这个集合
			System.out.println(examlist);
			for(int i=0;i<examlist.size();i++){
				StudyRecord studyrecord=new StudyRecord();
				JSONObject ob= examlist.getJSONObject(i);
				studyrecord.setAnsLogarithm(Integer.valueOf(ob.get("ddts").toString()));
				studyrecord.setAnswerDate(ob.get("dtrq").toString());	
				studyrecord.setAnswerBatch(ob.get("dtpc").toString());
				studyrecord.setIsComplete(null != ob.get("dtjg")?ob.get("dtjg").toString():"");	
				studyrecordList.add(studyrecord);
			}
			s.setStudyRecord(studyrecordList);
			base.setCode(head.get("fhz").toString());
			base.setMsg(head.get("fhz-msg").toString());				
			studyList.add(s);
			base.setData(studyList);
			list.add(base);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * B类学习随机取题
	 */
	@Override
	public List<BaseBean> blStudyAnswer(Study s) {
		BaseBean base=new BaseBean();
		List<BaseBean>list=new ArrayList<>();
		List<Study>studyList=new ArrayList<>();
		List<Answeroptions>anserList=new ArrayList<>();
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
				s.setSubjectName(body.getString("qt_tm").toString());
				s.setUserName(body.getString("true_name").toString());
				s.setTestQuestionsType(null !=body.getString("qt_stlx")?body.getString("qt_stlx").toString():"");  //图片
				s.setSubjectImg(null !=body.getString("qt_tp")?body.getString("qt_tp").toString():"");
				if(body.getString("qt_tmlb").toString().equals("文字题")){ //1.代表文字题，2代表图片题
					s.setSubjecttype(1);
				}else{
					s.setSubjecttype(2);
				}
				s.setSubjectImg(null !=body.get("qt_tp")?body.get("qt_tp").toString():""); //取题图片
				if(body.getString("qt_stlx").toString().equals("判断题")){
					Answeroptions anser1=new Answeroptions();
					anser1.setAnswerId("A");
					anser1.setAnswerName("正确");
					anserList.add(anser1);
					Answeroptions anser2=new Answeroptions();
					anser2.setAnswerId("B");
					anser2.setAnswerName("错误");
					anserList.add(anser2);
					
				}else{
					/*s.setAnswerA(null!=body.get("xx_a")?body.get("xx_a").toString():"");
					s.setAnswerB(null!=body.get("xx_b")?body.get("xx_b").toString():"");
					s.setAnswerC(null !=body.get("xx_c")?body.get("xx_c").toString():"");
					s.setAnswerD(null !=body.get("xx_d")?body.get("xx_d").toString():"");*/
					
					Object answerA=body.get("xx_a");
					Object answerB=body.get("xx_b");
					Object answerC=body.get("xx_c");
					Object answerD=body.get("xx_d");
					if(answerA!=null){
						Answeroptions anser1=new Answeroptions();
						anser1.setAnswerId("A");
						anser1.setAnswerName(null!=body.get("xx_a")?body.get("xx_a").toString():"");
						anserList.add(anser1);
					}
					if(answerB!=null){
						Answeroptions anser2=new Answeroptions();
						anser2.setAnswerId("B");
						anser2.setAnswerName(null!=body.get("xx_b")?body.get("xx_b").toString():"");
						anserList.add(anser2);
					}
					if(answerC!=null){
						Answeroptions anser3=new Answeroptions();
						anser3.setAnswerId("C");
						anser3.setAnswerName(null!=body.get("xx_c")?body.get("xx_c").toString():"");
						anserList.add(anser3);
					}
					if(answerD!=null){
						Answeroptions anser4=new Answeroptions();
						anser4.setAnswerId("D");
						anser4.setAnswerName(null!=body.get("xx_d")?body.get("xx_d").toString():"");
						anserList.add(anser4);
					}	
				}
				s.setAnsweroptions(anserList);
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
	 * B类学习答题
	 */
	@Override
	public List<BaseBean> blAnswerQuey(Study s) {
		List<BaseBean>list=new ArrayList<>();
		BaseBean base=new BaseBean();
		List<Study>studyList=new ArrayList<>();		
		String xx="<?xml version=\"1.0\" encoding=\"utf-8\"?><request><head><id>"+s.getSubjectId()+"</id><true_name>"+s.getUserName()+"</true_name>"
				+ "<sfzmhm>"+s.getIdentityCard()+"</sfzmhm><sjhm>"+s.getMobilephone()+"</sjhm><ip>"+s.getIpAddress()+"</ip><dt_qd>"+s.getUserSource()+"</dt_qd>"
				+ "<dt_sj>"+s.getAnswerDateTime()+"</dt_sj><dt_da>"+s.getSubjectAnswer()+"</dt_da><jfzq_start>"+s.getScoreStartDate()+"</jfzq_start><jfzq_end>"+s.getScoreEndDate()+"</jfzq_end></head></request>";
		try {
			String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(), iMicroclassCached.getMethod(), 
					interfaceNumber,xx,iMicroclassCached.getUserid(),iMicroclassCached.getUserpwd(),iMicroclassCached.getKey());
			System.out.println("center实现层次返回=="+respStr);
			JSONObject body=respStr.getJSONObject("body");
			JSONObject head=respStr.getJSONObject("head");
			s.setBatchResult(null !=head.get("pcjg")?head.get("pcjg").toString():"");  //批次结果
			s.setAnswerDate(s.getAnswerDate());  //答题时间 格式YYYY-MM-DD
			base.setCode(head.get("fhz").toString());
			base.setMsg(head.get("fhz-msg").toString());
			studyList.add(s);
			base.setData(studyList);
			list.add(base);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	
	/**
	 * 微课所有消分学习查询
	 */
	@Override
	public List<BaseBean> classroomStudyfind(Study s) {
		List<BaseBean>list=new ArrayList<>();
		BaseBean base=new BaseBean();
		List<Study>studyList=new ArrayList<>();
		String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><request><head><sfzmhm>"+s.getIdentityCard()+"</sfzmhm>"
				+ "<sjhm>"+s.getMobilephone()+"</sjhm><ip>"+s.getIpAddress()+"</ip><yhly>"+s.getUserSource()+"</yhly></head></request>";
		if(s.getClassroomId().equals("1")){
				
		}//消分学习
		try {
			
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