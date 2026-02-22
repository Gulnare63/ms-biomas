//package com.example.audit.resolver;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.ApplicationContext;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//
//@Component
//@RequiredArgsConstructor
//public class RepositoryResolver {
//
//    private final ApplicationContext context;
//
//    public JpaRepository<?, Long> resolve(Class<?> entityClass) {
//
//        Map<String, JpaRepository> repositories = context.getBeansOfType(JpaRepository.class);
//
//        for (JpaRepository repository : repositories.values()) {
//            Class<?> domainType = repository.getClass()
//                    .getInterfaces()[0]
//                    .getGenericInterfaces()[0]
//                    .getClass();
//
//            if (repository.getClass().getName().contains(entityClass.getSimpleName())) {
//                return repository;
//            }
//        }
//
//        throw new RuntimeException("Repository not found for " + entityClass.getSimpleName());
//    }
//}
