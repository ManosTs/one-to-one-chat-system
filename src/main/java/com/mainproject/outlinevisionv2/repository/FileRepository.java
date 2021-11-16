package com.mainproject.outlinevisionv2.repository;

import com.mainproject.outlinevisionv2.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File,String> {
    File findFileByName(String name);
    File findFileById(String id);
}
