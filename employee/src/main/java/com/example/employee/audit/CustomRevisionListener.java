package com.example.employee.audit;

import com.example.employee.dao.entity.CustomRevisionEntity;
//import org.springframework.security.core.Authentication;
import org.hibernate.envers.RevisionListener;
//import org.springframework.security.core.context.SecurityContextHolder;


public class CustomRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        CustomRevisionEntity rev = (CustomRevisionEntity) revisionEntity;
//        try {
//            Authentication auth = SecurityContextHolder
//                    .getContext()
//                    .getAuthentication();
//            if (auth != null && auth.isAuthenticated()) {
//                rev.setChangedBy(auth.getName());
//            } else {
//                rev.setChangedBy("system");
//            }
//        } catch (Exception e) {
            rev.setChangedBy("unknown");
        }
    }
//}