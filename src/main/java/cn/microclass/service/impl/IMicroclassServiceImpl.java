package cn.microclass.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.container.Container;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.microclass.bean.WechatUserInfoBean;
import cn.microclass.bean.studyclassroom.Answeroptions;
import cn.microclass.bean.studyclassroom.Study;
import cn.microclass.bean.studyclassroom.StudyRecord;
import cn.microclass.cached.impl.IMicroclassCachedImpl;
import cn.microclass.service.IMicroclassService;
import cn.sdk.bean.BaseBean;
import cn.sdk.util.StringUtil;
import cn.sdk.webservice.WebServiceClient;
@Service("microclassService")
@SuppressWarnings(value="all")
public class IMicroclassServiceImpl implements IMicroclassService {

	@Autowired
	private IMicroclassCachedImpl  iMicroclassCached;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	
	/**
	 * 消分业务相关信息  接口 exam003
	 * @throws Exception 
	 */
	public List<BaseBean> xfStudyQuery(Study s) throws Exception {
		List<BaseBean>list=new ArrayList<>();
		BaseBean base=new BaseBean();
		List<Study>studyList=new ArrayList<>();
		try {
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><request><head><sfzmhm>"+s.getIdentityCard()+"</sfzmhm>"
				+ "<sjhm>"+s.getMobilephone()+"</sjhm><ip>"+s.getIpAddress()+"</ip><yhly>"+s.getUserSource()+"</yhly></head></request>";
		
			String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(), iMicroclassCached.getMethod(), 
					interfaceNumber,xml,iMicroclassCached.getUserid(),iMicroclassCached.getUserpwd(),iMicroclassCached.getKey());
			logger.info(respStr.toJSONString());
			JSONObject body=respStr.getJSONObject("body");
			JSONObject head=respStr.getJSONObject("head");
			base.setCode(head.get("fhz").toString());
			if(base.getCode().equals("0000")){  //返回0000代表成功 
				s.setAnswerCorrect(Integer.valueOf(body.get("ddts").toString())); //答题题数
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
			logger.error("xfStudyQuery出错："+ s.toString(),e);
			throw e;
		}
		return list;
	}
	
	/**
	 * 消分学习随机取题接口 exam001
	 * @throws Exception 
	 */
	@Override
	public List<BaseBean> xfStudyAnswer(Study s) throws Exception {
		BaseBean base=new BaseBean();
		List<BaseBean>list=new ArrayList<>();
		List<Study>studyList=new ArrayList<>();
		List<Answeroptions>anserList=new ArrayList<>();
		try {
		String xml ="";
		if(s.getClassroomId().equals("1")||s.getClassroomId().equals("2")||s.getClassroomId().equals("3")){
			
		 xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><request><head><sfzmhm>"+s.getIdentityCard()+"</sfzmhm>"
				+ "<sjhm>"+s.getMobilephone()+"</sjhm><ip>"+s.getIpAddress()+"</ip><yhly>"+s.getUserSource()+"</yhly></head></request>";
		}else{
		 xml="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request><head><sfzmhm>"+s.getIdentityCard()+"</sfzmhm>"
					+ "<sjhm>"+s.getMobilephone()+"</sjhm><ywlx>"+s.getServiceType()+"</ywlx><ip>"+s.getIpAddress()+"</ip><yhly>"+s.getUserSource()+"</yhly></head></request>";
			
		}

		String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
				
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(), iMicroclassCached.getMethod(), 
					interfaceNumber,xml,iMicroclassCached.getUserid(),iMicroclassCached.getUserpwd(),iMicroclassCached.getKey());
			logger.info(respStr.toJSONString());
			
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
				s.setIdentityCard(body.getString("jszhm").toString());
				s.setTestQuestionsType(null !=body.getString("qt_stlx")?body.getString("qt_stlx").toString():"");  //图片
				s.setSubjectImg(null !=body.getString("qt_tp")?body.getString("qt_tp").toString():"");
				if(body.getString("qt_stlx").toString().equals("判断题")){
					Answeroptions anser1=new Answeroptions();
					anser1.setAnswerId("A");
					anser1.setAnswerIds("Y");
					anser1.setAnswerName("正确");
					anserList.add(anser1);
					Answeroptions anser2=new Answeroptions();
					anser2.setAnswerId("B");
					anser2.setAnswerIds("N");
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
			logger.error("xfStudyAnswer出错="+ s.toString(),e);
			throw e;
		}
		return list;
	}

	
	
