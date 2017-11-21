package cn.microclass.dao.mapper;

import org.springframework.stereotype.Repository;

import cn.microclass.bean.AppVersion;


@Repository
public interface AppVersionMapper {
	
	/**
	 * 根据app系统获取对应最新版本
	 * @param system 系统
	 * @return
	 */
    public AppVersion queryNewestAppVersion(String system);
    
}
