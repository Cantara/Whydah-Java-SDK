package net.whydah.sso.util;

import java.io.File;

public class FilePair implements Comparable<FilePair> {

	File f;
	long t;

	public FilePair(File file) {
		f = file;
		t = file.lastModified();
	}

	public int compareTo(FilePair o) {
		long u = o.t;
		return t < u ? -1 : t == u ? 0 : 1;
	}

}
