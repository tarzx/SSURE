package nuim.ssure;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ExportAct extends Activity implements OnClickListener{
	dataHelper databaseConnector;
	Context context;
	Button exportBtn;
	ExportExcel ExportExcel;
	
	
	String exportFileName="ExportExcel.csv";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initializeUI();
	}

	public void initializeUI()
	{
		context=this;
		databaseConnector=new dataHelper(context);
		ExportExcel=new ExportExcel(context);
		exportBtn=(Button) findViewById(R.id.b_export);
		exportBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.b_export:
			try {
				ExportExcel.exportDataToCSV(exportFileName, 'A');
				ExportExcel.exportDataToCSV(exportFileName, 'G');
				Toast.makeText(context, "File exported successfully", 1).show();
			} catch (Exception e) {
				Toast.makeText(context, "Please,Import data first", 1).show();
			
			}
			break;
		default:
			break;
		}
	}

}
