package creativeendlessgrowingceg.allergychecker;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.HashSet;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyPreference.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyPreference#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPreference extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "MyPreference";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private FrameLayout parentFrameLayout;
    private LinearLayout parentLinearLayout;
    private LoadUIAllergies loadUIAllergies;


    public MyPreference() {
        // Required empty public constructor
    }




    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllergyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public MyPreference newInstance(String param1, String param2) {
        MyPreference fragment = new MyPreference();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    //create the look
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentFrameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_allergy, container, false);

        //insert everything to this linear layout
        parentLinearLayout = (LinearLayout) parentFrameLayout.findViewById(R.id.linlayoutFrag);

        loadUIAllergies = new LoadUIAllergies(inflater, getContext(), getActivity(), parentFrameLayout, parentLinearLayout, new AllergyList(getContext()).getMyPreference());

        return parentFrameLayout;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        for (int key : loadUIAllergies.getCheckBoxToRemove().keySet()) {
            SharedPreferenceClass.setBoolean(getString(key),getContext(),false);
        }
        HashSet<Integer> currentlyActiveAllergies = loadUIAllergies.getCurrentlyActiveAllergies();
        for (int key : currentlyActiveAllergies) {
            SharedPreferenceClass.setBoolean(getString(key), getContext(),true);
        }
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        for (int key : loadUIAllergies.getCheckBoxToRemove().keySet()) {
            SharedPreferenceClass.setBoolean(getString(key),getContext(),false);
        }
        HashSet<Integer> currentlyActiveAllergies = loadUIAllergies.getCurrentlyActiveAllergies();
        for (int key : currentlyActiveAllergies) {
            SharedPreferenceClass.setBoolean(getString(key), getContext(),true);
        }
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}



