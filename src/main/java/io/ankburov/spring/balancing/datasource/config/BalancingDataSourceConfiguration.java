package io.ankburov.spring.balancing.datasource.config;

import io.ankburov.spring.balancing.datasource.BalancingDataSource;
import io.ankburov.spring.balancing.datasource.balancingtype.BalancingStrategy;
import io.ankburov.spring.balancing.datasource.balancingtype.RandomBalancingStrategy;
import io.ankburov.spring.balancing.datasource.balancingtype.StandbyBalancingStrategy;
import io.ankburov.spring.balancing.datasource.factory.DataSourceFactory;
import io.ankburov.spring.balancing.datasource.factory.HikariDataSourceFactory;
import io.ankburov.spring.balancing.datasource.failed.AlwaysUpdateFailedDataSourceStrategy;
import io.ankburov.spring.balancing.datasource.failed.UpdateFailedDataSourceStrategy;
import io.ankburov.spring.balancing.datasource.filter.FilteringStrategy;
import io.ankburov.spring.balancing.datasource.filter.OnlyWorkingFilteringStrategy;
import io.ankburov.spring.balancing.datasource.filter.UseAllFilteringStrategy;
import io.ankburov.spring.balancing.datasource.log.FailedDataSourceLogStrategy;
import io.ankburov.spring.balancing.datasource.log.TimedFailedDataSourceLogStrategy;
import io.ankburov.spring.balancing.datasource.property.BalancingDataSourceProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(BalancingDataSourceProperties.class)
public class BalancingDataSourceConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "spring.balancing-dataSources-config.balancing", value = "type", havingValue = "STANDBY", matchIfMissing = true)
    public BalancingStrategy standbyBalancingStrategy() {
        return new StandbyBalancingStrategy();
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.balancing-dataSources-config.balancing", value = "type", havingValue = "RANDOM")
    public BalancingStrategy randomBalancingStrategy() {
        return new RandomBalancingStrategy();
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.balancing-dataSources-config.filtering", value = "type", havingValue = "ALL")
    public FilteringStrategy useAllFilteringStrategy() {
        return new UseAllFilteringStrategy();
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.balancing-dataSources-config.filtering", value = "type", havingValue = "ONLY_WORKING")
    public FilteringStrategy onlyWorkingFilteringStrategy(BalancingDataSourceProperties properties) {
        return new OnlyWorkingFilteringStrategy(properties.getFiltering().getTimeThreshold());
    }

    @Bean
    public FailedDataSourceLogStrategy timedFailedDataSourceLogStrategy(BalancingDataSourceProperties properties) {
        return new TimedFailedDataSourceLogStrategy(properties.getLogging().getTimeThreshold());
    }

    @Bean
    public UpdateFailedDataSourceStrategy alwaysUpdateFailedDataSourceStrategy() {
        return new AlwaysUpdateFailedDataSourceStrategy();
    }

    @Bean
    public DataSourceFactory hikariDataSourceFactory() {
        return new HikariDataSourceFactory();
    }

    @Bean("dataSource")
    @ConditionalOnProperty(prefix = "spring.balancing-dataSources-config.balancing", value = "overrideBalancingDataSource", havingValue = "false", matchIfMissing = true)
    public DataSource dataSource(BalancingDataSourceProperties properties,
                                 DataSourceFactory dataSourceFactory,
                                 FilteringStrategy filteringStrategy,
                                 BalancingStrategy balancingStrategy,
                                 FailedDataSourceLogStrategy failedDataSourceLogStrategy,
                                 UpdateFailedDataSourceStrategy updateFailedDataSourceStrategy) {
        return new BalancingDataSource(dataSourceFactory, properties, filteringStrategy, balancingStrategy,
                                       failedDataSourceLogStrategy, updateFailedDataSourceStrategy);
    }

    /*@Configuration
    @ConditionalOnProperty(prefix = "spring.balancing-dataSources-config", value = "balancingType", havingValue = "STANDBY")
    public static class StandbyBalancingConfiguration {

        @Bean
        public BalancingStrategy balancingStrategy() {
            return new StandbyBalancingStrategy();
        }

    }*/

    /*@Configuration
    @ConditionalOnProperty(prefix = "spring.balancing-dataSources-config", value = "balancingType", havingValue = "RANDOM")
    public static class RandomBalancingConfiguration {

        @Bean
        public BalancingStrategy balancingStrategy() {
            return new RandomBalancingStrategy();
        }

    }*/

    /*@Configuration
    @Import({StandbyBalancingConfiguration.class})
    @EnableConfigurationProperties(BalancingDataSourceProperties.class)
    @ConditionalOnProperty(prefix = "spring.balancing-dataSources-config", value = "overrideBalancingDataSource", havingValue = "false")
    public static class DataSourceConfiguration {

        @Bean("dataSource")
        public DataSource dataSource(BalancingDataSourceProperties properties,
                                     BalancingStrategy balancingStrategy) {
            return new BalancingDataSource(properties, balancingStrategy);
        }
    }*/
}