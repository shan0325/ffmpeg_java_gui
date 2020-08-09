package com.ffmpeg.option.audio;

/**
 * -ab <int> : set bitrate (in bits/s)(초당 비트레이트를 설정 ex)128, 192, 320 etc)
	-aframes number :    set the number of audio frames to record(녹음될 오디오 프레임들의 갯수를 설정함)
	-aq quality :        set audio quality (codec-specific)(오디오의 질을 설정함)
	-ar rate<int> :           set audio sampling rate (in Hz)(오디오 샘플림을 설정함 32000,44000)
	-ac channels :       set number of audio channels(오디오 채널을 설정함 1-mono 2-stereo)
	-an :                disable audio (오디오를 사용하지 않음)
	-vol volume :        change audio volume (256=normal)(볼륨을 설정함)
	-newaudio :          add a new audio stream to the current output stream(출력할 오디오 스트림을 현재 오디오 스트림에 추가함)
	-alang code :        set the ISO 639 language code (3 letters) of the current audio stream
	-acodec <string> : 사용할 오디오 코덱을 지정. 
	    ac3 : AC3 ( Dolby Digital ) 
	    copy : 원본 데이터를 그대로 복사. 
	    libfaac : AAC-LC 
	    mp2 : MPEG Audio Layer II 
	    mp3 : MPEG Audio Layer III 
	    pcm_s16le : 비압축 16-bit PCM Audio 
	-async <int> : 오디오 동기화 방법. 오디오를 타임스템프에 맞춰서 늘리거나 줄일 수 있다. 이 매개변수는 오디오 변화에 의한 초당 최대 샘플 개수를 말한다. -async 1은 특별한 경우로 지연 수정 없이 오디오 스트림 시작 위치가 정확한 경우에만 사용가능.
 */
public enum AudioOption {
	AB("-ab"), AFRAMES("-aframes"), AQ("-aq"), AR("-ar"), AC("-ac"), AN("-an"), VOL("-vol"), NEWAUDIO("-newaudio"), ALANG("-alang"), ACODEC("-acodec"), ASYNC("-async");
	
	private String option;
	
	private AudioOption(String option) {
		this.option = option;
	}
	
	public String getOption() {
		return option;
	}
}
