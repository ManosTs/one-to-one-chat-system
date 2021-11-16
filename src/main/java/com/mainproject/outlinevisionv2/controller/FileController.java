package com.mainproject.outlinevisionv2.controller;

import com.mainproject.outlinevisionv2.entity.File;
import com.mainproject.outlinevisionv2.repository.FileRepository;
import com.mainproject.outlinevisionv2.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;

@RestController
@CrossOrigin(origins = "http://192.168.1.2:4200", exposedHeaders = "File-Data, Authorization")
@RequestMapping(value = "/files", method = RequestMethod.POST)
public class FileController {
    private FileService fileService;

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(value = "/upload")
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file){
        String message = "";

        try {
            File savedFile =  fileService.storeFile(file);
            return ResponseEntity.ok().body(savedFile);
        }catch (Exception e){
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status((HttpStatus.EXPECTATION_FAILED)).body(message);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity getFile(@PathVariable String id){
        File file = fileService.getFile(id);
        String s = Base64.getEncoder().encodeToString(file.getData());
        return ResponseEntity.ok().header("File-Data", s).body(file);
    }

}
