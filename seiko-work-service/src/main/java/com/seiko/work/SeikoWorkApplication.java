package com.seiko.work;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Seiko Work Service 启动类
 *
 * @author seiko
 */
@SpringBootApplication
@EnableCaching
@MapperScan("com.seiko.work.module.**.mapper")
public class SeikoWorkApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeikoWorkApplication.class, args);
        System.out.println("""
                 ____           _          __        __         _
                / ___|  ___  __| | ___  _ _\\ \\      / /__  _ __| | __
                \\___ \\ / _ \\/ _` |/ _ \\| '__\\ \\ /\\ / / _ \\| '__| |/ /
                 ___) |  __/ (_| | (_) | |   \\ V  V / (_) | |  |   <
                |____/ \\___|\\__,_|\\___/|_|    \\_/\\_/ \\___/|_|  |_|\\_\\

                Seiko Work Service started successfully!
                API docs: http://localhost:8080/api/swagger-ui.html
                """);
    }
}
