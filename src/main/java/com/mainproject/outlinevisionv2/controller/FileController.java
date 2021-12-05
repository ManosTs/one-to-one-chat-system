package com.mainproject.outlinevisionv2.controller;

import com.mainproject.outlinevisionv2.entity.Client;
import com.mainproject.outlinevisionv2.entity.File;
import com.mainproject.outlinevisionv2.repository.ClientRepository;
import com.mainproject.outlinevisionv2.repository.FileRepository;
import com.mainproject.outlinevisionv2.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Convert;
import java.util.Base64;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "/files", method = RequestMethod.POST)
public class FileController {
    private FileService fileService;

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    private ClientRepository clientRepository;

    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    private FileRepository fileRepository;

    @Autowired
    public void setFileRepository(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @PostMapping(value = "/upload/id={id}")
    public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file, @PathVariable String id){
        String message = "";
        Client clientFound = clientRepository.findClientById(id);
        try {
            if(clientFound.getFileID()!=null){
                fileRepository.deleteById(clientFound.getFileID());
            }

            File savedFile =  fileService.storeFile(file);

            clientFound.addFile(savedFile);
            clientFound.setFileID(savedFile.getId());

            clientRepository.save(clientFound);

            return ResponseEntity.ok().body(savedFile);
        }catch (Exception e){
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status((HttpStatus.EXPECTATION_FAILED)).body(message);
        }
    }

    @GetMapping("/client-id={id}")
    public ResponseEntity<?> getFile(@PathVariable String id){

        Client clientFound = clientRepository.findClientById(id);
        File file = fileService.getFile(clientFound.getFileID());


        String byteToString = Base64.getEncoder().encodeToString(file.getData());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.getData().length));

        return ResponseEntity.ok().headers(headers).body("data:image/png;base64,"+byteToString);
    }

}
