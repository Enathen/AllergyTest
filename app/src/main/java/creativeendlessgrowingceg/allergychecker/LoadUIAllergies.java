package creativeendlessgrowingceg.allergychecker;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 * load an UI.
 * Created by Enathen on 2018-01-12.
 */

public class LoadUIAllergies {
    private static final String TAG = "LoadUIAllergies";
    protected StartPage context;
    protected FrameLayout parentFrameLayout;
    protected LinearLayout parentLinearLayout;
    protected TreeMap<Integer, TreeMap<Integer, LinearLayout>> linearLayoutToInsertLater = new TreeMap<>();
    protected TreeMap<Integer, Integer> parentKeyToParentImage = new TreeMap<>();
    protected TreeMap<Integer, LinearLayout> parentLinearLayoutHashMap = new TreeMap<>();
    private boolean preference;
    private LayoutInflater inflater;
    private TreeMap<Integer, ArrayList<AllergyList.PictureIngredient>> myAllergies;
    private TreeMap<String, AllergyCheckBoxClass> allergyInfo = new TreeMap<>();


    public LoadUIAllergies(boolean preference, LayoutInflater inflater, StartPage context, FrameLayout parentFrameLayout, LinearLayout parentLinearLayout, TreeMap<Integer, ArrayList<AllergyList.PictureIngredient>> myAllergies) {
        this.preference = preference;
        this.inflater = inflater;
        this.context = context;

        this.parentFrameLayout = parentFrameLayout;
        this.parentLinearLayout = parentLinearLayout;
        this.myAllergies = myAllergies;
        new AddCategories().execute();
    }

    public LoadUIAllergies() {

    }

    /**
     * add all categories
     *
     * @param parentKey          to set.
     * @param parentKeyToPicture
     */
    private void addCategory(int parentKey, CheckBox parentCheckBox, int parentKeyToPicture) {
        TreeMap<Integer, LinearLayout> categoriesLinearLayout = new TreeMap<>();

        ArrayList<AllergyCheckBoxClass> allergyCheckBoxClasses = new ArrayList<>();
        for (AllergyList.PictureIngredient pictureIngredient : myAllergies.get(parentKey)) {

            LinearLayout newLinearLayout = (LinearLayout) inflater.inflate(R.layout.leftmarginrowlayout, null);
            TextView textOfAllergy = (TextView) newLinearLayout.findViewById(R.id.textViewLeftMargin);
            ImageView imageOfAllergy = (ImageView) newLinearLayout.findViewById(R.id.imageViewLeftMargin);
            CheckBox checkBoxOfAllergy = (CheckBox) newLinearLayout.findViewById(R.id.checkBoxRowLeftMargin);

            textOfAllergy.setText(TextHandler.capitalLetter(pictureIngredient.getIngredient()));
            imageOfAllergy.setImageResource(pictureIngredient.getPicture());
            categoriesLinearLayout.put(pictureIngredient.getId(), newLinearLayout);
            if (allergyInfo.containsKey(pictureIngredient.getIngredient())) {
                AllergyCheckBoxClass allergyCheckBoxClass = new AllergyCheckBoxClass(checkBoxOfAllergy, parentCheckBox,
                        parentKeyToPicture, pictureIngredient.getId(), parentKey, pictureIngredient.getIngredient(),
                        allergyInfo.get(pictureIngredient.getIngredient()).getSameItemDifferentCategories());
                allergyInfo.get(pictureIngredient.getIngredient()).getSameItemDifferentCategories().add(allergyCheckBoxClass);
                allergyCheckBoxClasses.add(allergyCheckBoxClass);

            } else {
                allergyInfo.put(pictureIngredient.getIngredient(), new AllergyCheckBoxClass(checkBoxOfAllergy,
                        parentCheckBox, parentKeyToPicture, pictureIngredient.getId(), parentKey,
                        pictureIngredient.getIngredient()));
                allergyInfo.get(pictureIngredient.getIngredient()).getSameItemDifferentCategories().add(allergyInfo.get(pictureIngredient.getIngredient()));
                allergyCheckBoxClasses.add(allergyInfo.get(pictureIngredient.getIngredient()));
            }


        }
        for (AllergyCheckBoxClass allergyCheckBoxClass : allergyCheckBoxClasses) {
            allergyCheckBoxClass.setNeigbhourClasses(allergyCheckBoxClasses);

        }
        linearLayoutToInsertLater.put(parentKey, categoriesLinearLayout);


    }

