package dk.schau.OSkoleMio.activities;

import java.io.InputStream;

import org.apache.http.util.EncodingUtils;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import dk.schau.OSkoleMio.R;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class AboutActivity extends SherlockFragmentActivity
{
	private final static String _ABOUTPAGE = "about.html";
	private final static String _ABOUTPAGEFULL = "file:///android_asset/" + _ABOUTPAGE;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		WebView webView = (WebView) findViewById(R.id.aboutview);

		WebSettings webSettings = webView.getSettings();
		webSettings.setBuiltInZoomControls(true);
		webSettings.setLoadsImagesAutomatically(true);
		webSettings.setSupportZoom(true);

		try
		{
			String page = loadPage(_ABOUTPAGE);

			page = page.replace("@@VERSION@@", getPackageVersion());
			webView.loadDataWithBaseURL("fake://not/needed", page, "text/html", "utf-8", "");
		}
		catch (Exception e)
		{
			webView.loadUrl(_ABOUTPAGEFULL);
		}
	}

	private String loadPage(String file) throws Exception
	{
		InputStream inputStream = null;
		byte[] buffer = null;

		try
		{
			inputStream = getResources().getAssets().open(file);
			buffer = new byte[inputStream.available()];

			inputStream.read(buffer);
			inputStream.close();
			return EncodingUtils.getAsciiString(buffer);
		}
		catch (Exception exception)
		{
			throw exception;
		}
		finally
		{
			if (inputStream != null)
			{
				try
				{
					inputStream.close();
				}
				catch (Exception exception)
				{
				}
			}
		}
	}

	private String getPackageVersion()
	{
		try
		{
			return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		}
		catch (Exception exception)
		{
		}

		return "?";
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				finish();
				return true;
		}

		return super.onOptionsItemSelected(item);  	
	}
}
