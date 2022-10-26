package com.visionrent.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.visionrent.domain.ImageFile;

public interface ImageFileRepository extends JpaRepository<ImageFile, String>{
	
}
