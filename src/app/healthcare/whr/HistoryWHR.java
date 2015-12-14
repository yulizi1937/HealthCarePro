package app.healthcare.whr;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ListView;
import app.healthcare.Constants;
import app.healthcare.R;

import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph;

public class HistoryWHR extends Activity {
	BarGraph bg;

	static final String KEY_ID = "ID";
	static final String KEY_RATE = "ratio";
	static final String KEY_TIME = "time";
	static final String KEY_DATE = "date";
	static final String KEY_STATUS = "status";

	ListView list;
	HistoryWHRAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_whr);
		initChart();
		initList();
	}

	private void initList() {
		ArrayList<HashMap<String, String>> ratiopWHRList = new ArrayList<HashMap<String, String>>();
		// looping through all song nodes &lt;song&gt;
		for (int i = 0; i < Constants.getInstance().listDataWHR.size(); i++) {
			// creating new HashMap
			HashMap<String, String> map = new HashMap<String, String>();
			// adding each child node to HashMap key =&gt; value
			map.put(KEY_ID, String.valueOf(Constants.getInstance().listDataWHR
					.get(i).getRatioWHRId()));
			map.put(KEY_TIME, Constants.getInstance().listDataWHR.get(i)
					.getTime());
			map.put(KEY_DATE, Constants.getInstance().listDataWHR.get(i)
					.getDate());
			map.put(KEY_STATUS, Constants.getInstance().listDataWHR.get(i)
					.getStatus());
			map.put(KEY_RATE,
					String.valueOf(Constants.getInstance().listDataWHR.get(i)
							.getRatio()));
			// adding HashList to ArrayList
			ratiopWHRList.add(map);
		}
		list = (ListView) findViewById(R.id.list_whr);
		// Getting adapter by passing xml data ArrayList
		adapter = new HistoryWHRAdapter(this, ratiopWHRList);
		list.setAdapter(adapter);
	}

	private void initChart() {
		final Resources resources = getResources();
		ArrayList<Bar> aBars = new ArrayList<Bar>();
		Bar bar;

		for (int i = Constants.getInstance().listDataWHR.size() - 1; i >= 0; i--) {
			bar = new Bar();
			bar.setColor(resources.getColor(R.color.red));
			bar.setSelectedColor(resources.getColor(R.color.transparent_orange));
			String dateMonth = Constants.getInstance().listDataWHR.get(i)
					.getDate();
			String[] sub = dateMonth.split("/");
			bar.setName(sub[0] + "/" + sub[1]);
			bar.setValue(Float.parseFloat(String.valueOf(Constants
					.getInstance().listDataWHR.get(i).getRatio())));
			bar.setValueString(String.valueOf(Constants.getInstance().listDataWHR
					.get(i).getRatio()));
			aBars.add(bar);
			if ((Constants.getInstance().listDataWHR.size() - i) >= 10) {
				break;
			}
		}

		final BarGraph barGraph = (BarGraph) findViewById(R.id.chart_history_whr);
		bg = barGraph;
		barGraph.setBars(aBars);
	}
}