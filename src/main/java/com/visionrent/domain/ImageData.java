package com.visionrent.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_imagedata")
@Entity
public class ImageData extends BaseEntity {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	//@JsonIgnore
	@Lob
	private byte[] data;

	public ImageData(byte[] data) {
		this.data = data;
	}
	
	public ImageData(Long id) {
		this.id = id;
	}
	
}
