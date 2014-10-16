package au.radsoft.textintents;

import android.app.Activity;
import android.content.Intent;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.net.URLEncoder;

public class TextIntents extends Activity implements AdapterView.OnItemClickListener
{
    EditText text_;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        //boolean customTitle = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main);
        //if (customTitle)
        //{
            //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        //}
        
        text_ = (EditText) findViewById(R.id.text);
        final ListView list = (ListView) findViewById(R.id.list);
        
        Intent intent = getIntent();
        if (intent != null && intent.getAction() == Intent.ACTION_SEND)
        {
            text_.setText(intent.getCharSequenceExtra(Intent.EXTRA_TEXT));
        }
        else
        {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            if (clipboard.hasPrimaryClip())
            {
                ClipData data = clipboard.getPrimaryClip();
                ClipData.Item item = data.getItemAt(0); // TODO Check all items
                text_.setText(item.coerceToText(this).toString());
            }
        }
        
        ChoiceAdapter.init(getPreferences(MODE_PRIVATE));
        final ChoiceAdapter adapter = new ChoiceAdapter(getLayoutInflater(), getPreferences(MODE_PRIVATE));
        list.setAdapter(adapter);
		list.setOnItemClickListener(this);
        registerForContextMenu(list);
		
		setFinishOnTouchOutside(true);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
        case R.id.action_add:
            toast("add");
            break;
            
        default:
            return false;
        }
        
        return true;
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item, menu);

        super.onCreateContextMenu(menu, view, menuInfo);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId())
        {
        case R.id.action_edit:
            toast("edit " + info.id);
            break;
            
        case R.id.action_delete:
            toast("delete");
            break;
            
        default:
            return false;
        }
        
        return true;
    }
    
    @Override // AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        open(view.getTag().toString());
    }
    
    void open(String s)
    {
        CharSequence text = text_.getText();
        if (text != null)
            s = s.replace("[text]", URLEncoder.encode(text.toString()));
            
        Uri uri = Uri.parse(s);
        toast(s);
        
        Intent i = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(i);
    }
    
    void toast(String msg)
    {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.show();
    }
}
