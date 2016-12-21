package com.sogou.util;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DaoUtil {
	public static final JdbcTemplate online_master;

	static final String driver = "com.mysql.jdbc.Driver";

	static {
		try {
			Properties prop = getConfig();
			DataSource masterSource = getOnlineMaster(prop);
			online_master = new ModifiedJdbcTemplate(masterSource);

		} catch (Exception e) {
			throw new IllegalStateException("init dbpool failed.", e);
		}
	}

	// 修改SimpleJcbcTemplate的默认行为，queryForObject无结果时返回null
	static class ModifiedJdbcTemplate extends JdbcTemplate {
		public ModifiedJdbcTemplate(DataSource dataSource) {
			super(dataSource);
		}

		public <T> T queryForObject(String sql, RowMapper<T> rm, Object... args) throws DataAccessException {
			List<T> results = query(sql, rm, args);
			int size = results.size();
			if (size == 1) return results.get(0);
			if (size == 0) return null;
			throw new IncorrectResultSizeDataAccessException(1, size);
		}
	}

	private static Properties getConfig() throws IOException {
		Properties prop = new Properties();
		prop.load(DaoUtil.class.getClassLoader().getResourceAsStream("jdbc.properties"));
		return prop;
	}

	private static DataSource getOnlineMaster(Properties prop) throws PropertyVetoException {
		ComboPooledDataSource online_master = new ComboPooledDataSource();
		online_master.setDriverClass(driver);
		online_master.setJdbcUrl(prop.getProperty("jdbc_online_master.url"));
		online_master.setUser(prop.getProperty("jdbc_online_master.user"));
		online_master.setPassword(prop.getProperty("jdbc_online_master.password"));
		online_master.setAutoCommitOnClose(true);
		online_master.setMinPoolSize(Integer.valueOf(prop.getProperty("jdbc_online_master.min.poolsize")));
		online_master.setMaxPoolSize(Integer.valueOf(prop.getProperty("jdbc_online_master.max.poolsize")));
		online_master.setInitialPoolSize(Integer.valueOf(prop.getProperty("jdbc_online_master.init.poolsize")));
		online_master.setPreferredTestQuery("select 1");
		online_master.setAcquireIncrement(5);
		online_master.setMaxStatements(100);
		online_master.setMaxStatementsPerConnection(100);
		online_master.setNumHelperThreads(10);
		online_master.setMaxIdleTime(600);
		online_master.setIdleConnectionTestPeriod(10);
		online_master.setAcquireRetryDelay(1000);
		return online_master;
	}

}
