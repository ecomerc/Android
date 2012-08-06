package dk.schau.OSkoleMio;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import android.content.res.AssetFileDescriptor;

public class SchoolsCollection
{
	public static ArrayList<School> schools = new ArrayList<School>();
	private static ArrayList<String> _allNames = new ArrayList<String>();

	public static boolean init(Activity parent)
	{
		long internalSchoolFileSize = getInternalSchoolFileSize(parent);
		long externalSchoolFileSize = getExternalSchoolFileSize();

		String xml = (externalSchoolFileSize > internalSchoolFileSize) ? getRemoteXml() : getLocalXml(parent);
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

	private static long getInternalSchoolFileSize(Activity parent)
	{
		AssetFileDescriptor assetFileDescriptor = null;
		long size = 0;

		try
		{
			assetFileDescriptor = parent.getResources().getAssets().openFd(FilePaths.getInternalSchoolsFile());
			size = assetFileDescriptor.getLength();
		}
		catch (IOException ioException)
		{
		}
		finally
		{
			if (assetFileDescriptor != null)
			{
				try
				{
					assetFileDescriptor.close();
				}
				catch (Exception exception)
				{
				}
			}
		}

		return size;
	}

	private static long getExternalSchoolFileSize()
	{
		File file = new File(FilePaths.getExternalSchoolsFile());

		if (file.exists())
		{
			return file.length();
		}

		return 0;
	}

	private static String getRemoteXml()
	{
		try
		{
			FileInputStream fileInputStream = new FileInputStream(FilePaths.getExternalSchoolsFile());

			return XMLFunctions.readXmlFile(fileInputStream);
		}
		catch (Exception exception)
		{
		}

		return "";
	}

	public static void setRemoteXml(String xml)
	{
		PrintWriter printWriter = null;

		try
		{
			FilePaths.createDownloadFolder();
			FileOutputStream fileOutputStream = new FileOutputStream(new File(FilePaths.getExternalSchoolsFile()));
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
			printWriter = new PrintWriter(bufferedOutputStream);

			printWriter.write(xml);
		}
		catch (Exception exception)
		{
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

	private static String getLocalXml(Activity parent)
	{
		try
		{
			AssetFileDescriptor assetFileDescriptor = parent.getResources().getAssets().openFd(FilePaths.getInternalSchoolsFile());

			return XMLFunctions.readXmlFile(assetFileDescriptor.createInputStream());
		}
		catch (Exception exception)
		{
		}

		return "";
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
