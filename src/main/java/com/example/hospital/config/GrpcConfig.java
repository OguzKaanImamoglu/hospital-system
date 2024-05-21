package com.example.hospital.config;

import io.grpc.protobuf.services.ProtoReflectionService;
import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {

    @Bean
    public GrpcServerConfigurer grpcServerConfigurer() {
        return serverBuilder -> serverBuilder.addService(ProtoReflectionService.newInstance());
    }
}
