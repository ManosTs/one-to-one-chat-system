package com.mainproject.outlinevisionv2.repository;

import com.mainproject.outlinevisionv2.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface FileRepository extends JpaRepository<File,String> {
    File findFileByName(String name);
    File findFileById(String id);

    @Transactional
    @Modifying
    @Query("DELETE FROM File WHERE id = :id")
    void deleteById(String id);
}
