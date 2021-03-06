package at.qurps.noefinderlein.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A list fragment representing a list of Locations. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 *
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class Fragment_LocationList extends ListFragment implements DialogFragment_FilterList.NoticeDialogListener{

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
    public static final String TAG = "Fragment_LocationList";
	private static final String STATE_ACTIVATED_POSITION = "activated_position";
    public static final String ARG_ITEM_JAHR = "item_jahr" ;
	public static final String ARG_MTWOPANE ="mTwoPane" ;
	public static final String ARG_ISREGION ="isRegion" ;
    public static final String ARG_REGION ="item_Region" ;
	public static final String ARG_REGION_NAME ="item_Region_name" ;
    public static final String PREF_SORT_STRING ="sort_by" ;

    public static final int SORT_BY_NAME = 1;
    public static final int SORT_BY_NOECNUM = 2;
	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;
    private SearchView searchView;
	private DestinationsDB db;
	private  Context mContext;
	public boolean mIsRegion=false;
    private int mRegion;
	private String mRegionName="";
	List<DB_Location_NoeC> listItems=new ArrayList<DB_Location_NoeC>();
    ArrayAdapter_Mainlist adapter;
	private boolean mTwoPane;
    public ViewGroup mcontainer;
    public int mRegionItemNummer;
    public int mRegionItemJahr;
	private Bundle msavedInstanceState;
	private View rootView;
	private boolean useOpenData;

	private final BroadcastReceiver myBRDataUpd=new DataUpdate();


	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected_Fragment_LocationList(int i, int year);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected_Fragment_LocationList(int id, int year) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
    public Fragment_LocationList() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mContext=getActivity();
        mTwoPane = getResources().getBoolean(R.bool.has_two_panes);
		this.db= new DestinationsDB(mContext);
        SharedPreferences sharedPrefAct = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        this.useOpenData = sharedPrefAct.getBoolean(Activity_Settings.KEY_PREF_LOAD_OPEN_DATA, true);
		//getListView().
		//TODO Fastscroll enable
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_location_list, container, false);
        //container.removeAllViews();
        mcontainer=container;
        ChangeView(getArguments());
        return rootView;
    }
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
		msavedInstanceState = savedInstanceState;
        Log.d(TAG,"--- hier "+ String.valueOf(savedInstanceState));
        if (savedInstanceState != null) {
            //Restore the fragment's state here
            mIsRegion=savedInstanceState.getBoolean(ARG_ISREGION);
            mRegion=savedInstanceState.getInt(ARG_REGION);
            mTwoPane=savedInstanceState.getBoolean(ARG_MTWOPANE);
            mRegionItemJahr=savedInstanceState.getInt(ARG_ITEM_JAHR);
        }
        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    public void ChangeView(Bundle arguments) {
		if (arguments != null) {
			if (arguments.containsKey(ARG_ISREGION)) {
				mIsRegion = arguments.getBoolean(ARG_ISREGION);
			}
            if (arguments.containsKey(ARG_REGION)) {
                mRegion = arguments.getInt(ARG_REGION);
            }
			if (arguments.containsKey(ARG_MTWOPANE)) {
				mTwoPane = arguments.getBoolean(ARG_MTWOPANE);

			}
			if (arguments.containsKey(ARG_REGION_NAME)) {
				mRegionName = arguments.getString(ARG_REGION_NAME);

			}
			if (arguments.containsKey(ARG_ITEM_JAHR)) {
				mRegionItemJahr = arguments.getInt(ARG_ITEM_JAHR);
			}
			Log.d(TAG, "oncreate hier" + String.valueOf(mTwoPane));
		}

        final TextView txt = (TextView)rootView.findViewById(R.id.mainlist_regiontext);

        if (mRegionName != "" && txt != null) {
            txt.setText(mRegionName);
            txt.setVisibility(View.VISIBLE);
		}else{
            txt.setVisibility(View.GONE);
        }
		dbContentChanged();
	}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        // Activities containing this fragment must implement its callbacks.
        if (!(context instanceof Callbacks)) {
            throw new IllegalStateException(
                    "Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) context;
    }
	@Override
	public void onResume() {
		super.onResume();
		mContext.registerReceiver(myBRDataUpd, new IntentFilter("dataupdate"));
		Date cDate = new Date();
		String fDate = new SimpleDateFormat("yyyy-MM-dd").format(cDate);
		SharedPreferences sharedPrefAct = PreferenceManager.getDefaultSharedPreferences(this.getContext());
		boolean compUseOpenData = sharedPrefAct.getBoolean(Activity_Settings.KEY_PREF_LOAD_OPEN_DATA, true);
		if(!fDate.equals(this.adapter.getactiveDay()) || compUseOpenData != this.useOpenData){
			// daychange - reload adapter
			dbContentChanged();
            this.useOpenData = compUseOpenData;
		}
		//dbContentChanged();
		//adapter.notifyDataSetChanged();
	}
	@Override
	public void onPause() {
		mContext.unregisterReceiver(myBRDataUpd);
		super.onPause();
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mCallbacks.onItemSelected_Fragment_LocationList(adapter.getIdtoPosition(position), adapter.getJahrtoPosition(position));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
        Log.d(TAG,"---hier");
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
        if(mRegionItemJahr!=-1){
            outState.putInt(ARG_ITEM_JAHR, mRegionItemJahr);
        }

        outState.putBoolean(ARG_ISREGION, mIsRegion);
        outState.putBoolean(ARG_MTWOPANE, mTwoPane);
        if(mRegion!=-1){
            outState.putInt(ARG_REGION, mRegion);
        }
	}
	public class DataUpdate extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			int id = intent.getIntExtra("id",-1);
			int year = intent.getIntExtra("year",-1);
			if(year != -1 && year != mRegionItemJahr){
				mRegionItemJahr = year;
			}
			dbContentChanged();
			adapter.notifyDataSetChanged();
		}
	}
	public void dbContentChanged(){

		if (mIsRegion)
		{
			listItems = db.getAllMenuLocationstoRegion(mRegion,mRegionItemJahr);
		}
		else
		{
			Log.d(TAG," da 2 ");
			listItems = db.getAllMenuLocations(mRegionItemJahr);
		}
		//Log.d(TAG," --- size "+String.valueOf(listItems.get(0).getName()));
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        int sortByWhat = sharedPref.getInt(PREF_SORT_STRING,0);
		adapter=new ArrayAdapter_Mainlist(mContext,listItems);
        filterWithNewData(getFilterliste(), getOpenFilter());
        sortBy(sortByWhat);
		setListAdapter(adapter);
	}
	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}
	public void filter_list(String filterquery)
	{
        if(adapter != null) {
            adapter.filterwithstring(filterquery);
        }
	}
	@Override
	public void onPrepareOptionsMenu(Menu menu)
	{
		MenuItem filtericon=menu.findItem(R.id.actionb_filter_list);
		Drawable drawable;
		if(isFilterSet())
		{
			drawable = ContextCompat.getDrawable(mContext,R.drawable.ic_filter_full);
		}
		else
		{
			drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_filter);
		}
		drawable = DrawableCompat.wrap(drawable);
		DrawableCompat.setTint(drawable, ContextCompat.getColor(mContext, R.color.noecard_white));
		filtericon.setIcon(drawable);
	}

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        inflater.inflate(R.menu.list_options, menu);
        this.searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search_list_actionbar));
        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                Log.d(TAG, "hier" + String.valueOf(mTwoPane));
                ((Fragment_LocationList) getActivity().getSupportFragmentManager()
                    .findFragmentByTag(TAG))
                    .filter_list(newText);
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                // this is your adapter that will be filtered
                //Toast.makeText(getApplicationContext(),"hier2", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
		Util.colorMenuItems(mContext, menu,R.id.search_list_actionbar, R.color.noecard_white);
        Util.colorMenuItems(mContext, menu,R.id.actionb_sort_list, R.color.noecard_white);
		Util.colorMenuItems(mContext, menu,R.id.actionb_filter_list, R.color.noecard_white);
		Util.colorMenuItems(mContext, menu,R.id.actionb_show_current_in_map, R.color.noecard_white);

        super.onCreateOptionsMenu(menu,inflater);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.actionb_filter_list:
            DialogFragment_FilterList newFragment = new DialogFragment_FilterList();
			newFragment.setListener(Fragment_LocationList.this);
		    newFragment.show(Fragment_LocationList.this.getActivity().getSupportFragmentManager(), "filter");
			return true;
        case R.id.actionb_sort_list:
            AlertDialog.Builder SortDBuilder = new AlertDialog.Builder(mContext);
            final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SortDBuilder.setCancelable(true);
            SortDBuilder.setTitle(R.string.sortby);
            SortDBuilder.setPositiveButton(R.string.sort_name, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    sharedPref.edit().putInt(PREF_SORT_STRING, SORT_BY_NAME).apply();
                    sortBy(SORT_BY_NAME);
                }
            });
            SortDBuilder.setNegativeButton(R.string.sort_noecnum, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    sharedPref.edit().putInt(PREF_SORT_STRING, SORT_BY_NOECNUM).apply();
                    sortBy(SORT_BY_NOECNUM);
                }
            });
            SortDBuilder.create().show();

            return true;
		case R.id.actionb_show_current_in_map:

            mTwoPane = getResources().getBoolean(R.bool.has_two_panes);

            Bundle arguments = new Bundle();
            arguments.putString(Activity_Map.ARG_ITEM_IDS, adapter.getItemsString());
            arguments.putInt(Activity_Map.ARG_ITEM_YEAR, mRegionItemJahr);
            arguments.putBoolean(Activity_Map.ARG_MTWOPANE, mTwoPane);

            Intent intent = new Intent(mContext, Activity_Map.class);
            intent.putExtras(arguments);
            startActivity(intent);
            return true;

		}
		return super.onOptionsItemSelected(item);
		
	}
	@Override
    public void onDialogPositiveClick(boolean[] filterlist, boolean openFilter) {
        // User touched the dialog's positive button
        dbContentChanged();
        filterWithNewData(filterlist, openFilter);
    }
    public void filterWithNewData(boolean[] filterlist, boolean openFilter) {
        // User touched the dialog's positive button
        //dbContentChanged();
        ActivityCompat.invalidateOptionsMenu(getActivity());
        adapter.filterwithtyp(filterlist, openFilter);
    }
    private void sortBy(int what){
        adapter.sortBy(what);
    }
	private boolean isFilterSet(){
		boolean returnval=true;
		SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
		String[] dest_typ_list = getResources().getStringArray(R.array.filter_typ_list);
        boolean noneset = true;
		for(int i=0;i<dest_typ_list.length;i++)
		{
			/*if(!sharedPref.getBoolean(dest_typ_list[i], true))
			{
				returnval=true;
			}
            else {*/
            if(sharedPref.getBoolean(dest_typ_list[i], false)){
                noneset = false;
            }
		}
        boolean filterOpen = sharedPref.getBoolean(getResources().getString(R.string.filter_open_sett), false);
        SharedPreferences sharedPrefAct = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        this.useOpenData = sharedPrefAct.getBoolean(Activity_Settings.KEY_PREF_LOAD_OPEN_DATA, true);
        if(filterOpen && this.useOpenData) {
            noneset = false;
        }
        String dayString = "";
        int year = Util.getDatePreferencesYear(getActivity());
        int month = Util.getDatePreferencesMonth(getActivity());
        int day = Util.getDatePreferencesDay(getActivity());

        Calendar currentDay = Calendar.getInstance();
        Calendar setDay = Calendar.getInstance();
        setDay.set(year, month, day);
        if((year == 0 && day == 0) || (year != 0 && day != 0 && (setDay.before(currentDay) || setDay.equals(currentDay)))) {
            year = currentDay.get(Calendar.YEAR);
            month = currentDay.get(Calendar.MONTH);
            day = currentDay.get(Calendar.DAY_OF_MONTH);
        }

        setDay.set(year, month, day);
        if(setDay.after(currentDay)) {
            noneset = false;
            dayString = " " + String.valueOf(day) + "." + String.valueOf(month + 1) + "." + String.valueOf(year);
        }


        if(noneset){
            returnval=false;
        }
        showFilterActive(returnval, dayString);
		return returnval;
	}
	private void showFilterActive(boolean filtered, String dayString){
		final TextView txt = (TextView)rootView.findViewById(R.id.mainlist_filtertext);
        txt.setText(getResources().getString(R.string.filteractive) + dayString);
		if(filtered && txt.getVisibility()==View.GONE){
            txt.setVisibility(View.VISIBLE);
			final Animation animLinearDown = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_top);
			txt.startAnimation(animLinearDown);
		}else if(!filtered && txt.getVisibility()==View.VISIBLE){
			final Animation animLinearUp = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_top);
            animLinearUp.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {}
                public void onAnimationRepeat(Animation animation) {}
                public void onAnimationEnd(Animation animation) {
                    txt.setVisibility(View.GONE);
                }
            });
			txt.startAnimation(animLinearUp);

		}
	}
	private boolean[] getFilterliste(){
        String[] dest_typ_list = getResources().getStringArray(R.array.filter_typ_list);
		boolean[] returnbool=new boolean[dest_typ_list.length];
		SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

		for(int i=0;i<dest_typ_list.length;i++)
		{
			returnbool[i]=sharedPref.getBoolean(dest_typ_list[i], false);
		}
		return returnbool;
	}
    private boolean getOpenFilter(){
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getBoolean(getResources().getString(R.string.filter_open_sett), false);
    }
}
