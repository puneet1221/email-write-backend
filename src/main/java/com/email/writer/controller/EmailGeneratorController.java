package com.email.writer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.email.writer.dto.EmailRequest;
import com.email.writer.service.EmailGneratorService;

@RestController
@RequestMapping("/api/email")
public class EmailGeneratorController {

	@Autowired
	EmailGneratorService emailGneratorService;

	@CrossOrigin(origins = {"https://mail.google.com"})
	@PostMapping("/generate")
	public ResponseEntity<String> generateEmail(@RequestBody EmailRequest request) {
		String response = emailGneratorService.generateEmailReply(request);
		return ResponseEntity.ok(response);
	}

}
