package info.amnipar.cvsu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
	public final static String EXTRA_MESSAGE = "info.amnipar.cvsu.MESSAGE";
	public final static String CVSU_MESSAGE = "info.amnipar.cvsu.CVSU";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
    	Intent intent = new Intent(this, DisplayMessageActivity.class);
    	EditText editText = (EditText) findViewById(R.id.edit_message);
    	String message = editText.getText().toString();
    	intent.putExtra(EXTRA_MESSAGE, message);
    	startActivity(intent);
    }
    public void sendCVSU(View view) {
    	Intent intent = new Intent(this, CVSUActivity.class);
    	EditText editText = (EditText) findViewById(R.id.edit_cvsu);
    	String message = editText.getText().toString();
    	intent.putExtra(CVSU_MESSAGE, message);
    	startActivity(intent);
    }
}