package com.ffmpeg.option.video;

public enum VideoCodec {
	COPY("copy"), LIBX264("libx264");
	
	private String codec;
	
	private VideoCodec(String codec) {
		this.codec = codec;
	}
	
	public String getCodec() {
		return codec;
	}
}
