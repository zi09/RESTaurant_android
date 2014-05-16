package com.uliamar.restaurant.app.controller;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.uliamar.restaurant.app.Bus.BusProvider;
import com.uliamar.restaurant.app.Bus.GetInvitationList;
import com.uliamar.restaurant.app.Bus.GetLocalRestaurantEvent;
import com.uliamar.restaurant.app.Bus.InvitationListReceivedEvent;
import com.uliamar.restaurant.app.R;
import com.uliamar.restaurant.app.model.Invitation;

import java.util.List;


/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the
 * interface.
 */
public class EventListFragment extends ListFragment {

    private boolean requestPending = false;
    private MySimpleArrayAdapter adapter;
    public final String TAG = "EventListFragment";

//    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types of parameters
    public static EventListFragment newInstance() {
        EventListFragment fragment = new EventListFragment();

        return fragment;
    }




    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Invitation[] invitations = new Invitation[0];
        adapter = new MySimpleArrayAdapter(getActivity(), invitations);
        setListAdapter(adapter);

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.get().register(this);
        onAskRefresh();
    }


    private void onAskRefresh() {
        if (!requestPending) {
            BusProvider.get().post(new GetInvitationList());
            Log.d(TAG, "Send GetInvitationList");
            requestPending = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.get().unregister(this);
    }

    @Subscribe
    public void OnInvitationListReceivedEvent(InvitationListReceivedEvent e) {
        Log.v(TAG, "We go the invitation list. let's put it in adaptateur and refresh");
        List<Invitation> l = e.get();
        if (l != null) {
            for (int i = 0; i < l.size(); i++) {
                adapter.add(l.get(i));
            }
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getActivity(), "Unable to retrieve the invitation", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = OrderReviewActivity.createIntent(getActivity(), (int)id);
        startActivity(i);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }


    public class MySimpleArrayAdapter extends ArrayAdapter<Invitation> {
        private final Context context;
        private  Invitation[] values;

        public MySimpleArrayAdapter(Context context, Invitation[] values) {
            super(context, R.layout.invitation_item_list, values);
            this.context = context;
            this.values = values;
        }

        public void update(Invitation[] values) {
            Log.v(TAG,  "We update with " + values.length +  " elements");
            this.values = values;
            this.notifyDataSetChanged();
        }

        @Override
        public long getItemId(int position) {
            return values[position].getInv_id();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.invitation_item_list, parent, false);

            TextView nameTextView = (TextView) rowView.findViewById(R.id.EventName);
            TextView text1TextView = (TextView) rowView.findViewById(R.id.EventText1);
            TextView text2TextView = (TextView) rowView.findViewById(R.id.EventText2);

            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
            text1TextView.setText(values[position].getOrder().getRestaurant().getName());
            return rowView;
        }
    }

}