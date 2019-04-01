package org.conquernos.cinnamon.manager.control.schema;


import java.io.Serializable;

public class Subject implements Serializable {

	private final String subject;
	private final int version;

	// Cache the hash code
	private int hash = 0;

	public Subject(String subject) {
		this(subject, 0);
	}

	public Subject(String subject, int version) {
		this.subject = subject;
		this.version = version;
	}

	public String getSubject() {
		return subject;
	}

	public int getVersion() {
		return version;
	}

	boolean isLatestVersion() {
		return version == 0;
	}

	@Override
	public int hashCode() {
		int h = hash;
		if (h == 0 && subject.length() > 0) {
			String value = subject + version;
			char val[] = value.toCharArray();

			for (int i=0; i<value.length(); i++) {
				h = 31 * h + val[i];
			}
			hash = h;
		}
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;

		if (obj instanceof Subject) {
			Subject dest = (Subject) obj;
			return subject.equals(dest.getSubject()) && version == dest.getVersion();
		}

		return false;
	}

	@Override
	public String toString() {
		return subject + 'v' + version;
	}

}
