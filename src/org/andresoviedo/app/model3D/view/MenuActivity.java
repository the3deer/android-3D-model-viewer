package org.andresoviedo.app.model3D.view;
//package org.andresoviedo.app.model3D;
//
//import java.io.File;
//
//import android.app.ListActivity;
//import android.content.ActivityNotFoundException;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import com.ipaulpro.afilechooser.utils.FileUtils;
//
//public class MenuActivity extends ListActivity {
//
//	private static final int REQUEST_CODE = 1234;
//	private static final String CHOOSER_TITLE = "Select a file";
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_menu);
//		setListAdapter(new ArrayAdapter<String>(this,
//				R.layout.activity_menu_item, getResources().getStringArray(
//						R.array.menu_items)));
//	}
//
//	@Override
//	public void onListItemClick(ListView l, View v, int position, long id) {
//		String selectedItem = (String) getListView()
//				.getItemAtPosition(position);
//		// Toast.makeText(getApplicationContext(),
//		// "Click ListItem '" + selectedItem + "'", Toast.LENGTH_LONG)
//		// .show();
//		Intent target = FileUtils.createGetContentIntent();
//		Intent intent = Intent.createChooser(target, CHOOSER_TITLE);
//		try {
//			startActivityForResult(intent, REQUEST_CODE);
//		} catch (ActivityNotFoundException e) {
//			// The reason for the existence of aFileChooser
//		}
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		switch (requestCode) {
//		case REQUEST_CODE:
//			if (resultCode == RESULT_OK) {
//				// The URI of the selected file
//				final Uri uri = data.getData();
//				// Create a File from this Uri
//				File file = FileUtils.getFile(uri);
//				// Toast.makeText(getApplicationContext(),
//				// "Selected file '" + file.getName() + "'",
//				// Toast.LENGTH_LONG).show();
//				startActivity(new Intent(getApplicationContext(),
//						ModelActivity.class));
//			}
//		}
//	}
//}
