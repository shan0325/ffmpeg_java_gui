package com.ffmpeg;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.ffmpeg.option.audio.AudioCodec;
import com.ffmpeg.option.video.VideoCodec;

public class FFMpegConvert extends JFrame {

	private static final String FFMPEG_PATH = System.getProperty("user.dir") + File.separator + "ffmpeg" + File.separator;
	private static final String FFMPEG_EXE_PATH = FFMPEG_PATH + "bin" + File.separator + "ffmpeg.exe";

	private JFileChooser jfc;
	private JFileChooser jfc_subt;
	private JButton jbt_open;
	private JButton jbt_subt;
	private JButton jbt_conv;
	private JLabel jlb;
	private JTextArea jta;
	private JScrollPane jsp;
	private JRadioButton[] convTypeRadio;
	private ButtonGroup convTypeGroup;
	private JComboBox<String> videoCodecJcb;
	private JComboBox<String> audioCodecJcb;
	private Cmd cmd;
	private Map<String, String> optionMap;

	public FFMpegConvert() {
		super("동영상 변환기");
		init();
		start();
		
		cmd = new Cmd();
		optionMap = new HashMap<>();
	}

	public void init() {
		ConvertType[] cTypes = ConvertType.values();
		String defaultJfcPath = "C:\\Users\\" + System.getenv("USERNAME") + "\\Downloads";
		
		jfc = new JFileChooser();
		String[] cTypesStrs = new String[cTypes.length];
		for (int i = 0; i < cTypes.length; i++) {
			cTypesStrs[i] = cTypes[i].name().toLowerCase();
		}
		jfc.setFileFilter(new FileNameExtensionFilter("video", cTypesStrs)); // 파일 필터
		jfc.setMultiSelectionEnabled(false); // 다중 선택 불가
		jfc.setCurrentDirectory(new File(defaultJfcPath)); // 기본경로지정
				
		jfc_subt = new JFileChooser();
		jfc_subt.setFileFilter(new FileNameExtensionFilter("subtitle", "srt"));
		jfc_subt.setMultiSelectionEnabled(false); // 다중 선택 불가
		jfc_subt.setCurrentDirectory(new File(defaultJfcPath)); // 기본경로지정
		
		jbt_open = new JButton("열기");
		jbt_subt = new JButton("자막넣기");
		jbt_conv = new JButton("변환");
		jlb = new JLabel("파일을 선택하세요");
		jta = new JTextArea();
		jsp = new JScrollPane(jta);
		jsp.setPreferredSize(new Dimension(0, 200));
		
		videoCodecJcb = new JComboBox<>();
		videoCodecJcb.addItem("선택없음");
		VideoCodec[] videoCodec = VideoCodec.values();
		for (int i = 0; i < videoCodec.length; i++) {
			videoCodecJcb.addItem(videoCodec[i].name());
		}
		
		audioCodecJcb = new JComboBox<>();
		audioCodecJcb.addItem("선택없음");
		AudioCodec[] audioCodec = AudioCodec.values();
		for (int i = 0; i < audioCodec.length; i++) {
			audioCodecJcb.addItem(audioCodec[i].name());
		}
		
		convTypeRadio = new JRadioButton[cTypes.length];
		convTypeGroup = new ButtonGroup();
		
		JPanel panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
		panel1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		panel1.add(jbt_open);
		panel1.add(jbt_subt);
		panel1.add(jbt_conv);
		
		JPanel panel2 = new JPanel();
		panel2.setBorder(new TitledBorder("변환 타입 설정"));
		for (int i = 0; i < convTypeRadio.length; i++) {
			convTypeRadio[i] = new JRadioButton(cTypes[i].name());
			convTypeGroup.add(convTypeRadio[i]);
			panel2.add(convTypeRadio[i]);
		}
		
		JPanel panel3 = new JPanel();
		panel3.setBorder(new TitledBorder("코덱 설정"));
		panel3.add(new JLabel("비디오: "));
		panel3.add(videoCodecJcb);
		panel3.add(new JLabel("  "));
		panel3.add(new JLabel("오디오 : "));
		panel3.add(audioCodecJcb);
		
		JPanel panel4 = new JPanel();
		panel4.setLayout(new BoxLayout(panel4, BoxLayout.X_AXIS));
		panel4.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		panel4.add(jlb);
		
		JPanel endPanel = new JPanel();
		endPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		endPanel.setLayout(new BoxLayout(endPanel, BoxLayout.X_AXIS));
		endPanel.add(jsp);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(panel1);
		panel.add(panel4);
		panel.add(panel2);
		panel.add(panel3);
		panel.add(endPanel);
		
		this.add(panel);
		this.setSize(600, 500);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void start() {
		// 열기 버튼 클릭 시
		jbt_open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// showopendialog 열기 창을 열고 확인 버튼을 눌렀는지 확인
				if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					jlb.setText("영상 파일 : " + jfc.getSelectedFile().getName());
					jta.append("영상 파일 : " + jfc.getSelectedFile().getName() + "\n");
					jfc_subt.setCurrentDirectory(jfc.getSelectedFile());
				}
			}
		});
		
		// 자막넣기 버튼 클릭 시
		jbt_subt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// showopendialog 열기 창을 열고 확인 버튼을 눌렀는지 확인
				if (jfc_subt.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					jta.append("자막 파일 : " + jfc_subt.getSelectedFile().getName() + "\n");
				}
			}
		});
		
		// 변환 버튼 클릭 시
		jbt_conv.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(jfc.getSelectedFile() == null) {
					JOptionPane.showMessageDialog(null, "파일을 선택하세요");
					return;
				}
				else if(!isCheckedConvertType(convTypeGroup)) {
					JOptionPane.showMessageDialog(null, "변환 타입을 선택하세요");
					return;
				}
				
				Map<String, String> convertInfo = makeConvertInfo(jfc, jfc_subt, convTypeGroup);
				jlb.setText(convertInfo.get("fileName") + " -> " + convertInfo.get("convFileName") + " 변환 시작");
				System.out.println(convertInfo.get("fileName") + " -> " + convertInfo.get("convFileName") + " 변환 시작");
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						String command = cmd.inputCommand(makeCommand(convertInfo));
						jta.append(command + "\n");
						
						String result = cmd.execCommand(command, reader -> {
							String line = null;
							StringBuffer sb = new StringBuffer();
							try {
								while((line = reader.readLine()) != null) {
									sb.append(line + "\n");
									jta.append(line + "\n");
									jta.setCaretPosition(jta.getDocument().getLength());
								}
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
							return sb;
						});
						if(result != null) {
							JOptionPane.showMessageDialog(null, "완료되었습니다");
						} else {
							JOptionPane.showMessageDialog(null, "실패하였습니다");
						}
						System.out.println(result);
						
						jbt_open.setEnabled(true);
						jbt_subt.setEnabled(true);
						jbt_conv.setEnabled(true);
					}
				}).start();
				
				jbt_open.setEnabled(false);
				jbt_subt.setEnabled(false);
				jbt_conv.setEnabled(false);
