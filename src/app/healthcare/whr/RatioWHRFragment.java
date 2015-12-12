package app.healthcare.whr;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import app.dto.RatioWHRDTO;
import app.healthcare.Constants;
import app.healthcare.R;

import com.gc.materialdesign.widgets.Dialog;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

@SuppressLint("RtlHardcoded")
public class RatioWHRFragment extends Fragment {

	CheckBox cbMale;
	CheckBox cbFeMale;
	EditText tbxCe;
	EditText tbxCm;
	TextView tbxImpact;
	TextView tbxResult;
	Button btnReinphut;
	Button btnCalculateWHR;
	List<RatioWHRDTO> allData = new ArrayList<RatioWHRDTO>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_ratio_whr,
				container, false);
		initView(rootView);
		try {
			ParseQuery<RatioWHRDTO> query = ParseQuery.getQuery("RatioWHRDTO");
			query.findInBackground(new FindCallback<RatioWHRDTO>() {
				@Override
				public void done(List<RatioWHRDTO> datas, ParseException e) {
					if (datas != null) {
						allData = datas;
					}
				}
			});
		} catch (Exception e) {
		}
		return rootView;
	}

	public void initView(View rootView) {
		cbMale = (CheckBox) rootView.findViewById(R.id.cbMale);
		cbMale.setOnCheckedChangeListener(listener);
		cbFeMale = (CheckBox) rootView.findViewById(R.id.cbFeMale);
		cbFeMale.setChecked(true);
		cbFeMale.setOnCheckedChangeListener(listener);
		tbxCe = (EditText) rootView.findViewById(R.id.tbxCe);
		tbxCm = (EditText) rootView.findViewById(R.id.tbxCm);
		btnCalculateWHR = (Button) rootView.findViewById(R.id.btnCalculateWHR);
		btnCalculateWHR.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("ShowToast")
			public void onClick(View v) {
				if (tbxCe.getText().length() == 0) {
					Toast.makeText(getActivity(), "Nhập thông số vòng eo",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (tbxCm.getText().length() == 0) {
					Toast.makeText(getActivity(), "Nhập thông số vòng mông",
							Toast.LENGTH_SHORT).show();
					return;
				}
				calculateWHR();
			}
		});
		btnReinphut = (Button) rootView.findViewById(R.id.btnReinput);
		btnReinphut.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				reInput();
			}
		});
		tbxResult = (TextView) rootView.findViewById(R.id.tbxResultWHR);
		tbxImpact = (TextView) rootView.findViewById(R.id.tbxImpact);
		if (Constants.getInstance().sex == 0) {
			cbMale.setChecked(true);
			cbFeMale.setChecked(false);
			cbMale.setEnabled(false);
			cbFeMale.setEnabled(false);
		} else if (Constants.getInstance().sex == 1) {
			cbMale.setChecked(false);
			cbFeMale.setChecked(true);
			cbMale.setEnabled(false);
			cbFeMale.setEnabled(false);
		}
	}

	protected void calculateWHR() {
		Double ce = Double.parseDouble(tbxCe.getText().toString());
		Double cm = Double.parseDouble(tbxCm.getText().toString());
		String result;
		Double ratioWHR = ce / cm;
		if (cbMale.isChecked()) {
			if (ratioWHR <= 0.9) {
				result = "Sức khỏe tốt";
			} else if (ratioWHR > 0.9 && ratioWHR <= 0.95) {
				result = "Ít nguy hiểm";
			} else if (ratioWHR > 0.95 && ratioWHR < 1) {
				result = "Mức độ nguy hiểm trung bình";
			} else {
				result = "Rất nguy hiểm";
			}
		} else {
			cbFeMale.setChecked(true);
			if (ratioWHR <= 0.7) {
				result = "Sức khỏe tốt";
			} else if (ratioWHR > 0.7 && ratioWHR <= 0.8) {
				result = "Ít nguy hiểm";
			} else if (ratioWHR > 0.8 && ratioWHR < 0.85) {
				result = "Nguy hiểm";
			} else {
				result = "Rất nguy hiểm";
			}
		}
		ratioWHR = (double) (Math.round((double) ratioWHR * 10) / (double) 10);
		tbxResult.setText(String.valueOf(ratioWHR));
		tbxImpact.setText(result);
		RatioWHRDTO dto = new RatioWHRDTO();
		dto.setRatio(ratioWHR);
		dto.setStatus(result);
		int id = 1;
		if (allData.size() > 0) {
			id = allData.size() + 1;
		}
		dto.setRatioWHRId(id);
		Constants.getInstance().getTime().setToNow();
		int date = Constants.getInstance().getTime().monthDay;
		int month = Constants.getInstance().getTime().month + 1;
		int year = Constants.getInstance().getTime().year;
		dto.setTime(String.valueOf(Constants.getInstance().getTime().hour)
				+ ":"
				+ String.valueOf(Constants.getInstance().getTime().minute)
				+ ":"
				+ String.valueOf(Constants.getInstance().getTime().second));
		dto.setDate(String.valueOf(date) + "/" + String.valueOf(month) + "/"
				+ String.valueOf(year) + "");
		final Double ratioToView = ratioWHR;
		final String resultToView = result;
		final Intent historyWHR = new Intent(getActivity(), HistoryWHR.class);
		dto.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException ex) {
				if (ex == null) {
					final Dialog dialog = new Dialog(getActivity(), "Chỉ số WHR",
							"Chỉ số WHR của bạn là: " + String.valueOf(ratioToView) + "\n"
									+ resultToView, app.healthcare.R.drawable.whr_icon);
					dialog.show();
					dialog.getButtonAccept().setOnClickListener(
							new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									startActivity(historyWHR);
									dialog.dismiss();
								}
							});

				} else {
					Dialog dialog = new Dialog(getActivity(), "Chỉ số WHR",
							"Có lỗi xảy ra!",
							app.healthcare.R.drawable.whr_icon);
					dialog.show();
				}
			}
		});
	}

	public void reInput() {
		tbxCe.setText("");
		tbxCm.setText("");
		tbxImpact.setText("Mức độ ảnh hưởng");
		tbxResult.setText("WHR");
	}

	private OnCheckedChangeListener listener = new OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
			if (isChecked) {
				switch (arg0.getId()) {
				case R.id.cbFeMale:
					cbFeMale.setChecked(true);
					cbMale.setChecked(false);
					break;
				case R.id.cbMale:
					cbMale.setChecked(true);
					cbFeMale.setChecked(false);
					break;
				}
			}
		}
	};
}