package nl.defsoftware.mrgb.services;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public interface Parser {
	
	void loadResource() throws UnsupportedEncodingException, FileNotFoundException;

	void parseData();
	
//	void storeParsedData(Object store);
}
