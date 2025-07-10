package com.example.demo.dto;

public class LlmRequest {
    private String prompt;
    private boolean stream = false;
	public String getPrompt() {
		return prompt;
	}
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}
	public boolean isStream() {
		return stream;
	}
	public void setStream(boolean stream) {
		this.stream = stream;
	}

    // Getters and setters
    
}
