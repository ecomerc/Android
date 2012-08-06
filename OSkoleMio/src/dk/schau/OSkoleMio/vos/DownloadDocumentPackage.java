package dk.schau.OSkoleMio.vos;

public class DownloadDocumentPackage
{
	public String fullPath = "";
	public String mimeType = "";
	
	public DownloadDocumentPackage(String fullPath, String mimeType)
	{
		this.fullPath = fullPath;
		this.mimeType = mimeType;
	}
}
