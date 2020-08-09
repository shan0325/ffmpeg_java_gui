package com.ffmpeg.option.audio;

/**
 * -acodec <string> : 사용할 오디오 코덱을 지정. 
		    copy : 원본 데이터를 그대로 복사. 
		    ac3 : AC3 ( Dolby Digital ) 
		    libfaac : AAC-LC 
		    mp2 : MPEG Audio Layer II 
		    mp3 : MPEG Audio Layer III 
		    pcm_s16le : 비압축 16-bit PCM Audio 
 */
public enum AudioCodec {
	COPY("copy"), AAC("aac"), AC3("ac3"), LIBFAAC("libfaac"), MP2("mp2"), MP3("mp3"), PCM_S16LE("pcm_s16le");
	
	private String codec;
	
	private AudioCodec(String codec) {
		this.codec = codec;
	}
	
	public String getCodec() {
		return codec;
	}
}
