package cn.microclass.dao;
import cn.microclass.bean.AppVersion;

public interface IAppVersionDao {
	
	/**
	 * 根据app系统获取对应最新版本
	 * @param system 系统
	 * @return
	 */
    public AppVersion queryNewestAppVersion(String system);

}
