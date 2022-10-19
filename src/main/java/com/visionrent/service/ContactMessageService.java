package com.visionrent.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.visionrent.domain.ContactMessage;
import com.visionrent.exception.ResourceNotFoundException;
import com.visionrent.exception.message.ErrorMessage;
import com.visionrent.repository.ContactMessageRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ContactMessageService {
	
	private ContactMessageRepository contactMessageRepository;
	
	
	public void saveMessage(ContactMessage contactMessage) {
		contactMessageRepository.save(contactMessage);
	}
	
	public List<ContactMessage> getAll(){
		return contactMessageRepository.findAll();
	}
	
	public Page<ContactMessage> getAll(Pageable pageable){
		return contactMessageRepository.findAll(pageable);
	}
	
	public ContactMessage getContactMessage(Long id) {
		return contactMessageRepository.findById(id).orElseThrow(() -> 
		new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE, id)));
	}
	
	public void deleteContactMessage(Long id) {
		ContactMessage message = getContactMessage(id);
		contactMessageRepository.delete(message);
	}
	
	public void updateContactMessage(Long id, ContactMessage contactMessage) {
		ContactMessage foundContactMessage = getContactMessage(id);
		foundContactMessage.setName(contactMessage.getName());
		foundContactMessage.setEmail(contactMessage.getEmail());
		foundContactMessage.setSubject(contactMessage.getSubject());
		foundContactMessage.setBody(contactMessage.getBody());
		contactMessageRepository.save(foundContactMessage);
	}
}
