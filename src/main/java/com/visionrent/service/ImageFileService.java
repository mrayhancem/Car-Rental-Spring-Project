package com.visionrent.service;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.visionrent.domain.ImageData;
import com.visionrent.domain.ImageFile;
import com.visionrent.exception.ResourceNotFoundException;
import com.visionrent.exception.message.ErrorMessage;
import com.visionrent.repository.ImageFileRepository;

@Service
public class ImageFileService {
	
	@Autowired
	ImageFileRepository imageFileRepository;

	public String saveImage(MultipartFile file) {
		
		String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
		ImageFile imageFile = null;
		
		try {
			ImageData imgData = new ImageData(file.getBytes());
			imageFile = new ImageFile(fileName, file.getContentType(), imgData);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.getMessage());
		}
		
		imageFileRepository.save(imageFile);
		
		return imageFile.getId();
		
	}
	
	public ImageFile getImageById(String id) {
		return imageFileRepository.findById(id).orElseThrow(()-> 
		new ResourceNotFoundException(String.format(ErrorMessage.IMAGE_NOT_FOUND_MESSAGE, id)));
	}
}
