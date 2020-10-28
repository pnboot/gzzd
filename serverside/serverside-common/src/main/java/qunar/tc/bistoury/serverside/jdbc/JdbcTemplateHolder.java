package qunar.tc.bistoury.serverside.jdbc;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

/**
 * @author xkrivzooh
 * @since 2019/8/15
 */
public class JdbcTemplateHolder {

    private static final String DEFAULT_DATASOURCE_CONF = "jdbc.properties";

    private static final Supplier<DataSource> DS_SUPPLIER = Suppliers.memoize(JdbcTemplateHolder::createDataSource);

    private static final Supplier<JdbcTemplate> JDBC_TEMPLATE_SUPPLIER = Suppliers.memoize(JdbcTemplateHolder::createJdbcTemplate);

    private static final Supplier<NamedParameterJdbcTemplate> NAMED_PARAMETER_JDBC_TEMPLATE_SUPPLIER =
            Suppliers.memoize(JdbcTemplateHolder::createNamedParameterJdbcTemplate);

    private static JdbcTemplate createJdbcTemplate() {
        return new JdbcTemplate(DS_SUPPLIER.get());
    }

    private static NamedParameterJdbcTemplate createNamedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(DS_SUPPLIER.get());
    }

    private static DataSource createDataSource() {
//        final DynamicConfig config = DynamicConfigLoader.load(DEFAULT_DATASOURCE_CONF);
//        ServiceLoader<DataSourceFactory> factories = ServiceLoader.load(DataSourceFactory.class);
//        for (DataSourceFactory factory : factories) {
//            return factory.createDataSource(config);
//        }

//        return new DefaultDataSourceFactory().createDataSource(config);
        return null;
    }

    public static JdbcTemplate getOrCreateJdbcTemplate() {
        return JDBC_TEMPLATE_SUPPLIER.get();
    }

    public static NamedParameterJdbcTemplate getOrCreateNamedParameterJdbcTemplate() {
        return NAMED_PARAMETER_JDBC_TEMPLATE_SUPPLIER.get();
    }

    public static DataSource getOrCreateDataSource() {
        return DS_SUPPLIER.get();
    }

}
