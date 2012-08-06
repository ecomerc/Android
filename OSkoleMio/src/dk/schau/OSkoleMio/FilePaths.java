package dk.schau.OSkoleMio;

import java.io.File;

import android.os.Environment;

public class FilePaths
{
	private static final String _DOWNLOADFOLDER = "/OSkoleMio/downloads/";
	private static final String _EXTERNALSCHOOLSFILE = "/OSkoleMio/schools.xml";
	private static final String _INTERNALSCHOOLSFILE = "schools.xml.png";
	private static final String _WEBSCHOOLSFILE = "http://www.schau.dk/oskolemio/schools.xml";

	public static String getDownloadFolder()
	{
		return Environment.getExternalStorageDirectory() + _DOWNLOADFOLDER;
	}

	public static String getExternalSchoolsFile()
	{
		return Environment.getExternalStorageDirectory() + _EXTERNALSCHOOLSFILE;
	}

	public static String getInternalSchoolsFile()
	{
		return _INTERNALSCHOOLSFILE;
	}

	public static String getWebSchoolsFile()
	{
		return _WEBSCHOOLSFILE;
	}

	public static void createDownloadFolder()
	{
		File directory = new File(getDownloadFolder());
		directory.mkdirs();
	}
}
