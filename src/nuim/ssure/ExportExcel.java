package nuim.ssure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

public class ExportExcel {
	dataHelper dbHelper;

	Context context;

	protected static final File DATABASE_DIRECTORY = new File(
			Environment.DIRECTORY_DOWNLOADS, "Export");

	public ExportExcel(Context context) {
		this.context = context;
	}

	public Boolean exportDataToCSV(String outFileName, char Type) {

		outFileName = Type + "_" + outFileName + ".csv";
		
		Log.e("excel", "in exportDatabasecsv()");
		Boolean returnCode = false;

		String csvHeader = "";
		String csvValues = "";

		try {

			dbHelper = new dataHelper(context);
			dbHelper.open();

			/*if (!DATABASE_DIRECTORY.exists()) {
				Log.e("excel", "directory does not exist");
				if(!DATABASE_DIRECTORY.mkdir()){
					Log.e("Directory", "error");
				}
			}*/
			Log.e("export fun:file name", outFileName);
			File outFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), outFileName);
			Log.e("File", "created");
			FileWriter fileWriter = new FileWriter(outFile);
			Log.e("after FileWriter :file name", outFile.toString());
			BufferedWriter out = new BufferedWriter(fileWriter);
			
			Cursor cursor;
			if (Type=='A') { cursor = dbHelper.getAcc(); }
			else { cursor = dbHelper.getGra(); }
			// Log.e("excel", "cursor col count" + cursor.getCount());

			int col_count = cursor.getColumnCount();
			Log.e("col count", ""+col_count);
			csvHeader += "\"" + "ID" + "\",";
			csvHeader += "\"" + "X" + "\",";
			csvHeader += "\"" + "Y" + "\",";
			csvHeader += "\"" + "Z" + "\",";
			csvHeader += "\"" + "TIMESTAMP" + "\",";
			csvHeader += "\n";

			if (cursor != null) {
				Log.e("Cursor", Type + " exists");
				out.write(csvHeader);
				while (!cursor.isAfterLast()) {
					// csvValues = Long.toString(cursor.getLong(0)) + ",";

					csvValues = cursor.getString(0) + ","; // ID
					csvValues += cursor.getString(1) + ","; // X
					csvValues += cursor.getString(2) + ","; // Y
					csvValues += cursor.getString(3) + ","; // Z
					csvValues += cursor.getString(4) + ",\n"; // Timestamp
					

					out.write(csvValues);
					cursor.moveToNext();
				}
				// Log.e("excel", "csvValues are:-  " + csvValues);

			}
			out.close();
			cursor.close();
			returnCode = true;
		} catch (Exception e) {
			returnCode = false;
			Log.e("Exception", e.getMessage());
		}

		dbHelper.close();
		
		Log.e("Message", "End Export");
		return returnCode;
	}

}
