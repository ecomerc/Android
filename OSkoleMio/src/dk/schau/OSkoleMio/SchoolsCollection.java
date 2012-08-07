package dk.schau.OSkoleMio;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import dk.schau.OSkoleMio.vos.School;

import android.app.Activity;
import android.os.Environment;

public class SchoolsCollection
{
	private static final String _ASSETSSCHOOLSFILE = "schools.xml.png";
	private static final String _FOLDER = "/OSkoleMio";	private static final String _SCHOOLSFILE = _FOLDER + "/schools.xml";
	public static ArrayList<School> schools = new ArrayList<School>();
	private static ArrayList<String> _allNames = new ArrayList<String>();

	public static boolean init(Activity parent)
	{
		try
		{
			copyBundledSchoolsFile(parent);
		}
		catch (Exception ex)
		{
			return false;
		}
		
		String xml;
		try
		{
			xml = getXml();
		}
		catch (Exception ex)
		{
			return false;
		}
		
		Document doc = XMLFunctions.xmlFromString(xml);
		if (doc == null)
		{
			return false;
		}

		dispose();

		NodeList nodes = doc.getElementsByTagName("school");

		for (int index = 0; index < nodes.getLength(); index++)
		{
			Element element = (Element) nodes.item(index);

			schools.add(new School(XMLFunctions.getValue(element, "name"), XMLFunctions.getValue(element, "url")));
		}
		
		sortSchoolsList();
		return true;
	}

	public static void dispose()
	{
		_allNames.clear();
		schools.clear();
	}
	
	private static void copyBundledSchoolsFile(Activity parent) throws IOException
	{
		File file = new File(Environment.getExternalStorageDirectory() + _SCHOOLSFILE);

		if (file.exists())
		{
			return;
		}
		
		createOSkoleMioFolder();
		copyFile(parent);
	}
	
	private static void createOSkoleMioFolder()
	{
		String folder = Environment.getExternalStorageDirectory() + _FOLDER;
		File directory = new File(folder);
		
		if (directory.exists())
		{
			return;
		}
		directory.mkdirs();
	}
	
	private static void copyFile(Activity parent) throws IOException
	{
		InputStream input = parent.getAssets().open(_ASSETSSCHOOLSFILE);
		OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + _SCHOOLSFILE);

		byte[] buffer = new byte[2048];
		int length;
		while ((length = input.read(buffer)) > 0)
		{
			output.write(buffer, 0, length);
		}

		output.flush();
		output.close();
		input.close();
	}

	private static String getXml() throws IOException
	{
		FileInputStream fileInputStream = null;
		
		try
		{
			fileInputStream = new FileInputStream(Environment.getExternalStorageDirectory() + _SCHOOLSFILE);

			return XMLFunctions.readXmlFile(fileInputStream);
		}
		finally
		{
			if (fileInputStream != null)
			{
				fileInputStream.close();
			}
		}
	}

	public static void setRemoteXml(String xml) throws IOException
	{
		PrintWriter printWriter = null;

		try
		{
			createOSkoleMioFolder();
			FileOutputStream fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + _SCHOOLSFILE));
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
			printWriter = new PrintWriter(bufferedOutputStream);

			printWriter.write(xml);
		}
		finally
		{
			if (printWriter != null)
			{
				try
				{
					printWriter.close();
				}
				catch (Exception exception)
				{
				}
			}
		}
	}

	private static void sortSchoolsList()
	{
		final Collator dkCollator = Collator.getInstance(new Locale("da", "DK"));
		if (dkCollator == null)
		{
			Collections.sort(schools);
		}
		else
		{
			Collections.sort(schools, new Comparator<School>()
			{
				@Override
				public int compare(School one, School another)
				{
					return dkCollator.compare(one.getName(), another.getName());
				}
			});
		}
	}

	public static ArrayList<String> getAllNames(Activity parent)
	{
		if (_allNames.size() != 0)
		{
			return _allNames;
		}
		
		if (schools.size() == 0)
		{
			if (init(parent))
			{
				return _allNames;
			}
		}

		Iterator<School> iterator = schools.iterator();

		while (iterator.hasNext())
		{
			School school = iterator.next();
			_allNames.add(school.getName());
		}

		DanishStringSorter danishStringSorter = new DanishStringSorter();
		_allNames = (ArrayList<String>) danishStringSorter.sort(_allNames);
		
		return _allNames;
	}
}
