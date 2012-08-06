package dk.schau.OSkoleMio.activities;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import dk.schau.OSkoleMio.DB;
import dk.schau.OSkoleMio.R;
import dk.schau.OSkoleMio.SchoolsCollection;
import dk.schau.OSkoleMio.adapters.LoginAdapter;
import dk.schau.OSkoleMio.vos.Login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class OSkoleMioActivity extends SherlockListActivity
{
	private static int _CURRENT_MESSAGES_LEVEL = 4;
	private boolean _autoLaunch = true;
	private List<Login> _logins;
	private LoginAdapter _loginAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		SchoolsCollection.init(this);
		DB database = new DB(this);
		_logins = database.selectAll(database.getReadableDatabase());
		final Collator dkCollator = Collator.getInstance(new Locale("da", "DK"));
		if (dkCollator == null)
		{
			Collections.sort(_logins);
		}
		else
		{
			Collections.sort(_logins, new Comparator<Login>()
			{
				@Override
				public int compare(Login one, Login another)
				{
					return dkCollator.compare(one.getName(), another.getName());
				}
			});
		}
		database.close();

		Bundle bundle = getIntent().getExtras();

		if (bundle != null)
		{
			if (bundle.containsKey("noauto"))
			{
				_autoLaunch = false;
			}
		}

		setContentView(R.layout.loginlist);

		_loginAdapter = new LoginAdapter(this, R.layout.listitem, _logins);
		setListAdapter(_loginAdapter);

		registerForContextMenu(getListView());

		if (!showMessages())
		{
			handleAutoStart();
		}
	}

	private void handleAutoStart()
	{
		if (_logins.size() == 0)
		{
			startActivity(new Intent(this, EditLoginActivity.class));
		}
		else
			if (_logins.size() == 1 && _autoLaunch)
			{
				Intent intent = new Intent(this, WebControllerActivity.class);
				intent.putExtra("id", ((Login) _logins.get(0)).getId());
				startActivity(intent);
			}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.loginlist_actionbar_menu, menu);
		return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menu_info:
				startActivity(new Intent(this, AboutActivity.class));
				return true;
				
			case R.id.menu_new:
				startActivity(new Intent(this, EditLoginActivity.class));
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id)
	{
		Intent intent = new Intent(this, WebControllerActivity.class);
		intent.putExtra("id", ((Login) _logins.get(position)).getId());
		startActivity(intent);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, view, menuInfo);

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		String header = "";

		if (info.position >= 0)
		{
			if ((header = _logins.get(info.position).getName()) == null)
			{
				header = "";
			}
		}

		if (header.length() < 1)
		{
			header = "Ukendt";
		}

		menu.setHeaderTitle(header);
		menu.add(Menu.NONE, 0, 0, "Rediger");
		menu.add(Menu.NONE, 1, 1, "Slet");
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item)
	{
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		Login login = _logins.get(info.position);

		if (item.getItemId() == 0)
		{
			Intent intent = new Intent(this, EditLoginActivity.class);
			intent.putExtra("id", login.getId());
			startActivity(intent);
			return true;
		}
		
		deleteLogin(login);
		return true;
	}

	private void deleteLogin(final Login login)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage("Er det OK at slette " + login.getName() + "?").setCancelable(false).setPositiveButton("Ja", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				DB database = new DB(getApplicationContext());
				database.deleteChildById(database.getWritableDatabase(), login.getId());
				database.close();

				for (Login tmp : _logins)
				{
					if (tmp.getId() == login.getId())
					{
						_logins.remove(tmp);
						break;
					}
				}

				_loginAdapter.notifyDataSetChanged();

				if (_logins.size() == 0)
				{
					startActivity(new Intent(getApplicationContext(), EditLoginActivity.class));
				}
			}
		}).setNegativeButton("Nej", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				dialog.cancel();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	private boolean showMessages()
	{
		SharedPreferences preferences = getSharedPreferences("OSkoleMio", Context.MODE_PRIVATE);
		if (preferences != null)
		{
			int level = preferences.getInt("MESSAGES_LEVEL", 0);

			if (level < _CURRENT_MESSAGES_LEVEL)
			{
				popupMessages(preferences);
				return true;
			}
		}

		return false;
	}

	private void popupMessages(final SharedPreferences preferences)
	{
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.messagedialog);
		dialog.setTitle(getString(R.string.msgtitle));
		dialog.setCancelable(false);

		Button button = (Button) dialog.findViewById(R.id.ok);
		button.setOnClickListener(new OnClickListener()
		{
			public void onClick(View view)
			{
				SharedPreferences.Editor editor = preferences.edit();
				editor.putInt("MESSAGES_LEVEL", _CURRENT_MESSAGES_LEVEL);
				editor.commit();
				dialog.cancel();
				handleAutoStart();
			}
		});

		dialog.show();
	}
}
