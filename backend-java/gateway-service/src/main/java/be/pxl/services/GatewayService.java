package be.pxl.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * GatewayService
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayService
{
    public static void main( String[] args )
    {
        SpringApplication.run(GatewayService.class, args);
    }
}
