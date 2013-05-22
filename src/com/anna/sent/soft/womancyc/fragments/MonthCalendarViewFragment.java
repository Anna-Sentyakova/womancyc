package com.anna.sent.soft.womancyc.fragments;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;

import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.adapters.MonthCalendarViewAdapter;
import com.anna.sent.soft.womancyc.data.CalendarData;
import com.anna.sent.soft.womancyc.data.DataKeeper;
import com.anna.sent.soft.womancyc.shared.Shared;
import com.anna.sent.soft.womancyc.utils.DateUtils;
import com.anna.sent.soft.womancyc.utils.OnSwipeTouchListener;
import com.anna.sent.soft.womancyc.utils.StateSaver;
import com.anna.sent.soft.womancyc.utils.StateSaverFragment;

public class MonthCalendarViewFragment extends StateSaverFragment implements
		OnClickListener, OnItemClickListener, OnItemLongClickListener,
		StateSaver, OnDateSetListener {
	private static final String TAG = "moo";
	private static final boolean DEBUG = true;

	private String wrapMsg(String msg) {
		return getClass().getSimpleName() + ": " + msg;
	}

	@SuppressWarnings("unused")
	private void log(String msg) {
		if (DEBUG) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	private void log(String msg, boolean debug) {
		if (DEBUG && debug) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	private Button currentMonth;
	private Button prevMonth;
	private Button nextMonth;
	private GridView calendarView;
	private MonthCalendarViewAdapter adapter;
	private static final String CURRENT_MONTH_TEMPLATE = "MMMM yyyy";
	private boolean mIsLargeLayout;

	public MonthCalendarViewFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.month_calendar_view, null);
		return v;
	}

	@Override
	public void setViews(Bundle savedInstanceState) {
		mIsLargeLayout = getResources().getBoolean(R.bool.isLargeLayout);

		adapter = new MonthCalendarViewAdapter(getActivity(), mDataKeeper);

		prevMonth = (Button) getActivity().findViewById(R.id.prevMonth);
		prevMonth.setOnClickListener(this);

		currentMonth = (Button) getActivity().findViewById(R.id.currentMonth);
		currentMonth.setOnClickListener(this);

		nextMonth = (Button) getActivity().findViewById(R.id.nextMonth);
		nextMonth.setOnClickListener(this);

		calendarView = (GridView) getActivity().findViewById(
				R.id.calendarGridView);
		calendarView.setAdapter(adapter);

		calendarView.setOnItemClickListener(this);
		calendarView.setOnItemLongClickListener(this);
		calendarView
				.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
					@Override
					public boolean onSwipeRight() {
						toPrevDate();
						return true;
					}

					@Override
					public boolean onSwipeLeft() {
						toNextDate();
						return true;
					}
				});

		currentMonth.setText(DateFormat.format(CURRENT_MONTH_TEMPLATE, adapter
				.getSelectedDate().getTime()));
		if (mIsLargeLayout) {
			showAsEmbeddedFragment(adapter.getSelectedDate());
		}
	}

	@Override
	public void restoreState(Bundle state) {
		Calendar dateToShow = (Calendar) state
				.getSerializable(Shared.DATE_TO_SHOW);
		log("restore " + DateUtils.toString(getActivity(), dateToShow), false);
		updateMonthCalendar(dateToShow);
	}

	@Override
	public void saveState(Bundle state) {
		log("save "
				+ DateUtils.toString(getActivity(), adapter.getSelectedDate()),
				false);
		state.putSerializable(Shared.DATE_TO_SHOW, adapter.getSelectedDate());
	}

	private void updateMonthCalendar(Calendar dateToShow) {
		adapter.setSelectedDate(dateToShow);
		currentMonth.setText(DateFormat.format(CURRENT_MONTH_TEMPLATE,
				dateToShow.getTime()));
		if (mIsLargeLayout) {
			showAsEmbeddedFragment(adapter.getSelectedDate());
		}
	}

	private void toPrevDate() {
		Calendar dateToShow = (Calendar) adapter.getSelectedDate().clone();
		dateToShow.set(Calendar.DAY_OF_MONTH, 1);
		dateToShow.add(Calendar.MONTH, -1);
		updateMonthCalendar(dateToShow);
	}

	private void toNextDate() {
		Calendar dateToShow = (Calendar) adapter.getSelectedDate().clone();
		dateToShow.set(Calendar.DAY_OF_MONTH, 1);
		dateToShow.add(Calendar.MONTH, 1);
		updateMonthCalendar(dateToShow);
	}

	@Override
	public void onClick(View v) {
		if (v == prevMonth) {
			toPrevDate();
		} else if (v == nextMonth) {
			toNextDate();
		} else if (v == currentMonth) {
			Bundle args = new Bundle();
			args.putSerializable(Shared.DATE_TO_SHOW, adapter.getSelectedDate());
			DatePickerDialogFragment dialog = new DatePickerDialogFragment();
			dialog.setArguments(args);
			dialog.setOnDateSetListener(this);
			dialog.show(getFragmentManager(),
					DatePickerDialogFragment.class.getSimpleName());
		}
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		Calendar dateToShow = Calendar.getInstance();
		dateToShow.set(year, month, day);
		updateMonthCalendar(dateToShow);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Object item = adapter.getItem(position);
		if (item != null) {
			Calendar date = (Calendar) item;

			updateMonthCalendar(date);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		Object item = adapter.getItem(position);
		if (item != null) {
			Calendar date = (Calendar) item;

			String title = DateUtils.toString(getActivity(), date);
			Bundle args = new Bundle();
			args.putString(Shared.DATE_TO_SHOW, title);

			if (!mIsLargeLayout) {
				showAsDialogFragment(date);
			}
		}

		return true;
	}

	public void showAsDialogFragment(Calendar date) {
		FragmentManager fragmentManager = getFragmentManager();
		createEditorFragment(date).show(fragmentManager,
				CalendarItemEditorDialogFragment.class.getSimpleName());
	}

	public void showAsEmbeddedFragment(Calendar date) {
		FragmentManager fragmentManager = getFragmentManager();

		Fragment details = fragmentManager.findFragmentById(R.id.editor);
		if (details != null) {
			fragmentManager.beginTransaction().remove(details).commit();
		}

		fragmentManager.beginTransaction()
				.add(R.id.editor, createEditorFragment(date)).commit();
	}

	private DialogFragment createEditorFragment(Calendar date) {
		int index = new DateUtils().indexOf(mDataKeeper.getData(), date);
		CalendarData value;
		if (index >= 0) {
			value = mDataKeeper.getData().get(index);
		} else {
			value = new CalendarData(date);
		}

		Bundle args = new Bundle();
		args.putSerializable(CalendarData.class.getSimpleName(), value);

		CalendarItemEditorDialogFragment newFragment = new CalendarItemEditorDialogFragment();
		newFragment.setArguments(args);

		return newFragment;
	}

	private DataKeeper mDataKeeper = null;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof DataKeeper) {
			mDataKeeper = (DataKeeper) activity;
		}
	}

	public void update() {
		adapter.notifyDataSetChanged();
	}
}
