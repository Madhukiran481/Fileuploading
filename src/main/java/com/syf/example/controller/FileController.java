package com.syf.example.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.syf.example.response.UploadFileResponse;
import com.syf.example.service.FileStorageService;

@RestController
public class FileController {
	
	@Autowired
    private FileStorageService fileStorageService;

	@PostMapping("/uploadFile")
	public UploadFileResponse  uploadFile(@RequestParam("file") MultipartFile file){
		
		String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
	}
	
	@RequestMapping("page")
	public String webPage(){
		return "hello.html";
	}
	
	@RequestMapping(path="/download", method= RequestMethod.GET)
	public ResponseEntity<Resource> downloadFile(String param) throws IOException{
		File file = new File("test.txt");
		//File file =new ClassPathResource("test.txt").getFile();
		
		System.out.println(" file lcoation:"+file.getAbsolutePath());
		System.out.println(" file exists:"+file.exists());
		Path path = Paths.get(file.getAbsolutePath());
		ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
		
		HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        
		return ResponseEntity.ok().headers(headers)
	            .contentLength(file.length())
	            .contentType(MediaType.parseMediaType("application/octet-stream"))
	            .body(resource);

	}
}
