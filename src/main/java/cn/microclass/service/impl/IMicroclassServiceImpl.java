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

import cn.microclass.bean.AppVersion;
import cn.microclass.bean.WechatUserInfoBean;
import cn.microclass.bean.studyclassroom.Answeroptions;
import cn.microclass.bean.studyclassroom.Study;
import cn.microclass.bean.studyclassroom.StudyRecord;
import cn.microclass.cached.impl.IMicroclassCachedImpl;
import cn.microclass.dao.IAppVersionDao;
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
	
	@Autowired
	private IAppVersionDao appVersionDao;
	
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
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(s.getUserSource()), iMicroclassCached.getMethod(s.getUserSource()), 
					interfaceNumber,xml,iMicroclassCached.getUserid(s.getUserSource()),iMicroclassCached.getUserpwd(s.getUserSource()),iMicroclassCached.getKey(s.getUserSource()));
			logger.info(respStr.toJSONString());
			JSONObject body=respStr.getJSONObject("body");
			JSONObject head=respStr.getJSONObject("head");
			base.setCode(head.get("fhz").toString());
			if("0000".equals(base.getCode())){  //返回0000代表成功 
				s.setAnswerCorrect(Integer.valueOf(body.getString("ddts"))); //答题题数
				s.setScoreStartDate(body.getString("jfzq_start"));
				s.setScoreEndDate(body.getString("jfzq_end"));
				s.setIntegral(body.getString("ljjf")); //累计积分
				s.setUserName(body.getString("true_name"));
				base.setMsg(head.getString("fhz-msg"));	
			}else{
				base.setMsg(head.getString("fhz-msg"));
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
		if("1".equals(s.getClassroomId())||"2".equals(s.getClassroomId())||"3".equals(s.getClassroomId())){
//		if(s.getClassroomId().equals("1")||s.getClassroomId().equals("2")||s.getClassroomId().equals("3")){
			
		 xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><request><head><sfzmhm>"+s.getIdentityCard()+"</sfzmhm>"
				+ "<sjhm>"+s.getMobilephone()+"</sjhm><ip>"+s.getIpAddress()+"</ip><yhly>"+s.getUserSource()+"</yhly></head></request>";
		}else{
		 xml="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request><head><sfzmhm>"+s.getIdentityCard()+"</sfzmhm>"
					+ "<sjhm>"+s.getMobilephone()+"</sjhm><ywlx>"+s.getServiceType()+"</ywlx><ip>"+s.getIpAddress()+"</ip><yhly>"+s.getUserSource()+"</yhly></head></request>";
			
		}

		String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
				
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(s.getUserSource()), iMicroclassCached.getMethod(s.getUserSource()), 
					interfaceNumber,xml,iMicroclassCached.getUserid(s.getUserSource()),iMicroclassCached.getUserpwd(s.getUserSource()),iMicroclassCached.getKey(s.getUserSource()));
			logger.info(respStr.toJSONString());
			
			JSONObject body=respStr.getJSONObject("body");
			JSONObject head=respStr.getJSONObject("head");
			base.setCode(head.getString("fhz"));
			//s.setCode(body.get("xx_a").toString());
			if("0000".equals(base.getCode())){  //返回0000代表成功 
				s.setSubjectId(body.getString("id"));
				s.setScoreStartDate(body.getString("jfzq_start"));
				s.setScoreEndDate(body.getString("jfzq_end"));
				s.setAnswerDateTime(body.getString("qt_sj"));
				s.setTestQuestionsType(body.getString("qt_stlx"));
				s.setSubjectName(body.getString("qt_tm"));
				s.setUserName(body.getString("true_name"));
				s.setIdentityCard(body.getString("jszhm"));
				s.setTestQuestionsType(null !=body.getString("qt_stlx")?body.getString("qt_stlx"):"");  //图片
				s.setSubjectImg(null !=body.getString("qt_tp")?body.getString("qt_tp"):"");
				if("判断题".equals(body.getString("qt_stlx"))){
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
						anser1.setAnswerName(null!=body.get("xx_a")?body.getString("xx_a"):"");
						anserList.add(anser1);
					}
					if(answerB!=null){
						Answeroptions anser2=new Answeroptions();
						anser2.setAnswerId("B");
						anser2.setAnswerName(null!=body.get("xx_b")?body.getString("xx_b"):"");
						anserList.add(anser2);
					}
					if(answerC!=null){
						Answeroptions anser3=new Answeroptions();
						anser3.setAnswerId("C");
						anser3.setAnswerName(null!=body.getString("xx_c")?body.getString("xx_c"):"");
						anserList.add(anser3);
					}
					if(answerD!=null){
						Answeroptions anser4=new Answeroptions();
						anser4.setAnswerId("D");
						anser4.setAnswerName(null!=body.getString("xx_d")?body.getString("xx_d"):"");
						anserList.add(anser4);
					}	
				}
				s.setAnsweroptions(anserList);
				base.setMsg(head.getString("fhz-msg"));	
			}else if("0001".equals(base.getCode())){  //当返回0001的时候代表今日已经答了10题了 不能继续答题了！
				s.setAnswerState(0);
				base.setMsg(head.getString("fhz-msg"));
			}else{
				base.setMsg(head.getString("fhz-msg"));
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
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(s.getUserSource()), iMicroclassCached.getMethod(s.getUserSource()), 
					interfaceNumber,xx,iMicroclassCached.getUserid(s.getUserSource()),iMicroclassCached.getUserpwd(s.getUserSource()),iMicroclassCached.getKey(s.getUserSource()));
			logger.info(respStr.toJSONString());
			JSONObject body=respStr.getJSONObject("body");
			JSONObject head=respStr.getJSONObject("head");
			base.setCode(head.getString("fhz"));
			base.setMsg(head.getString("fhz-msg"));
			Object dcts=head.getString("dcts"); //答错题数
			if(dcts!=null&&!dcts.equals("")){
				s.setAnswererror(Integer.valueOf(head.getString("dcts")));  //答题错误题数
			}
			Object ddts=head.get("ddts"); //答对题数
			if(ddts!=null&&!ddts.equals("")){
				s.setAnswerCorrect(Integer.valueOf(head.getString("ddts"))); //答对题数
			}
			Object pcjg=head.get("pcjg");
			if(pcjg!=null&&!pcjg.equals("")){
			    s.setBatchResult(null!=head.getString("pcjg")?head.getString("pcjg"):"");	
			}
			Object syts=head.get("syts");
			if(syts!=null&&!syts.equals("")){
				s.setSurplusAnswe(Integer.valueOf(head.getString("syts"))); //剩余题数	
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
//		if(s.getClassroomId().equals("1")||s.getClassroomId().equals("2")||s.getClassroomId().equals("3")){
		if("1".equals(s.getClassroomId())||"2".equals(s.getClassroomId())||"3".equals(s.getClassroomId())){
			xfxx="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request><head><sfzmhm>"+s.getIdentityCard()+"</sfzmhm>"
					+ "<sjhm>"+s.getMobilephone()+"</sjhm><ip>"+s.getIpAddress()+"</ip><yhly>"+s.getUserSource()+"</yhly></head></request>";
			
		}else{
			 xfxx="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<request><head><sfzmhm>"+s.getIdentityCard()+"</sfzmhm>"
					+ "<sjhm>"+s.getMobilephone()+"</sjhm><ywlx>"+s.getServiceType()+"</ywlx><ip>"+s.getIpAddress()+"</ip><yhly>"+s.getUserSource()+"</yhly></head></request>";
		}
			String interfaceNumber = s.getInterfaceId();  //获取取题接口编号
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(s.getUserSource()), iMicroclassCached.getMethod(s.getUserSource()), 
			interfaceNumber,xfxx,iMicroclassCached.getUserid(s.getUserSource()),iMicroclassCached.getUserpwd(s.getUserSource()),iMicroclassCached.getKey(s.getUserSource()));
			logger.info(respStr.toJSONString());
			JSONObject body=respStr.getJSONObject("body"); //获取json中body
			JSONObject head=respStr.getJSONObject("head"); //获取json中head
			base.setCode(head.get("fhz").toString());
			if("0000".equals(base.getCode())){  //返回0000代表成功 
			Object jfzq_start=body.get("jfzq_start"); //
			if(jfzq_start!=null){
				s.setScoreStartDate(body.getString("jfzq_start")); //记分周期开始日期
			}
			Object jfzq_end=body.get("jfzq_end"); 
			if(jfzq_end!=null){
				s.setScoreEndDate(body.getString("jfzq_end"));//记分周期结束日期	
			}
						
			String xString=body.toString();
			
			if(StringUtils.isNotBlank(xString)){
				if(xString.contains("[")){
					//多条学习记录
					JSONArray examlist= body.getJSONArray("examlist");  //从json中body获取examlist这个集合
					for(int i=0;i<examlist.size();i++){
						StudyRecord studyrecord=new StudyRecord();
						JSONObject ob= examlist.getJSONObject(i);
						studyrecord.setAnsLogarithm(Integer.valueOf(ob.getString("ddts")));
						studyrecord.setAnswerDate(ob.getString("dtrq"));	
						studyrecord.setAnswerBatch(ob.getString("dtpc"));
						studyrecord.setIsComplete(null != ob.getString("dtjg")?ob.getString("dtjg"):"");	
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
					studyrecord.setAnswerDate(null !=examlists.getString("dtrq")?examlists.getString("dtrq"):"");	
					studyrecord.setAnswerBatch(null !=examlists.getString("dtpc")?examlists.getString("dtpc"):"");
					studyrecord.setIsComplete(null != examlists.getString("dtjg")?examlists.getString("dtjg"):"");
					studyrecord.setAnsLogarithm(Integer.valueOf(examlists.getString("ddts")));
					studyrecordList.add(studyrecord);
					s.setStudyRecord(studyrecordList);
					//}	
				}
			}
				s.setIdentityCard(body.getString("jszhm"));
				s.setUserName(body.getString("true_name"));
				base.setMsg(head.getString("fhz-msg"));	
			}else{
				base.setMsg(head.getString("fhz"));
				base.setMsg(head.getString("fhz-msg"));
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
				
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(s.getUserSource()), iMicroclassCached.getMethod(s.getUserSource()), 
					interfaceNumber,xml,iMicroclassCached.getUserid(s.getUserSource()),iMicroclassCached.getUserpwd(s.getUserSource()),iMicroclassCached.getKey(s.getUserSource()));
			logger.info(respStr.toString());
			JSONObject body=respStr.getJSONObject("body");
			JSONObject head=respStr.getJSONObject("head");
			base.setCode(head.getString("fhz"));
			//s.setCode(body.get("xx_a").toString());
			if("0000".equals(base.getCode())){  //返回0000代表成功 
				s.setSubjectId(body.getString("id"));
				s.setIdentityCard(body.getString("jszhm"));
				s.setAnswerDateTime(body.getString("qt_sj"));
				s.setTestQuestionsType(body.getString("qt_stlx"));
				s.setSubjectName(body.getString("qt_tm"));
				if(body.getString("qt_tmlb").equals("文字题")){
					s.setSubjecttype(1);
				}else{
					s.setSubjecttype(2);
				}
				s.setSubjectImg(null !=body.get("qt_tp")?body.get("qt_tp").toString():""); //取题图片
				s.setUserName(body.getString("true_name"));
				if(body.getString("qt_stlx").equals("判断题")){
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
						anser1.setAnswerName(null!=body.getString("xx_a")?body.getString("xx_a"):"");
						anserList.add(anser1);
					}
					if(answerB!=null){
						Answeroptions anser2=new Answeroptions();
						anser2.setAnswerId("B");
						anser2.setAnswerName(null!=body.getString("xx_b")?body.getString("xx_b"):"");
						anserList.add(anser2);
					}
					if(answerC!=null){
						Answeroptions anser3=new Answeroptions();
						anser3.setAnswerId("C");
						anser3.setAnswerName(null!=body.getString("xx_c")?body.getString("xx_c"):"");
						anserList.add(anser3);
					}
					if(answerD!=null){
						Answeroptions anser4=new Answeroptions();
						anser4.setAnswerId("D");
						anser4.setAnswerName(null!=body.getString("xx_d")?body.getString("xx_d"):"");
						anserList.add(anser4);
					}	
				}
				s.setAnsweroptions(anserList);
				s.setServiceType(body.getString("ywlx"));
				base.setMsg(head.getString("fhz-msg"));	
			}else{
				base.setMsg(head.getString("fhz-msg"));
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
				
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(s.getUserSource()), iMicroclassCached.getMethod(s.getUserSource()), 
					interfaceNumber,xml,iMicroclassCached.getUserid(s.getUserSource()),iMicroclassCached.getUserpwd(s.getUserSource()),iMicroclassCached.getKey(s.getUserSource()));
			logger.info(respStr.toJSONString());
			JSONObject body=respStr.getJSONObject("body");
			JSONObject head=respStr.getJSONObject("head");
			if(head.get("fhz").toString().equals("0002")){
				base.setCode(head.getString("fhz"));
				base.setMsg(head.getString("fhz-msg"));   //返回答题错误  或者答题正确
			}else{
				s.setAnswererror(Integer.valueOf(head.getString("dcts")));  //答题错误题数
				s.setAnswerCorrect(Integer.valueOf(head.getString("ddts")));  //答对题数
				base.setCode(head.getString("fhz"));
				base.setMsg(head.getString("fhz-msg"));   //返回答题错误  或者答题正确
				s.setBatchResult(head.getString("pcjg"));
				s.setSurplusAnswe(Integer.valueOf(head.getString("syts"))); //剩余题数
				s.setServiceType(head.getString("ywlx")); //业务类型
				s.setTrainResult(null !=head.getString("pxjg")?head.getString("pxjg"):"");  //接收培训结果
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
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(s.getUserSource()), iMicroclassCached.getMethod(s.getUserSource()), 
					interfaceNumber,xml,iMicroclassCached.getUserid(s.getUserSource()),iMicroclassCached.getUserpwd(s.getUserSource()),iMicroclassCached.getKey(s.getUserSource()));
			logger.info(respStr.toJSONString());
			JSONObject body=respStr.getJSONObject("body");
			JSONObject head=respStr.getJSONObject("head");
			base.setCode(head.getString("fhz"));
			//s.setCode(body.get("xx_a").toString());
			if("0000".equals(base.getCode())){  //返回0000代表成功 
				s.setSubjectId(body.getString("id"));
				s.setDecisionId(null !=body.getString("jdsbh")?body.getString("jdsbh"):""); //决定书编号
				s.setIdentityCard(body.getString("jszhm"));
				s.setAnswerDateTime(body.getString("qt_sj"));
				s.setTestQuestionsType(body.getString("qt_stlx"));  //选择题或者判断题
				s.setSubjectName(body.getString("qt_tm"));
				s.setSubjectImg(null !=body.getString("qt_tp")?body.getString("qt_tp"):""); //取题图片
				
				if("判断题".equals(body.getString("qt_stlx"))){
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
						anser1.setAnswerName(null!=body.getString("xx_a")?body.getString("xx_a"):"");
						anserList.add(anser1);
					}
					if(answerB!=null){
						Answeroptions anser2=new Answeroptions();
						anser2.setAnswerId("B");
						anser2.setAnswerName(null!=body.getString("xx_b")?body.getString("xx_b"):"");
						anserList.add(anser2);
					}
					if(answerC!=null){
						Answeroptions anser3=new Answeroptions();
						anser3.setAnswerId("C");
						anser3.setAnswerName(null!=body.getString("xx_c")?body.getString("xx_c"):"");
						anserList.add(anser3);
					}
					if(answerD!=null){
						Answeroptions anser4=new Answeroptions();
						anser4.setAnswerId("D");
						anser4.setAnswerName(null!=body.getString("xx_d")?body.getString("xx_d"):"");
						anserList.add(anser4);
					}	
				}
				s.setAnsweroptions(anserList);
				s.setServiceType(body.getString("ywlx"));
				base.setMsg(head.getString("fhz-msg"));	
			}else{
				base.setMsg(head.getString("fhz-msg"));
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
			
			JSONObject respStr =WebServiceClient.getInstance().requestWebService(iMicroclassCached.getUrl(s.getUserSource()), iMicroclassCached.getMethod(s.getUserSource()), 
					interfaceNumber,xml,iMicroclassCached.getUserid(s.getUserSource()),iMicroclassCached.getUserpwd(s.getUserSource()),iMicroclassCached.getKey(s.getUserSource()));
			logger.info(respStr.toJSONString());
			JSONObject body=respStr.getJSONObject("body");
			JSONObject head=respStr.getJSONObject("head");
			if("0002".equals(head.getString("fhz"))){
				base.setCode(head.getString("fhz"));
				base.setMsg(head.getString("fhz-msg"));   //返回答题错误  或者答题正确
			}else{
				s.setAnswererror(Integer.valueOf(head.getString("dcts")));  //答题错误题数
				s.setAnswerCorrect(Integer.valueOf(head.getString("ddts")));  //答对题数
				base.setCode(head.getString("fhz"));
				base.setMsg(head.getString("fhz-msg"));   //返回答题错误  或者答题正确
				s.setBatchResult(head.getString("pcjg"));
				s.setSurplusAnswe(Integer.valueOf(head.getString("syts"))); //剩余题数
				s.setAnswerDate(s.getAnswerDate());  //答题时间 格式YYYY-MM-DD
				s.setServiceType(head.getString("ywlx")); //业务类型
				s.setTrainResult(null !=head.getString("pxjg")?head.getString("pxjg"):"");  //接收培训结果
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

	@Override
	public AppVersion queryNewestAppVersion(String system) {
		AppVersion appVersion = null;
		try {
			appVersion = iMicroclassCached.getAppVersion(system);
			if(appVersion == null){
				logger.info("【app】从数据库中获取app版本，参数system = " + system);
				appVersion = appVersionDao.queryNewestAppVersion(system);
				iMicroclassCached.setAppVersion(system, appVersion);
			}
			logger.info("【app】获取app版本结果，appVersion = " + appVersion);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return appVersion;
	}
	




	



}