//				System.exit(0);
			}
		});
		
		// 비디오코덱 선택 시
		videoCodecJcb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String videoCodecItem = (String) videoCodecJcb.getSelectedItem();
//				System.out.println(videoCodecItem);
				
				if("선택없음".equals(videoCodecItem)) {
					optionMap.remove("videoCodec");
				} else {
					VideoCodec videoCodec = VideoCodec.valueOf(videoCodecItem);
					optionMap.put("videoCodec", "-vcodec " + videoCodec.getCodec());
				}
			}
		});
		
		// 오디오코덱 선택 시
		audioCodecJcb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String audioCodecItem = (String) audioCodecJcb.getSelectedItem();
//				System.out.println(audioCodecItem);
				
				if("선택없음".equals(audioCodecItem)) {
					optionMap.remove("audioCodec");
				} else {
					AudioCodec audioCodec = AudioCodec.valueOf(audioCodecItem);
					optionMap.put("audioCodec", "-acodec " + audioCodec.getCodec());
				}
			}
		});
		
		// 창 닫을 시 ffmpeg 프로그램 닫기
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				cmd.destroyProcess();
			}
		});
	}
	
	public Map<String, String> makeConvertInfo(JFileChooser jfc, JFileChooser jfc_subt, ButtonGroup bg) {
		Map<String, String> map = new HashMap<String, String>();
		
		File selFile = jfc.getSelectedFile();
		String path = selFile.getPath();
		String parDir = selFile.getParent();
		String fileName = selFile.getName();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1);
		ConvertType ct = getConvertType(bg);
		String convFileName = fileName.substring(0, fileName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + "." + ct.name().toLowerCase();
		
		map.put("filePath", path);
		map.put("fileName", fileName);
		map.put("fileExt", fileExt.toLowerCase());
		map.put("parDir", parDir);
		map.put("convFilePath", parDir + File.separator + convFileName);
		map.put("convFileName", convFileName);
		map.put("convFileExt", ct.name().toLowerCase());
		
		File subtFile = jfc_subt.getSelectedFile();
		if(subtFile != null) {
			map.put("subtFilePath", subtFile.getPath());
			map.put("subtFileName", subtFile.getName());
			map.put("subtFileParDir", subtFile.getParent());
		}
		
		return map;
	}
	
	public String makeCommand(Map<String, String> convertInfoMap) {
		StringBuffer sb = new StringBuffer();
		sb.append(FFMPEG_EXE_PATH);
		sb.append(" -i ");
		sb.append("\"");
		sb.append(convertInfoMap.get("filePath"));
		sb.append("\"");
		
		if(convertInfoMap.get("subtFilePath") != null) {
			sb.append(" -sub_charenc EUC-KR ");
			sb.append(" -i ");
			sb.append("\"");
			sb.append(convertInfoMap.get("subtFilePath"));
			sb.append("\"");
//			sb.append(" -map 0:v -map 0:a -map 1 ");
			sb.append(" -map 0:0 -map 0:1 -map 1:0 ");
			
//			sb.append(" -i ");
//			sb.append("\"");
//			sb.append("subtitles=");
//			sb.append(convertInfoMap.get("subtFilePath"));
//			sb.append("\"");
		}
		
		sb.append(getOption());
		
		if(convertInfoMap.get("subtFilePath") != null) {
			if(ConvertType.MP4.name().toLowerCase().equals(convertInfoMap.get("convFileExt"))) {
				sb.append(" -c:s mov_text ");
			} else {
				sb.append(" -c:s srt ");
			}
			sb.append(" -metadata:s:s:0 language=kor ");
		}
		
		sb.append("\"");
		sb.append(convertInfoMap.get("convFilePath"));
		sb.append("\"");
		
		return sb.toString();
	}
	
	public String makeSubtitleCommand(Map<String, String> convertInfoMap) {
		StringBuffer sb = new StringBuffer();
		sb.append(FFMPEG_EXE_PATH);
		sb.append(" -i ");
		sb.append("\"");
		sb.append(convertInfoMap.get("subtFilePath"));
		sb.append("\"");
		sb.append(" ");
		sb.append("\"");
		sb.append(convertInfoMap.get("subtFileParDir"));
		sb.append(File.separator);
		sb.append("subtitleTempFile.ass");
		sb.append("\"");
		
		return sb.toString();
	}
	
	public String getOption() {
		StringBuffer option = new StringBuffer();
		option.append(" ");
		
		Iterator<String> keyIter = optionMap.keySet().iterator();
		while(keyIter.hasNext()) {
			String key = keyIter.next();
			option.append(optionMap.get(key));
			option.append(" ");
		}
		
		return option.toString();
	}
	
	public boolean isCheckedConvertType(ButtonGroup bg) {
		return getConvertType(bg) == null ? false : true; 
	}
	
	public ConvertType getConvertType(ButtonGroup bg) {
		ConvertType ct = null;
		Enumeration<AbstractButton> elements = bg.getElements();
		while(elements.hasMoreElements()) {
			AbstractButton ab = elements.nextElement();
			JRadioButton jb = (JRadioButton)ab;
			if(jb.isSelected()) {
				ct = ct.valueOf(jb.getActionCommand());
			}
		}
		return ct;
	}

	public static void main(String[] args) {
		FFMpegConvert ffMpegConvert = new FFMpegConvert();
		
	}
}