    private CheckBox createParentLinearLayout(final int key, int pictureID) {
        final LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.rowcategorylayout, null);
        LinearLayout linearLayoutRow = (LinearLayout) linearLayout.findViewById(R.id.linearLayoutRowCategoryHorizontal);
        ((TextView) linearLayout.findViewById(R.id.textViewCategory)).setText(TextHandler.capitalLetter(context.getString(key)));

        final CheckBox checkboxRowCategory = (CheckBox) linearLayoutRow.findViewById(R.id.checkBoxRowCategory);


        ImageView imageIcon = (ImageView) linearLayoutRow.findViewById(R.id.imageViewRowCategory);
        imageIcon.setImageResource(pictureID);
        final ImageView dropDownImage = (ImageView) linearLayoutRow.findViewById(R.id.dropDownList);

        dropDownImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateLinearLayout.onclickDropDownList(dropDownImage, linearLayoutToInsertLater.get(key), linearLayout);
            }
        });
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateLinearLayout.onclickDropDownList(dropDownImage, linearLayoutToInsertLater.get(key), linearLayout);
            }
        });

        parentLinearLayoutHashMap.put(key, linearLayout);

        return checkboxRowCategory;

    }

    synchronized public void savePicture(StartPage context, HashMap<Integer, ImageView> imageViewHashMap) {
        String alreadyString = "00000000";

        for (ImageView imageView : imageViewHashMap.values()) {
            imageView.setImageDrawable(context.getDrawable(R.drawable.emptyborder));
        }
        HashSet<Integer> allergySavePicture = SharedPreferenceClass.getSharedPreference(context, "allergySavePicture", TAG);
        HashSet<Integer> allergySavePreference = (SharedPreferenceClass.getSharedPreference(context, "preferenceSavePicture", TAG));
        Set<String> allergySavePictureName = (SharedPreferenceClass.getSharedPreferenceString(context, "allergySavePictureName", TAG));
        Set<String> allergySavePictureNamePreference = SharedPreferenceClass.getSharedPreferenceString(context, "preferenceSavePictureName", TAG);
        if(allergySavePicture == null){
            return;
        }
        
        checkDeleteAllergySavePicture(context,allergySavePicture,allergySavePictureName,"allergySavePicture","allergySavePictureName");
        checkDeleteAllergySavePicture(context,allergySavePreference,allergySavePictureNamePreference, "preferenceSavePicture", "preferenceSavePictureName");


        addPicture(allergySavePreference,alreadyString,imageViewHashMap, addPicture(allergySavePicture,alreadyString,imageViewHashMap,0));


    }

    private int addPicture(HashSet<Integer> allergySavePicture, String alreadyString, HashMap<Integer, ImageView> imageViewHashMap, int i) {
        ArrayList<Integer> alreadySelectedImages = new ArrayList<>();

        for (int id : allergySavePicture) {
            if (i > 7) {
                break;
            }

            if (alreadyString.charAt(i) == '0') {
                if (!alreadySelectedImages.contains(id)) {

                    imageViewHashMap.get(i).setImageResource(id);
                    alreadySelectedImages.add(id);
                    StringBuilder myName = new StringBuilder(alreadyString);
                    myName.setCharAt(i, '1');
                    alreadyString = myName.toString();
                    i++;
                }
            }
        }
        return i;
    }



    private void checkDeleteAllergySavePicture(StartPage context, HashSet<Integer> allergySavePicture, Set<String> allergySavePictureName, String savePicture, String savePictureName) {
        HashSet<Integer> allergyToRemove = new HashSet<>();

        for (Integer integer : allergySavePicture) {
            try {
                int drawable = context.getResources().getIdentifier(String.valueOf(integer), "drawable", context.getPackageName());
                context.getDrawable(integer);
                if(!allergySavePictureName.contains(context.getResources().getResourceEntryName(drawable))){
                    allergyToRemove.add(drawable);
                    allergySavePictureName.remove(context.getResources().getResourceEntryName(drawable));
                }
            } catch (Resources.NotFoundException e){
                allergyToRemove.add(context.getResources().getIdentifier(String.valueOf(integer), "drawable", context.getPackageName()));

            }
        }

        allergySavePicture.removeAll(allergyToRemove);
        SharedPreferenceClass.setSharedPreference(context, allergySavePicture, savePicture, TAG);
        SharedPreferenceClass.setSharedPreference(context, (HashSet<String>) allergySavePictureName, savePictureName, TAG);
    }

    synchronized public HashSet<String> getCurrentlyActiveAllergies() {
        HashSet<String> hashSet = new HashSet<>();
        for (AllergyCheckBoxClass allergyCheckBoxClass : allergyInfo.values()) {
            for (AllergyCheckBoxClass checkBoxClass : allergyCheckBoxClass.getSameItemDifferentCategories()) {
                if (checkBoxClass.isOn()) {
                    hashSet.add(checkBoxClass.getChildIngredient());
                }
            }
        }
        return hashSet;
    }

    synchronized public HashSet<String> getCurrentlyNotActiveAllergies() {
        HashSet<String> hashSet = new HashSet<>();
        for (AllergyCheckBoxClass allergyCheckBoxClass : allergyInfo.values()) {
            for (AllergyCheckBoxClass checkBoxClass : allergyCheckBoxClass.getSameItemDifferentCategories()) {
                if (!checkBoxClass.isOn()) {
                    hashSet.add(checkBoxClass.getChildIngredient());
                }
            }
        }
        return hashSet;
    }

    synchronized public HashSet<Integer> getCurrentlyActiveParentAllergies() {
        HashSet<Integer> hashSet = new HashSet<>();
        for (AllergyCheckBoxClass allergyCheckBoxClass : allergyInfo.values()) {
            if (allergyCheckBoxClass.getParentCheckBox().isChecked()) {
                hashSet.add(allergyCheckBoxClass.getParentKey());


            }
        }
        return hashSet;
    }

    synchronized public HashSet<Integer> getCurrentlyNotActiveParentAllergies() {
        HashSet<Integer> hashSet = new HashSet<>();
        for (AllergyCheckBoxClass allergyCheckBoxClass : allergyInfo.values()) {
            if (!allergyCheckBoxClass.getParentCheckBox().isChecked()) {
                hashSet.add(allergyCheckBoxClass.getParentKey());
            }
        }
        return hashSet;
    }

    public HashMap<String, CheckBox> getCheckBoxToRemove() {
        HashMap<String, CheckBox> hashMap = new HashMap<>();
        for (AllergyCheckBoxClass allergyCheckBoxClass : allergyInfo.values()) {
            if (allergyCheckBoxClass.isRemove()) {
                hashMap.put(allergyCheckBoxClass.getChildIngredient(), allergyCheckBoxClass.getChildCheckBox());
            }
            for (AllergyCheckBoxClass checkBoxClass : allergyCheckBoxClass.getSameItemDifferentCategories()) {

                if (checkBoxClass.isRemove()) {
                    hashMap.put(checkBoxClass.getChildIngredient(), checkBoxClass.getChildCheckBox());
                }

            }
        }
        return hashMap;
    }

    synchronized public void saveCurrentlyActive(boolean preference) {
        for (String key : getCheckBoxToRemove().keySet()) {
            SharedPreferenceClass.setBoolean(key, context, false);
        }

        for (String key : getCurrentlyActiveAllergies()) {
            SharedPreferenceClass.setBoolean(key, context, true);
        }

        if (!preference) {
            SharedPreferenceClass.setSharedPreference(context, getCurrentlyActiveParentAllergies(), "allergyToSendToPreference", TAG);
            SharedPreferenceClass.setSharedPreference(context, getCurrentlyNotActiveParentAllergies(), "notAllergyToSendToPreference", TAG);
            saveAllergies();
        }
        if (preference) {

            SharedPreferenceClass.setSharedPreference(context, getCurrentlyActiveAllergies(), "preferenceToSendToAllergy", TAG);
            SharedPreferenceClass.setSharedPreference(context, getCurrentlyNotActiveAllergies(), "notPreferenceToSendToAllergy", TAG);
            savePreference();
        }


    }

    private void saveAllergies() {
        HashSet<Integer> hashSet = new HashSet<>();
        HashSet<Integer> hashSetPicture = new HashSet<>();
        HashSet<Integer> hashSetNot= new HashSet<>();
        HashSet<Integer> hashSetPictureNot = new HashSet<>();
        HashSet<Integer> hashSetPreference = SharedPreferenceClass.getSharedPreference(context,"preferenceSave",TAG);
        HashSet<Integer> hashSetPicturePreference =  SharedPreferenceClass.getSharedPreference(context,"preferenceSavePicture",TAG);
        getCheckedCheckBoxes(hashSet,hashSetPicture);
        getCheckedCheckBoxesNot(hashSetNot,hashSetPictureNot);

        saveHashSet(hashSet,"allergySave");
        saveHashSet(hashSetPicture,"allergySavePicture");
        savePictureName(hashSetPicture,"allergySavePictureName");

        debug(hashSet,hashSetPicture,hashSetNot,hashSetPictureNot,hashSetPreference,hashSetPicturePreference);

        addPreferences(hashSet, hashSetPreference,hashSetPicturePreference);
        removePreferences(hashSetNot, hashSetPreference,hashSetPicturePreference);

        saveHashSet(hashSetPreference,"preferenceSave");
        saveHashSet(hashSetPicturePreference,"preferenceSavePicture");
        savePictureName(hashSetPicturePreference,"preferenceSavePictureName");
    }

    private void removePreferences(HashSet<Integer> hashSetNot, HashSet<Integer> hashSetPreference, HashSet<Integer> hashSetPicturePreference) {
        TreeMap<Integer, ArrayList<AllergyList.PictureIngredient>> myPreference = new AllergyList(context).getMyPreference();
        for (Integer integer : hashSetNot) {
            ArrayList<AllergyList.PictureIngredient> specifiedKey = new AllergyList(context).getSpecifiedKeyAllergy(integer);
            if(specifiedKey != null){
                hashSetPreference.remove(integer);
                for (Integer pictureIngredients : myPreference.keySet()) {
                    for (AllergyList.PictureIngredient pictureIngredient : myPreference.get(pictureIngredients)) {
                        if(pictureIngredient.getId() == integer){
                            hashSetPicturePreference.remove(pictureIngredient.getPicture());
                            Log.d(TAG, "removePreferences: " + context.getString(pictureIngredient.getId())+ " Parent:" + context.getString(pictureIngredients));
                        }
                    }

                }
            }
        }
    }

    private void addPreferences(HashSet<Integer> hashSet, HashSet<Integer> hashSetPreference, HashSet<Integer> hashSetPicturePreference) {
        TreeMap<Integer, ArrayList<AllergyList.PictureIngredient>> myPreference = new AllergyList(context).getMyPreference();
        for (Integer integer : hashSet) {
            ArrayList<AllergyList.PictureIngredient> specifiedKey = new AllergyList(context).getSpecifiedKeyAllergy(integer);
            if(specifiedKey != null){
                hashSetPreference.add(integer);
                for (Integer pictureIngredients : myPreference.keySet()) {
                    boolean addPreference = true;
                    for (AllergyList.PictureIngredient pictureIngredient : myPreference.get(pictureIngredients)) {
                        if(pictureIngredient.getId() == integer){

                            Log.d(TAG, "addPreferences: " + context.getString(pictureIngredient.getId())+ " Parent:" + context.getString(pictureIngredients));
                        }else{
                            if(!hashSetPreference.contains(pictureIngredient.getId())){
                                Log.d(TAG, "addPreferences!: " + context.getString(pictureIngredient.getId())+ " Parent:" + context.getString(pictureIngredients));
                                addPreference = false;
                            }
                        }
                    }
                    if(addPreference){
                        hashSetPicturePreference.add(myPreference.get(pictureIngredients).get(0).getPicture());
                    }
                }
            }


        }
    }

    public void printHashSet(HashSet<Integer> hashSet,String name){
        for (Integer integer : hashSet) {
            Log.d(TAG, "Name: "+name + " : String: " + context.getString(integer));
        }
    }


    synchronized private void savePreference() {
        HashSet<Integer> hashSet = new HashSet<>();
        HashSet<Integer> hashSetPicture = new HashSet<>();
        HashSet<Integer> hashSetNot= new HashSet<>();
        HashSet<Integer> hashSetPictureNot = new HashSet<>();
        HashSet<Integer> hashSetAllergies = SharedPreferenceClass.getSharedPreference(context,"allergySave",TAG);
        HashSet<Integer> hashSetPictureAllergies =  SharedPreferenceClass.getSharedPreference(context,"allergySavePicture",TAG);
        getCheckedCheckBoxes(hashSet,hashSetPicture);
        getCheckedCheckBoxesNot(hashSetNot,hashSetPictureNot);

        //debug(hashSet,hashSetPicture,hashSetNot,hashSetPictureNot,hashSetAllergies,hashSetPictureAllergies);


        saveHashSet(hashSet,"preferenceSave");
        saveHashSet(hashSetPicture,"preferenceSavePicture");
        savePictureName(hashSetPicture,"preferenceSavePictureName");
        removeAllergies(hashSetNot, hashSetAllergies, hashSetPictureAllergies);
        addAllergies(hashSet,hashSetAllergies,hashSetPictureAllergies);
        saveHashSet(hashSetAllergies,"allergySave");
        saveHashSet(hashSetPictureAllergies,"allergySavePicture");
        savePictureName(hashSetPictureAllergies,"allergySavePictureName");



    }

    private void savePictureName(HashSet<Integer> hashSetPicture, String name) {
        HashSet<String> hashSetPictureName = new HashSet<>();
        for (Integer integer : hashSetPicture) {
            hashSetPictureName.add(context.getResources().getResourceEntryName(integer));
            Log.d(TAG, "savePictureName: "+context.getResources().getResourceEntryName(integer));
        }
        SharedPreferenceClass.setSharedPreference(context, hashSetPictureName, name, TAG);

    }

    private void debug(HashSet<Integer> hashSet, HashSet<Integer> hashSetPicture, HashSet<Integer> hashSetNot, HashSet<Integer> hashSetPictureNot, HashSet<Integer> hashSetAllergies, HashSet<Integer> hashSetPictureAllergies) {
        printHashSet(hashSet,"hashSet");
        printHashSet(hashSetPicture,"hashSetPicture");
        printHashSet(hashSetNot,"hashSetNot");
        printHashSet(hashSetPictureNot,"hashSetPictureNot");
        printHashSet(hashSetAllergies,"hashSetOPOSITE");
        printHashSet(hashSetPictureAllergies,"hashSetPictureOPOSITE");
    }


    private void addAllergies(HashSet<Integer> hashSet, HashSet<Integer> hashSetAllergies, HashSet<Integer> hashSetPictureAllergies) {
        for (Integer integer : hashSet) {
            ArrayList<AllergyList.PictureIngredient> specifiedKey = new AllergyList(context).getSpecifiedKeyAllergy(integer);
            if(specifiedKey != null){
                Log.d(TAG, "addAllergiesParent: " + context.getString(integer));
                for (AllergyList.PictureIngredient pictureIngredient : specifiedKey) {
                    hashSetAllergies.add(pictureIngredient.getId());
                    Log.d(TAG, "addAllergiesCHILD: " + context.getString(pictureIngredient.getId()));

                }
                hashSetAllergies.add(integer);
                hashSetPictureAllergies.add(specifiedKey.get(0).getPicture());
            }
        }
    }

    private void removeAllergies(HashSet<Integer> hashSetNot, HashSet<Integer> hashSetAllergies, HashSet<Integer> hashSetPictureAllergies) {
        for (Integer integer : hashSetNot) {
            if(hashSetAllergies.contains(integer)){
                Log.d(TAG, "removeAllergiesParent: " + context.getString(integer));
                ArrayList<AllergyList.PictureIngredient> specifiedKey = new AllergyList(context).getSpecifiedKeyAllergy(integer);
                HashSet<Integer> remove = new HashSet<>();
                for (AllergyList.PictureIngredient pictureIngredient : specifiedKey) {
                    remove.add(pictureIngredient.getId());
                    Log.d(TAG, "removeAllergiesChild: " + context.getString(pictureIngredient.getId()));
                }
                hashSetAllergies.removeAll(remove);
                hashSetAllergies.remove(integer);
                hashSetPictureAllergies.remove(specifiedKey.get(0).getPicture());
            }
        }
    }

    private void saveHashSet(HashSet<Integer> hashSet, String name) {
        SharedPreferenceClass.setSharedPreference(context,hashSet,name,TAG);
    }

    private void getCheckedCheckBoxesNot(HashSet<Integer> hashSetNot, HashSet<Integer> hashSetPictureNot) {
        for (AllergyCheckBoxClass allergyCheckBoxClass : allergyInfo.values()) {
            for (AllergyCheckBoxClass checkBoxClass : allergyCheckBoxClass.getSameItemDifferentCategories()) {
                //if all checked
                if (!checkBoxClass.getParentCheckBox().isChecked()) {
                    hashSetPictureNot.add(checkBoxClass.getParentPicture());
                }
                if (!checkBoxClass.getChildCheckBox().isChecked()) {
                    hashSetNot.add(checkBoxClass.getChildKey());
                    if(!preference){
                        hashSetPictureNot.add(checkBoxClass.getParentPicture());
                        hashSetNot.add(checkBoxClass.getParentKey());

                    }
                }
            }
        }
    }
    private void getCheckedCheckBoxes(HashSet<Integer> hashSet, HashSet<Integer> hashSetPicture) {
        for (AllergyCheckBoxClass allergyCheckBoxClass : allergyInfo.values()) {
            for (AllergyCheckBoxClass checkBoxClass : allergyCheckBoxClass.getSameItemDifferentCategories()) {
                //if all checked
                if (checkBoxClass.getParentCheckBox().isChecked()) {
                    hashSetPicture.add(checkBoxClass.getParentPicture());
                }
                if (checkBoxClass.getChildCheckBox().isChecked()) {
                    hashSet.add(checkBoxClass.getChildKey());
                    if(!preference){
                        hashSetPicture.add(checkBoxClass.getParentPicture());
                        hashSet.add(checkBoxClass.getParentKey());

                    }
                }
            }
        }
    }

    synchronized public HashSet<Integer> getAllergies(Context startPage) {
        HashSet<Integer> sharedPreference = SharedPreferenceClass.getSharedPreference(startPage, "allergySave", "LoadUIAllergies");
        sharedPreference.addAll(SharedPreferenceClass.getSharedPreference(startPage, "preferenceSave", "LoadUIAllergies"));
        return sharedPreference;
    }


    public void checkStringDelete(StartPage startPage) {
        TreeMap<Integer, ArrayList<AllergyList.PictureIngredient>> allergies = new AllergyList(startPage.getBaseContext()).getMyAllergies();
        TreeMap<Integer, ArrayList<AllergyList.PictureIngredient>> preference = new AllergyList(startPage.getBaseContext()).getMyPreference();
        HashSet<Integer>  myAllergies = SharedPreferenceClass.getSharedPreference(startPage,"allergySave",TAG);
        HashSet<Integer>  myPreference = SharedPreferenceClass.getSharedPreference(startPage,"preferenceSave",TAG);
        removeStringAllergies(startPage,allergies,myAllergies,"allergySave");
        removeStringAllergies(startPage,preference,myPreference,"preferenceSave");




    }

    private void removeStringAllergies(StartPage startPage, TreeMap<Integer, ArrayList<AllergyList.PictureIngredient>> allergies, HashSet<Integer> myAllergies, String name) {
        HashSet<Integer> allergiesToRemove = new HashSet<>();
        for (Integer myAllergy : myAllergies) {
            try {
                startPage.getString(myAllergy);
                boolean destroy = true;
                for (Integer integer : allergies.keySet()) {
                    if(integer == myAllergy){
                        destroy = false;
                        break;
                    }
                    for (AllergyList.PictureIngredient pictureIngredients : allergies.get(integer)) {
                        if(pictureIngredients.getId() == myAllergy){
                            destroy = false;
                            break;
                        }
                    }
                    if(!destroy){
                        break;
                    }
                }
                if(destroy){
                    allergiesToRemove.add(myAllergy);
                }
            }catch (Resources.NotFoundException e){
                allergiesToRemove.add(myAllergy);
                Log.d(TAG, "removeDangStringAllergies: "+ myAllergy);
            }
        }
        Log.d(TAG, "removeStringAllergies: "+ myAllergies);
        myAllergies.removeAll(allergiesToRemove);
        Log.d(TAG, "removeStringAllergies: "+ myAllergies);
        SharedPreferenceClass.setSharedPreference(startPage, myAllergies, name, TAG);
    }


    private class AddCategories extends AsyncTask<String, Integer, String> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected String doInBackground(String... params) {
            for (int key : myAllergies.keySet()) {
                parentKeyToParentImage.put(key, myAllergies.get(key).get(0).getPicture());
            }
            for (int key : myAllergies.keySet()) {

                addCategory(key, createParentLinearLayout(key, parentKeyToParentImage.get(key)), parentKeyToParentImage.get(key));
            }
            return null;
        }

        /**
         * Runs on the UI thread before {@link #doInBackground}.
         *
         * @see #onPostExecute
         * @see #doInBackground
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         * <p>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param s The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @Override
        protected void onPostExecute(String s) {
            for (int key : linearLayoutToInsertLater.keySet()) {

                parentLinearLayout.addView(parentLinearLayoutHashMap.get(key));
            }

            for (AllergyCheckBoxClass allergyCheckBoxClass : allergyInfo.values()) {
                for (AllergyCheckBoxClass checkBoxClass : allergyCheckBoxClass.getSameItemDifferentCategories()) {
                    final CheckBox checkBox = checkBoxClass.getChildCheckBox();
                    checkBox.setChecked(SharedPreferenceClass.checkBoolean(checkBoxClass.getChildIngredient(), context));
                    checkBoxClass.setOn(SharedPreferenceClass.checkBoolean(checkBoxClass.getChildIngredient(), context));
                    if (!preference) {
                        if (checkBox.isChecked()) {
                            checkBoxClass.getParentCheckBox().setChecked(true);
                            checkBoxClass.setOn(true);
                        }
                        CreateLinearLayout.checkBoxChildOnCheckedListener(checkBoxClass);
                    } else {
                        CreateLinearLayout.checkBoxChildOnCheckedListenerPreference(checkBoxClass);

                    }

                }
            }
            HashSet<Integer> hashSet = new HashSet<>();

            for (AllergyCheckBoxClass allergyCheckBoxClass : allergyInfo.values()) {
                if (!hashSet.contains(allergyCheckBoxClass.getParentKey())) {
                    hashSet.add(allergyCheckBoxClass.getParentKey());
                    CreateLinearLayout.parentCheckedChanged(allergyCheckBoxClass);
                    if (preference) {
                        for (AllergyCheckBoxClass allergyCheckBoxClass1 : allergyCheckBoxClass.getSameItemDifferentCategories()) {
                            CreateLinearLayout.checkParentShouldChecked(allergyCheckBoxClass1, true);
                        }

                    }

                }
            }
            if (!preference) {
                HashSet<String> hashSetCheckedAllergies = (HashSet<String>) SharedPreferenceClass.getSharedPreferenceString(context, "preferenceToSendToAllergy", TAG);
                HashSet<String> hashSetCheckedNotAllergies = (HashSet<String>) SharedPreferenceClass.getSharedPreferenceString(context, "notPreferenceToSendToAllergy", TAG);
                HashSet<Integer> alreadyChecked = new HashSet<>();
                for (AllergyCheckBoxClass allergyCheckBoxClass : allergyInfo.values()) {
                    if (!alreadyChecked.contains(allergyCheckBoxClass.getParentKey())) {
                        if (hashSetCheckedAllergies.contains(context.getString(allergyCheckBoxClass.getParentKey()))) {
                            alreadyChecked.add(allergyCheckBoxClass.getParentKey());
                            Log.d(TAG, "lägger till från preference: " + context.getString(allergyCheckBoxClass.getParentKey()));
                            allergyCheckBoxClass.getParentCheckBox().setChecked(true);
                        }
                        if (hashSetCheckedNotAllergies.contains(context.getString(allergyCheckBoxClass.getParentKey()))) {
                            alreadyChecked.add(allergyCheckBoxClass.getParentKey());
                            Log.d(TAG, "TAR bort från preference: " + context.getString(allergyCheckBoxClass.getParentKey()));
                            allergyCheckBoxClass.getParentCheckBox().setChecked(false);
                        }
                    }

                }

                hashSetCheckedAllergies.clear();
                hashSetCheckedNotAllergies.clear();
                SharedPreferenceClass.setSharedPreference(context, hashSetCheckedAllergies, "preferenceToSendToAllergy", TAG);
                SharedPreferenceClass.setSharedPreference(context, hashSetCheckedAllergies, "notPreferenceToSendToAllergy", TAG);
            } else {
                HashSet<Integer> hashSetCheckedAllergies = SharedPreferenceClass.getSharedPreference(context, "allergyToSendToPreference", TAG);
                HashSet<Integer> hashSetCheckedNotAllergies = SharedPreferenceClass.getSharedPreference(context, "notAllergyToSendToPreference", TAG);

                for (AllergyCheckBoxClass allergyCheckBoxClass : allergyInfo.values()) {
                    if (hashSetCheckedAllergies.contains(allergyCheckBoxClass.getChildKey())) {
                        Log.d(TAG, "lägger till från allergy: " + context.getString(allergyCheckBoxClass.getParentKey()));
                        allergyCheckBoxClass.getChildCheckBox().setChecked(true);
                    }
                    if (hashSetCheckedNotAllergies.contains(allergyCheckBoxClass.getChildKey())) {
                        Log.d(TAG, "TAR bort från allergy: " + context.getString(allergyCheckBoxClass.getParentKey()));
                        allergyCheckBoxClass.getChildCheckBox().setChecked(false);
                    }

                }
                hashSetCheckedAllergies.clear();
                hashSetCheckedNotAllergies.clear();
                SharedPreferenceClass.setSharedPreference(context, hashSetCheckedAllergies, "allergyToSendToPreference", TAG);
                SharedPreferenceClass.setSharedPreference(context, hashSetCheckedAllergies, "notAllergyToSendToPreference", TAG);
            }
            parentLinearLayout.addView(new TextView(context));
            parentLinearLayout.addView(new TextView(context));
            parentLinearLayout.addView(new TextView(context));
            parentLinearLayout.addView(new TextView(context));


            parentLinearLayout.removeView(parentLinearLayout.findViewById(R.id.progressBarAllergy));

            super.onPostExecute(s);
        }
    }


}