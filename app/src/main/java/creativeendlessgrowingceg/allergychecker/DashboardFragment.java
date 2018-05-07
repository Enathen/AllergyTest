package creativeendlessgrowingceg.allergychecker;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import creativeendlessgrowingceg.allergychecker.fragment.HistoryFragment;
import creativeendlessgrowingceg.allergychecker.fragment.StartPage;

import static creativeendlessgrowingceg.allergychecker.StatisticAPI.contextName;
import static creativeendlessgrowingceg.allergychecker.StatisticAPI.foundCount;
import static creativeendlessgrowingceg.allergychecker.StatisticAPI.scannedCount;
import static creativeendlessgrowingceg.allergychecker.StatisticAPI.wordCount;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout inflate = (FrameLayout) inflater.inflate(R.layout.fragment_dashbord, container, false);

        CardClassSetup linearCardClass = new CardClassSetup();
        //TODO color background depending on size
        AtomicInteger atomicInteger = new AtomicInteger(0);
        ColorGradientPicker colorGradientPicker = new ColorGradientPicker();

        CardClassLayout history = new CardClassLayout().useCardClassLayout(getContext(), "History", R.drawable.history, (LinearLayout) inflate.findViewById(R.id.linearCardHistory), colorGradientPicker.ColorGradientPickerPick(5, atomicInteger.get()));
        CardClassLayout statistic = new CardClassLayout().useCardClassLayout(getContext(), "Statistic", R.drawable.star, (LinearLayout) inflate.findViewById(R.id.linearCardStatistic), colorGradientPicker.ColorGradientPickerPick(5, atomicInteger.addAndGet(1)));
        CardClassLayout language = new CardClassLayout().useCardClassLayout(getContext(), "Language", R.drawable.language, (LinearLayout) inflate.findViewById(R.id.linearCardLanguage), colorGradientPicker.ColorGradientPickerPick(5, atomicInteger.addAndGet(1)));
        CardClassLayout showAllergies = new CardClassLayout().useCardClassLayout(getContext(), "Show Allergies", R.drawable.wheatcircle, (LinearLayout) inflate.findViewById(R.id.linearCardShowAllergies), colorGradientPicker.ColorGradientPickerPick(5, atomicInteger.addAndGet(1)));
        CardClassLayout camera = new CardClassLayout().useCardClassLayout(getContext(), "Camera", R.drawable.ic_menu_camera, (LinearLayout) inflate.findViewById(R.id.linearCardCamera), colorGradientPicker.ColorGradientPickerPick(5, atomicInteger.addAndGet(1)));
        historySetup(history,linearCardClass);
        statisticSetup(statistic,linearCardClass);
        languageSetup(language,linearCardClass);
        cameraSetup(camera,linearCardClass);
        linearCardClass.CardDefaultTransition(history);
        linearCardClass.CardDefaultTransition(statistic);
        linearCardClass.CardDefaultTransition(language);
        linearCardClass.CardDefaultTransition(showAllergies);
        linearCardClass.CardDefaultTransition(camera);

        return inflate;

    }

    private void cameraSetup(CardClassLayout camera, CardClassSetup linearCardClass) {
        linearCardClass.addCheckBox(camera, new CheckBoxLayout.CheckBoxBuilder(getContext(),getString(R.string.flash)).optionalImage(R.drawable.flashon).optionalCheckBox().buildCheckBoxLayout().getView());
        linearCardClass.addCheckBox(camera, new CheckBoxLayout.CheckBoxBuilder(getContext(),getString(R.string.flash)).optionalImage(R.drawable.flashon).optionalCheckBox().buildCheckBoxLayout().getView());

    }

    private void languageSetup(CardClassLayout language, CardClassSetup linearCardClass) {
        for (Locale locale : LanguagesAccepted.getLanguages(getContext())) {
            linearCardClass.addCheckBox(language, new CheckBoxLayout.CheckBoxBuilder(getContext(),
                    getString(LanguagesAccepted.getCountryName(locale.getLanguage()))).optionalImage(LanguagesAccepted.getFlag(locale.getLanguage())).optionalCheckBox().buildCheckBoxLayout().getView());
        }
    }

    private void statisticSetup(CardClassLayout statistic, CardClassSetup linearCardClass) {
        SharedPreferences sp = getContext().getSharedPreferences(contextName(), Context.MODE_PRIVATE);
        linearCardClass.addCheckBox(statistic, new CheckBoxLayout.CheckBoxBuilder(getContext(), getString(R.string.wordCount)).optionalLastString(sp.getString(wordCount(), "100")).buildCheckBoxLayout().getView());
        linearCardClass.addCheckBox(statistic, new CheckBoxLayout.CheckBoxBuilder(getContext(), getString(R.string.foundAllergiesCount)).optionalLastString(sp.getString(foundCount(), "50000")).buildCheckBoxLayout().getView());
        linearCardClass.addCheckBox(statistic, new CheckBoxLayout.CheckBoxBuilder(getContext(), getString(R.string.scannedCount)).optionalLastString(sp.getString(scannedCount(), "0")).buildCheckBoxLayout().getView());


    }
    private void historySetup(CardClassLayout history, CardClassSetup linearCardClass) {
        ArrayList<String> arrayList = new DateAndHistory(getActivity()).getArrayFromHistory();
        Collections.sort(arrayList, new HistoryFragment.stringComparator());
        Collections.reverse(arrayList);
        for (String s : arrayList) {
            linearCardClass.addCheckBox(history, new CheckBoxLayout.CheckBoxBuilder(getContext(), s.substring(0, 20)).buildListener(historyCheckbox(s.substring(20))).buildCheckBoxLayout().getView());

        }

    }

    private View.OnClickListener historyCheckbox(final String substring) {
        return new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(getActivity(), StartPage.class);
                intent.putExtra("HistoryFragment", substring);
                startActivity(intent);
                getActivity().finish();

            }
        };
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


}