	/**
	 *消分学习答题： 接口exam002
	 * @throws Exception 
	 */
	@Override
	public List<BaseBean> xfAnswerQuey(Study s) throws Exception {
		List<BaseBean>list=new ArrayList<>();
		BaseBean base=new BaseBean();
		List<Study>studyList=new ArrayList<>();		
		try {
		String xx="<?xml version=\"1.0\" encoding=\"utf-8\"?><request><head><id>"+s.getSubjectId()+"</id><true_name>"+s.getUserName()+"</true_name>"
				+ "<sfzmhm>"+s.getIdentityCard()+"</sfzmhm><sjhm>"+s.getMobilephone()+"</sjhm><ip>"+s.getIpAddress()+"</ip><dt_qd>"+s.getUserSource()+"</dt_qd>"
				+ "<dt_sj>"+s.getAnswerDateTime()+"</dt_sj><dt_da>"+s.getSubjectAnswer()+"</dt_da><jfzq_start>"+s.getScoreStartDate()+"</jfzq_start><jfzq_end>"+s.getScoreEndDate()+"</jfzq_end></head></request>";
		
			String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(), iMicroclassCached.getMethod(), 
					interfaceNumber,xx,iMicroclassCached.getUserid(),iMicroclassCached.getUserpwd(),iMicroclassCached.getKey());
			logger.info(respStr.toJSONString());
			JSONObject body=respStr.getJSONObject("body");
			JSONObject head=respStr.getJSONObject("head");
			base.setCode(head.get("fhz").toString());
			base.setMsg(head.get("fhz-msg").toString());
			Object dcts=head.get("dcts"); //答错题数
			if(dcts!=null&&!dcts.equals("")){
				s.setAnswererror(Integer.valueOf(head.get("dcts").toString()));  //答题错误题数
			}
			Object ddts=head.get("ddts"); //答对题数
			if(ddts!=null&&!ddts.equals("")){
				s.setAnswerCorrect(Integer.valueOf(head.get("ddts").toString())); //答对题数
			}
			Object pcjg=head.get("pcjg");
			if(pcjg!=null&&!pcjg.equals("")){
			    s.setBatchResult(null!=head.get("pcjg")?head.get("pcjg").toString():"");	
			}
			Object syts=head.get("syts");
			if(syts!=null&&!syts.equals("")){
				s.setSurplusAnswe(Integer.valueOf(head.get("syts").toString())); //剩余题数	
			}
			s.setAnswerDate(s.getAnswerDate());  //答题时间 格式YYYY-MM-DD
			studyList.add(s);
			base.setData(studyList);
			list.add(base);
		} catch (Exception e) {
			logger.error("xfAnswerQuey方法出错="+ s.toString(),e);
			throw e;
			
		}
		return list;
	}


	/**
	 * 满分 学习查询  、AB类驾驶人学习查询、 电动车学习查询、行人非机动车安全学习查询 方法
	 * @throws Exception 
	 */
	@Override
	public List<BaseBean> xrStudyQuery(Study s) throws Exception {
		
		List<BaseBean>list=new ArrayList<>();
		BaseBean base=new BaseBean();
		List<Study>studyList=new ArrayList<>();
		List<StudyRecord>studyrecordList=new ArrayList<>();
		try {
		String xfxx="";
		if(s.getClassroomId().equals("1")||s.getClassroomId().equals("2")||s.getClassroomId().equals("3")){ 
			xfxx="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request><head><sfzmhm>"+s.getIdentityCard()+"</sfzmhm>"
					+ "<sjhm>"+s.getMobilephone()+"</sjhm><ip>"+s.getIpAddress()+"</ip><yhly>"+s.getUserSource()+"</yhly></head></request>";
			
		}else{
			 xfxx="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request><head><sfzmhm>"+s.getIdentityCard()+"</sfzmhm>"
					+ "<sjhm>"+s.getMobilephone()+"</sjhm><ywlx>"+s.getServiceType()+"</ywlx><ip>"+s.getIpAddress()+"</ip><yhly>"+s.getUserSource()+"</yhly></head></request>";
		}
			String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(), iMicroclassCached.getMethod(), 
			interfaceNumber,xfxx,iMicroclassCached.getUserid(),iMicroclassCached.getUserpwd(),iMicroclassCached.getKey());
			logger.info(respStr.toJSONString());
			JSONObject body=respStr.getJSONObject("body"); //获取json中body
			JSONObject head=respStr.getJSONObject("head"); //获取json中head
			base.setCode(head.get("fhz").toString());
			if(base.getCode().equals("0000")){  //返回0000代表成功 
			Object jfzq_start=body.get("jfzq_start"); //
			if(jfzq_start!=null){
				s.setScoreStartDate(body.get("jfzq_start").toString()); //记分周期开始日期
			}
			Object jfzq_end=body.get("jfzq_end"); 
			if(jfzq_end!=null){
				s.setScoreEndDate(body.get("jfzq_end").toString());//记分周期结束日期	
			}
						
			String xString=body.toString();
			
			if(StringUtils.isNotBlank(xString)){
				if(xString.contains("[")){
					//多条学习记录
					JSONArray examlist= body.getJSONArray("examlist");  //从json中body获取examlist这个集合
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
				}else if(!xString.contains("examlist")){
					base.setMsg("没有学习记录");
					logger.info("没有学习记录");
				}else{
					//String isexamlist=(String)body.get("examlist");
					//if(isexamlist!=null&&!isexamlist.equals("")){
					//单挑学习记录
					JSONObject examlists=(JSONObject) body.get("examlist");
					//examlists.get("dtrq");
					StudyRecord studyrecord=new StudyRecord();
					//studyrecord.setAnsLogarithm(Integer.valueOf(null!=examlists.get("ddts")?examlists.get("ddts").toString():"0"));
					studyrecord.setAnswerDate(null !=examlists.get("dtrq")?examlists.get("dtrq").toString():"");	
					studyrecord.setAnswerBatch(null !=examlists.get("dtpc")?examlists.get("dtpc").toString():"");
					studyrecord.setIsComplete(null != examlists.get("dtjg")?examlists.get("dtjg").toString():"");
					studyrecord.setAnsLogarithm(Integer.valueOf(examlists.get("ddts").toString()));
					studyrecordList.add(studyrecord);
					s.setStudyRecord(studyrecordList);
					//}	
				}
			}
				s.setIdentityCard(body.get("jszhm").toString());
				s.setUserName(body.getString("true_name").toString());
				base.setMsg(head.get("fhz-msg").toString());	
			}else{
				base.setMsg(head.get("fhz").toString());
				base.setMsg(head.get("fhz-msg").toString());
			}
			studyList.add(s);
			base.setData(studyList);
			list.add(base);
		} catch (Exception e) {
			logger.error("xrStudyQuery方法出错"+ s.toString(),e);
			throw e;
		}
		return list;
	}
	
	/**
	 * 6.34.3	行人、非机动车驾驶人道路交通安全学习随机取题
	 * @throws Exception 
	 */
	@Override
	public List<BaseBean> xrStudyAnswer(Study s) throws Exception {
		List<BaseBean>list=new ArrayList<>();
		BaseBean base=new BaseBean();
		List<Study>studyList=new ArrayList<>();
		List<Answeroptions>anserList=new ArrayList<>();
		try {
		String xml="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<request><head><sfzmhm>"+s.getIdentityCard()+"</sfzmhm>"
				+ "<sjhm>"+s.getMobilephone()+"</sjhm><ywlx>"+s.getServiceType()+"</ywlx><ip>"+s.getIpAddress()+"</ip><yhly>"+s.getUserSource()+"</yhly></head></request>";
		
		String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
				
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(), iMicroclassCached.getMethod(), 
					interfaceNumber,xml,iMicroclassCached.getUserid(),iMicroclassCached.getUserpwd(),iMicroclassCached.getKey());
			logger.info(respStr.toString());
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
					anser1.setAnswerIds("Y");
					anser1.setAnswerName("正确");
					anserList.add(anser1);
					Answeroptions anser2=new Answeroptions();
					anser2.setAnswerId("B");
					anser2.setAnswerName("错误");
					anser2.setAnswerIds("N");
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
				base.setMsg(head.get("fhz-msg").toString());	
			}else{
				base.setMsg(head.get("fhz-msg").toString());
			}
			studyList.add(s);
			base.setData(studyList);
			list.add(base);
		} catch (Exception e) {
			logger.error("xrStudyAnswer方法出错"+ s.toString(),e);
			throw e;
		}
		return list;
	}
	
	/**
	 * 6.34.3	行人、非机动车驾驶人道路交通安全学习答题方法
	 * @throws Exception 
	 */
	@Override
	public List<BaseBean> xrAnswerQuey(Study s) throws Exception {
		List<BaseBean>list=new ArrayList<>();
		BaseBean base=new BaseBean();
		List<Study>studyList=new ArrayList<>();
		try {
		String xml="<?xml version=\"1.0\" encoding=\"utf-8\"?><request><head><id>"+s.getSubjectId()+"</id>"
				+ "<sfzmhm>"+s.getIdentityCard()+"</sfzmhm><sjhm>"+s.getMobilephone()+"</sjhm><ywlx>AQ</ywlx><ip>"+s.getIpAddress()+"</ip><dt_qd>"+s.getUserSource()+"</dt_qd>"
				+ "<dt_da>"+s.getSubjectAnswer()+"</dt_da></head></request>";
		String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
				
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(), iMicroclassCached.getMethod(), 
					interfaceNumber,xml,iMicroclassCached.getUserid(),iMicroclassCached.getUserpwd(),iMicroclassCached.getKey());
			logger.info(respStr.toJSONString());
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
			logger.error("xrAnswerQuey方法出错"+ s.toString(),e);
			throw e;
		}
		
		return list;
	}

	
	/**
	 * 电动车违法随机取题
	 * @throws Exception 
	 */
	
	@Override
	public List<BaseBean> ddcStudyAnswer(Study s) throws Exception {
		List<BaseBean>list=new ArrayList<>();
		BaseBean base=new BaseBean();
		List<Study>studyList=new ArrayList<>();
		List<StudyRecord>studyrecordList=new ArrayList<>();
		List<Answeroptions>anserList=new ArrayList<>();
		try {
		String xml="<?xml version=\"1.0\" encoding=\"utf-8\"?><request><head><sfzmhm>"+s.getIdentityCard()+"</sfzmhm><sjhm>"+s.getMobilephone()+"</sjhm>"
				+ "<ywlx>"+s.getServiceType()+"</ywlx><jdsbh>"+s.getDecisionId()+"</jdsbh><ip>"+s.getIpAddress()+"</ip><yhly>"+s.getUserSource()+"</yhly></head></request>";
		
			String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(), iMicroclassCached.getMethod(), 
					interfaceNumber,xml,iMicroclassCached.getUserid(),iMicroclassCached.getUserpwd(),iMicroclassCached.getKey());
			logger.info(respStr.toJSONString());
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
					anser1.setAnswerIds("Y");
					anserList.add(anser1);
					Answeroptions anser2=new Answeroptions();
					anser2.setAnswerId("B");
					anser2.setAnswerName("错误");
					anser2.setAnswerIds("N");
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
				base.setMsg(head.get("fhz-msg").toString());	
			}else{
				base.setMsg(head.get("fhz-msg").toString());
			}
			studyList.add(s);
			base.setData(studyList);
			list.add(base);
		} catch (Exception e) {
			logger.error("ddcStudyAnswer方法出错"+ s.toString(),e);
			throw e;
		}	
		return list;
	}

	List<Study>jgList=null;
	/**
	 * 电动车违法答题方法
	 * @throws Exception 
	 */
	@Override
	public List<BaseBean> ddcAnswerQuey(Study s) throws Exception {
	
		List<BaseBean>list=new ArrayList<>();
		BaseBean base=new BaseBean();
		List<Study>studyList=new ArrayList<>();
		try {	
		String xml="<?xml version=\"1.0\" encoding=\"utf-8\"?><request><head><id>"+s.getSubjectId()+"</id><sfzmhm>"+s.getIdentityCard()+"</sfzmhm><sjhm>"+s.getMobilephone()+"</sjhm>"
				+ "<ywlx>"+s.getServiceType()+"</ywlx><jdsbh>"+s.getDecisionId()+"</jdsbh><ip>"+s.getIpAddress()+"</ip><dt_qd>C</dt_qd><yhly>"+s.getUserSource()+"</yhly><dt_da>"+s.getSubjectAnswer()+"</dt_da></head></request>";
		
		String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
			
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(), iMicroclassCached.getMethod(), 
					interfaceNumber,xml,iMicroclassCached.getUserid(),iMicroclassCached.getUserpwd(),iMicroclassCached.getKey());
			logger.info(respStr.toJSONString());
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
				s.setServiceType(head.get("ywlx").toString()); //业务类型
				s.setTrainResult(null !=head.get("pxjg")?head.get("pxjg").toString():"");  //接收培训结果
			}				
			studyList.add(s);
			base.setData(studyList);
			list.add(base);
		} catch (Exception e) {
			logger.error("ddcAnswerQuey方法出错"+ s.toString(),e);
			throw e;
		}
		return list;
	}
	




	



}