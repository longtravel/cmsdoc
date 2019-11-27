package ots.cmsdoc.application.datasources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ots.cmsdoc.application.util.CmsProperties;

import javax.sql.DataSource;

@Configuration
public class DataSourceBean {

    private CmsProperties props;

    @Autowired
    public DataSourceBean(CmsProperties props) {
        this.props=props;
    }
    
    /*@Bean

        dataSourceBuilder.url("jdbc:db2://localhost:50000/DB0TDEV:currentSchema=CWSINT;");
        dataSourceBuilder.username("db2inst1");
        dataSourceBuilder.password("db2inst1");
    */
    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.ibm.db2.jcc.DB2Driver");
        //dataSourceBuilder.url("jdbc:db2://dblb-1.nonprod-gateway.cwds.io:4016/DBN1SOC:currentSchema=CWSNS1;");
        dataSourceBuilder.url(props.getJdbcUrl());
        dataSourceBuilder.username(props.getDb2User());
        dataSourceBuilder.password(props.getDb2Pass());
        return dataSourceBuilder.build();
    }
}
