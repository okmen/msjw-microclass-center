package cn.microclass.dao.impl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.microclass.bean.AppVersion;
import cn.microclass.dao.IAppVersionDao;
import cn.microclass.dao.mapper.AppVersionMapper;

@Repository
public class IAppVersionDaoImpl implements IAppVersionDao {

	protected Logger log = Logger.getLogger(this.getClass());
	@Autowired
	private AppVersionMapper appVersionMapper;
	
	@Override
	public AppVersion queryNewestAppVersion(String system) {
		return appVersionMapper.queryNewestAppVersion(system);
	}
	
}
