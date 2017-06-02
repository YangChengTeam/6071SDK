package com.feiyou.plugin;

public class EmulatorCheck {
	public native int anti();

	static {
		System.loadLibrary("fyae");
	}
}