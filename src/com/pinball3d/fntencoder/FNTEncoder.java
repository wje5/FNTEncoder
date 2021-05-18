package com.pinball3d.fntencoder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

public class FNTEncoder {
	public static void main(String[] args) {
		String path = "font.fnt";
		String outputPath = "font.bin";
		BufferedReader bf = null;
		OutputStream stream = null;
		try {
			FileReader fr = new FileReader(path);
			bf = new BufferedReader(fr);
			int size = 0, lineHeight = 0, base = 0, scaleW = 0, scaleH = 0, pages = 0, charsCount = 0,
					kerningsCount = 0, charIndex = 0, kerningIndex = 0;
			int[] padding = new int[4], spacing = new int[2];
			boolean bold = false, italic = false, smooth = false, aa = false;
			int[][] chars = null, kernings = null;
			while (true) {
				String s = bf.readLine();
				if (s == null || s.isEmpty()) {
					break;
				}
				String[] a = s.split("\\s+");
				switch (a[0]) {
				case "info":
					for (int i = 1; i < a.length; i++) {
						String[] t = a[i].split("=");
						if (t.length == 2) {
							switch (t[0]) {
							case "size":
								size = Integer.valueOf(t[1]);
								break;
							case "bold":
								bold = t[1].equals("1");
								break;
							case "italic":
								italic = t[1].equals("1");
								break;
							case "smooth":
								smooth = t[1].equals("1");
								break;
							case "aa":
								aa = t[1].equals("1");
								break;
							case "padding":
								String[] p = t[1].split(",");
								for (int j = 0; j < 4; j++) {
									padding[j] = Integer.valueOf(p[j]);
								}
								break;
							case "spacing":
								p = t[1].split(",");
								spacing[0] = Integer.valueOf(p[0]);
								spacing[1] = Integer.valueOf(p[1]);
								break;
							}
						}
					}
					break;
				case "common":
					for (int i = 1; i < a.length; i++) {
						String[] t = a[i].split("=");
						if (t.length == 2) {
							switch (t[0]) {
							case "lineHeight":
								lineHeight = Integer.valueOf(t[1]);
								break;
							case "base":
								base = Integer.valueOf(t[1]);
								break;
							case "scaleW":
								scaleW = Integer.valueOf(t[1]);
								break;
							case "scaleH":
								scaleH = Integer.valueOf(t[1]);
								break;
							case "pages":
								pages = Integer.valueOf(t[1]);
								break;
							}
						}
					}
					break;
				case "chars":
					for (int i = 1; i < a.length; i++) {
						String[] t = a[i].split("=");
						if (t.length == 2) {
							if (t[0].equals("count")) {
								charsCount = Integer.valueOf(t[1]);
								chars = new int[charsCount][9];
								charIndex = 0;
							}
						}
					}
					break;
				case "kernings":
					for (int i = 1; i < a.length; i++) {
						String[] t = a[i].split("=");
						if (t.length == 2) {
							if (t[0].equals("count")) {
								kerningsCount = Integer.valueOf(t[1]);
								kernings = new int[kerningsCount][3];
								kerningIndex = 0;
							}
						}
					}
					break;
				case "char":
					for (int i = 1; i < a.length; i++) {
						String[] t = a[i].split("=");
						if (t.length == 2) {
							int ind = -1;
							switch (t[0]) {
							case "id":
								ind = 0;
								break;
							case "x":
								ind = 1;
								break;
							case "y":
								ind = 2;
								break;
							case "width":
								ind = 3;
								break;
							case "height":
								ind = 4;
								break;
							case "xoffset":
								ind = 5;
								break;
							case "yoffset":
								ind = 6;
								break;
							case "xadvance":
								ind = 7;
								break;
							case "page":
								ind = 8;
								break;
							}
							if (ind >= 0) {
								chars[charIndex][ind] = Integer.valueOf(t[1]);
							}
						}
					}
					charIndex++;
					break;
				case "kerning":
					for (int i = 1; i < a.length; i++) {
						String[] t = a[i].split("=");
						if (t.length == 2) {
							int ind = -1;
							switch (t[0]) {
							case "first":
								ind = 0;
								break;
							case "second":
								ind = 1;
								break;
							case "amount":
								ind = 2;
								break;
							}
							if (ind >= 0) {
								kernings[kerningIndex][ind] = Integer.valueOf(t[1]);
							}
						}
					}
					kerningIndex++;
					break;
				}
			}
			File output = new File(outputPath);
			stream = new FileOutputStream(output);
			writeBooleans(stream, new boolean[] { bold, italic, smooth, aa, false, false, false, false });
			writeInt(stream, size);
			writeInt(stream, lineHeight);
			writeInt(stream, base);
			writeInt(stream, scaleW);
			writeInt(stream, scaleH);
			writeInt(stream, pages);
			writeInt(stream, charsCount);
			writeInt(stream, kerningsCount);
			writeInt(stream, padding[0]);
			writeInt(stream, padding[1]);
			writeInt(stream, padding[2]);
			writeInt(stream, padding[3]);
			writeInt(stream, spacing[0]);
			writeInt(stream, spacing[1]);
			for (int i = 0; i < charsCount; i++) {
				int[] a = chars[i];
				for (int j = 0; j < 9; j++) {
					writeInt(stream, a[j]);
				}
			}
			for (int i = 0; i < kerningsCount; i++) {
				int[] a = kernings[i];
				for (int j = 0; j < 3; j++) {
					writeInt(stream, a[j]);
				}
			}
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bf != null) {
				try {
					bf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void writeInt(OutputStream stream, int i) throws IOException {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) ((i >> 24) & 0xFF);
		bytes[1] = (byte) ((i >> 16) & 0xFF);
		bytes[2] = (byte) ((i >> 8) & 0xFF);
		bytes[3] = (byte) (i & 0xFF);
		stream.write(bytes);
	}

	public static void writeBooleans(OutputStream stream, boolean[] bools) throws IOException {
		if (bools.length != 8) {
			throw new IllegalArgumentException();
		}
		byte b = 0;
		for (int i = 0; i < 7; i++) {
			if (bools[i]) {
				b |= 1;
			}
			b <<= 1;
		}
		if (bools[7]) {
			b |= 1;
		}
		stream.write(b);
	}
}
