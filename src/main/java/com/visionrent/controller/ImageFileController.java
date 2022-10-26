package com.visionrent.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.visionrent.domain.ImageFile;
import com.visionrent.dto.response.ImageSavedReponse;
import com.visionrent.dto.response.ResponseMessage;
import com.visionrent.service.ImageFileService;

@RestController
@RequestMapping("/files")
public class ImageFileController {
	
	@Autowired
	private ImageFileService imageFileService;
	
	@PostMapping("/upload")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ImageSavedReponse> uploadFile(@RequestParam("file") MultipartFile file){
		String imageId = imageFileService.saveImage(file);
		
		ImageSavedReponse imageSavedReponse = new ImageSavedReponse(imageId, ResponseMessage.IMAGE_SAVED_RESPONSE_MESSAGE, true);
		
		return new ResponseEntity<ImageSavedReponse>(imageSavedReponse, HttpStatus.OK);
	}
	
	
	@GetMapping("/download/{id}")
	public ResponseEntity<byte[]> downloadFile(@PathVariable String id){
		ImageFile imageFile = imageFileService.getImageById(id);
		
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachments; filenames="+ imageFile.getName())
				.body(imageFile.getImgData().getData());
	}
}
