package com.example.demo.config;

import org.hibernate.envers.RevisionListener;
import com.example.demo.audit.CustomRevisionEntity;
import com.example.demo.security.AuditContext;

public class AuditRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        CustomRevisionEntity audit = (CustomRevisionEntity) revisionEntity;

        audit.setUsername(AuditContext.getCurrentUser());
    }
}
