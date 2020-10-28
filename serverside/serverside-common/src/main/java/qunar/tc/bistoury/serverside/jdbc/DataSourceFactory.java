package qunar.tc.bistoury.serverside.jdbc;

import qunar.tc.bistoury.serverside.configuration.DynamicConfig;

import javax.sql.DataSource;

public interface DataSourceFactory {

	DataSource createDataSource(DynamicConfig dynamicConfig);

}
