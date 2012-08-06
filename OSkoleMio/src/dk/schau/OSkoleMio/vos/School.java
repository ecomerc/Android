package dk.schau.OSkoleMio.vos;

public class School implements Comparable<School>
{
	private String _name;
	private String _url;

	public School(String name, String url)
	{
		_name = name;
		_url = url;
	}

	public String getName()
	{
		return _name;
	}

	public String getUrl()
	{
		return _url;
	}
	
	public int compareTo(School other)
	{
		return _name.compareTo(other.getName());
	}
}
