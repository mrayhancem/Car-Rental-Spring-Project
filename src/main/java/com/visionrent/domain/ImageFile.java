package com.visionrent.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Table(name="t_imagefile")
@Entity
public class ImageFile extends BaseEntity {
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name="uuid", strategy = "uuid2")
	private String id;
	
	private String name;
	
	private String type;
	
	private long length;
	
	@OneToOne(cascade = CascadeType.ALL)
	private ImageData imgData;
	
	public ImageFile(String name, String type, ImageData imgData) {
		this.name = name;
		this.type = type;
		this.imgData = imgData;
		this.length = imgData.getData().length;
	}
}
