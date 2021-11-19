package com.mainproject.outlinevisionv2.service;

import com.mainproject.outlinevisionv2.entity.File;
import com.mainproject.outlinevisionv2.repository.ClientRepository;
import com.mainproject.outlinevisionv2.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileService {
    private FileRepository fileRepository;

    @Autowired
    public void setFileRepository(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    private ClientRepository clientRepository;

    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public File storeFile(MultipartFile file) throws IOException{
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        File file1 = new File(fileName,file.getContentType(), file.getBytes());

        return fileRepository.save(file1);
    }

    public File getFile(String id){
        return fileRepository.findById(id).orElse(null);
    }

    public Stream<File> getAllFiles(){
        return fileRepository.findAll().stream();
    }
}
