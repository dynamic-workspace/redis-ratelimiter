package com.example.redisratelimiter.configuration

import io.lettuce.core.ReadFrom
import io.lettuce.core.cluster.ClusterClientOptions
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisClusterConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import java.time.Duration


@Configuration
class RedisConfiguration(
    @Value("\${spring.data.redis.cluster.nodes}")
    val clusterNodes: List<String>,
) {
    @Bean
    fun lettuceConnectionFactory(): LettuceConnectionFactory {
        val clientConfiguration = LettuceClientConfiguration.builder()
            .clientOptions(
                ClusterClientOptions.builder()
                    .topologyRefreshOptions(
                        ClusterTopologyRefreshOptions.builder()
                            .refreshPeriod(Duration.ofMinutes(1))
                            .enableAdaptiveRefreshTrigger()
                            .enablePeriodicRefresh(true) // Default 가 False
                            .build()
                    )
                    .build()
            )
            .readFrom(ReadFrom.REPLICA_PREFERRED) // 복제본 노드에서 읽지만 사용할 수 없는 경우 마스터에서 읽습니다.
            .build()


        // 모든 클러스터(master, slave) 정보를 적는다. (해당 서버중 접속되는 서버에서 cluster nodes 명령어를 통해 모든 클러스터 정보를 읽어오기에 다운 됐을 경우를 대비하여 모든 노드 정보를 적어두는편이 좋다.)
        val redisClusterConfiguration = RedisClusterConfiguration(clusterNodes)
        redisClusterConfiguration.maxRedirects = 2

        return LettuceConnectionFactory(redisClusterConfiguration, clientConfiguration)
    }
}
