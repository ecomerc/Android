package dk.schau.OSkoleMio.activities;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import dk.schau.OSkoleMio.DB;
import dk.schau.OSkoleMio.R;
import dk.schau.OSkoleMio.activities.AboutActivity;
import dk.schau.OSkoleMio.vos.Login;
import android.view.View;
import android.widget.EditText;

public class EditLoginActivity extends SherlockFragmentActivity
{
	private int _id = -1;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editlogin);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null)
		{
			Login login = null;

			if (bundle.containsKey("id"))
			{
				_id = bundle.getInt("id");

				DB database = new DB(this);
				login = database.getLoginById(database.getReadableDatabase(), _id);
				database.close();
			}
			else if (bundle.containsKey("child"))
			{
				login = new Login(bundle.getString("child"));

				_id = login.getId();
			}

			((EditText) findViewById(R.id.tvname)).setText(login.getName());
			((EditText) findViewById(R.id.tvurl)).setText(login.getUrl());
			((EditText) findViewById(R.id.tvlogin)).setText(login.getLogin());
			((EditText) findViewById(R.id.tvpassword)).setText(login.getPassword());
		}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.editlogin_actionbar_menu, menu);
		return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				finish();
				return true;

			case R.id.menu_info:
				startActivity(new Intent(this, AboutActivity.class));
				return true;
				
			case R.id.menu_done:
				saveEntry();
				return true;
		}

		return super.onOptionsItemSelected(item);  	
	}

	private void saveEntry()
	{
		DB database = new DB(this);

		Login login = new Login(_id, getEditText(R.id.tvname), getEditText(R.id.tvurl), getEditText(R.id.tvlogin), getEditText(R.id.tvpassword));

		database.saveLogin(database.getWritableDatabase(), login);
		database.close();

		Intent intent = new Intent(this, OSkoleMioActivity.class);
		intent.putExtra("noauto", "true");
		startActivity(intent);
	}

	private String getEditText(int guiId)
	{
		String text = ((EditText) findViewById(guiId)).getText().toString();

		if (text == null || text.length() == 0)
		{
			return "";
		}

		return text.trim();
	}

	public void buttonSchoolClicked(View view)
	{
		Login login = new Login(_id, getEditText(R.id.tvname), getEditText(R.id.tvurl), getEditText(R.id.tvlogin), getEditText(R.id.tvpassword));

		Intent intent = new Intent(this, SchoolPickerActivity.class);
		intent.putExtra("child", login.toString());
		startActivity(intent);
	}
}
