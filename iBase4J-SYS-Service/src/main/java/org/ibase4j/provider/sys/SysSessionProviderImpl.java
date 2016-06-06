package org.ibase4j.provider.sys;

import java.util.List;
import java.util.Map;

import org.ibase4j.core.support.dubbo.BaseProviderImpl;
import org.ibase4j.core.support.dubbo.spring.annotation.DubboService;
import org.ibase4j.mybatis.generator.dao.SysSessionMapper;
import org.ibase4j.mybatis.generator.model.SysSession;
import org.ibase4j.mybatis.sys.dao.SysSessionExpandMapper;
import org.ibase4j.provider.sys.SysSessionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import com.github.pagehelper.PageInfo;

/**
 * @author ShenHuaJie
 * @version 2016年5月20日 下午3:19:19
 */
@CacheConfig(cacheNames = "sysSession")
@DubboService(interfaceClass = SysSessionProvider.class)
public class SysSessionProviderImpl extends BaseProviderImpl<SysSession> implements SysSessionProvider {
	@Autowired
	private SysSessionMapper sessionMapper;
	@Autowired
	private SysSessionExpandMapper sessionExpandMapper;

	@CachePut
	public SysSession update(SysSession record) {
		if (record.getId() == null) {
			Integer id = sessionExpandMapper.queryBySessionId(record.getSessionId());
			if (id != null) {
				record.setId(id);
				sessionMapper.updateByPrimaryKey(record);
			} else {
				sessionMapper.insert(record);
			}
		} else {
			sessionMapper.updateByPrimaryKey(record);
		}
		return record;
	}

	@CacheEvict
	public void delete(Integer id) {
		sessionMapper.deleteByPrimaryKey(id);
	}

	// 系统触发,由系统自动管理缓存
	public void deleteBySessionId(final String sessionId) {
		sessionExpandMapper.deleteBySessionId(sessionId);
	}

	@Cacheable
	public SysSession queryById(Integer id) {
		return sessionMapper.selectByPrimaryKey(id);
	}

	public PageInfo<SysSession> query(Map<String, Object> params) {
		this.startPage(params);
		return getPage(sessionExpandMapper.query(params));
	}

	public List<String> querySessionIdByAccount(String account) {
		return sessionExpandMapper.querySessionIdByAccount(account);
	}
}
