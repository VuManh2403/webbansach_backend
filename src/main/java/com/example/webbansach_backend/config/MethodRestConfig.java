package com.example.webbansach_backend.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class MethodRestConfig implements RepositoryRestConfigurer {

    @Autowired
    private EntityManager entityManager;
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {


        // expose ids
        // Cho phép trả về id tat ca cac phuong thuc
        config.exposeIdsFor(entityManager.getMetamodel().getEntities().stream().map(Type::getJavaType).toArray(Class[]::new));
        // config.exposeIdsFor(TheLoai.class); nguyen the loai

        // cho phep frontend dc truy cap cac phuong thuc
        // CORS configuration
//        cors.addMapping("/**") // tat ca ca duong dan
//                .allowedOrigins(url)
//                .allowedMethods("GET", "POST", "PUT", "DELETE");
//
//
//        // Chặn các methods
//        HttpMethod[] chanCacPhuongThuc ={
//                HttpMethod.POST,
//                HttpMethod.PUT,
//                HttpMethod.PATCH,
//                HttpMethod.DELETE,
//        };
//        disableHttpMethods(TheLoai.class, config, chanCacPhuongThuc);
//
//        // Chặn các method DELETE
//        HttpMethod[] phuongThucDelete = {
//                HttpMethod.DELETE
//        };
//        disableHttpMethods(NguoiDung.class, config,phuongThucDelete );
    }

    private void disableHttpMethods(Class c,
                                    RepositoryRestConfiguration config,
                                    HttpMethod[] methods){
        config.getExposureConfiguration()
                .forDomainType(c)
                .withItemExposure((metdata, httpMethods) -> httpMethods.disable(methods))
                .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(methods));
    }
}
