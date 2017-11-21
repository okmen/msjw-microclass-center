package cn.microclass.cached;

import java.util.List;

import cn.microclass.bean.AppVersion;
import cn.microclass.bean.studyclassroom.Study;

public interface IMicroclassCached extends ICacheKey {

	
	public List<Study> findUser(Study study);
	
	
	public void setAppVersion(String system, AppVersion appVersion);
	
	public AppVersion getAppVersion(String system);
}
