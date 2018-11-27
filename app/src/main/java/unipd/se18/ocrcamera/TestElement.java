package unipd.se18.ocrcamera;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Class that contains a single test element, used in PhotoTester to build a single test and on AdapterTestElement to show data in a listview
 * @author Luca Moroldo, Francesco Pham
 */

public class TestElement {

    private static final String TAG = "TestElement";

    private Bitmap picture;
    private JSONObject jsonObject;
    private String fileName;
    private HashMap<String, Bitmap> alterationsBitmaps;

    /**
     *
     * @param picture Bitmap associated to the jsonObject
     * @param jsonObject JSONObject containing test data (ingredients, tags, notes, alterations if any)
     * @param fileName name of the test
     * @author Luca Moroldo - g3
     */
    public TestElement(Bitmap picture, JSONObject jsonObject, String fileName) {
        this.picture = picture;
        this.jsonObject = jsonObject;
        this.fileName = fileName;
        //prepare alterations bitmap if there is any
        String[] alterationsNames = getAlterationsNames();
        if(alterationsNames != null) {
            alterationsBitmaps = new HashMap<String, Bitmap>();
            for(String alterationName : alterationsNames) {
                alterationsBitmaps.put(alterationName, null);
            }
        }
    }

    /**
     * Array of Strings, each string is an ingredient, ingredients are separated on comma
     * @return Array of strings, each string is an ingredient
     */
    public String[] getIngredientsArray() {
        String ingredients = getIngredients();
        String[] ingredientsArr = ingredients.trim().split("\\s*,\\s*"); //split removing whitespaces
        return ingredientsArr;
    }

    /**
     * @return string with key 'ingredients' if exist, null otherwise
     * @author Luca Moroldo - g3
     */
    public String getIngredients() {
        try {
            String ingredients = jsonObject.getString("ingredients");
            return ingredients;
        } catch (JSONException e) {
            Log.i(TAG, "getIngredients: No ingredient found in test " + fileName);
        }
        return null;
    }

    /**
     * @return array of strings with key 'tags' if exist, null otherwise
     * @author Luca Moroldo - g3
     */
    public String[] getTags() {
        try {
            return Utils.getStringArrayFromJSON(jsonObject, "tags");
        } catch (JSONException e) {
            Log.i(TAG, "getTags: No tag found in test " + fileName);
        }
        return null;
    }

    /**
     *
     * @return Bitmap associated to this test if it has been set, null otherwise
     */
    public Bitmap getPicture() {
        return picture;
    }

    public String getFileName() {
        return fileName;
    }

    /**
     * @return String with key 'notes' if exist, can be empty
     * @author Luca Moroldo - g3
     */
    public String getNotes() {
        try {
            return jsonObject.getString("notes");
        } catch (JSONException e) {
            Log.i(TAG, "getNotes: no note found in test " + fileName);
        }
        return null;
    }

    /**
     * Getter for alterations filenames of a test (e.g. cropped photo) if any
     * @return array of strings if the element has any alteration (each string is a filename), null otherwise
     * @author Luca Moroldo - g3
     */
    public String[] getAlterationsNames() {

        JSONObject alterations = null;
        try {
            alterations = jsonObject.getJSONObject("alterations");
        } catch (JSONException e) {
            Log.i(TAG, "getAlterationsNames: no alteration found in " + fileName);
            return null;
        }

        ArrayList<String> alterationsNames= new ArrayList<String>();

        Iterator<String> keys = alterations.keys();

        while(keys.hasNext()) {
            String key = keys.next();
            alterationsNames.add(key);
        }

        return alterationsNames.toArray(new String[0]);
    }

    /**
     * @return Float confidence if set, -1 otherwise
     * @author Luca Moroldo - g3
     */
    public float getConfidence() {
        try {
            String confidence = jsonObject.getString("confidence");
            return Float.parseFloat(confidence);
        } catch (JSONException e) {
            Log.i(TAG, "getConfidence: No tag found in test " + fileName);
        }
        return -1;
    }

    /**
     * @return String recognized text if set, null otherwise
     * @author Luca Moroldo - g3
     */
    public String getRecognizedText() {
        try {
            return jsonObject.getString("extracted_text");
        } catch (JSONException e) {
            Log.i(TAG, "getRecognizedText: No recognized text found in test " + fileName);
        }
        return null;
    }

    /**
     * Get an alteration extracted text
     * @param alterationName name of an existing alteration inside this test
     * @return alteration recognized text if it's set, null if recognized text hasn't been set or if there isn't any alteration named with the given param
     * @author Luca Moroldo - g3
     */
    public String getAlterationRecognizedText(String alterationName) {
        JSONObject jsonAlterations = null;
        try {
            jsonAlterations = jsonObject.getJSONObject("alterations");
        } catch (JSONException e) {
            Log.i(TAG, "getAlterationRecognizedText: No alteration found in " + fileName + " with name " + alterationName);
            return null;
        }

        try {

            JSONObject jsonAlteration = jsonAlterations.getJSONObject(alterationName);

            try {
                return jsonAlteration.getString("extracted_text");
            } catch (JSONException e) {
            Log.i(TAG, "getAlterationRecognizedText: There is no confidence set in altaration " + alterationName + " inside test " + fileName);
            }

        } catch (JSONException e) {
            Log.i(TAG, "getAlterationRecognizedText: There is no alteration with name " + alterationName + " inside test " + fileName);
        }

        return null;
    }

    /**
     * Get an alteration confidence
     * @param alterationName name of an existing alteration inside this test
     * @return confidence if it has been set, -1 if the confidence hasn't been set or if there isn't any alteration named with the given param
     * @author Luca Moroldo - g3
     */
    public float getAlterationConfidence(String alterationName) {

        JSONObject jsonAlterations = null;
        try {
            jsonAlterations = jsonObject.getJSONObject("alterations");
        } catch (JSONException e) {
            Log.i(TAG, "getAlterationConfidence: No alteration found in " + fileName + " with name " + alterationName);
            return -1;
        }

        try {
            JSONObject jsonAlteration = jsonAlterations.getJSONObject(alterationName);

            try {
                String confidence = jsonAlteration.getString("confidence");
                return Float.parseFloat(confidence);
            } catch (JSONException e) {
                Log.i(TAG, "getAlterationConfidence: There is no confidence set in altaration " + alterationName + " inside test " + fileName);
            }

        } catch (JSONException e) {
            Log.i(TAG, "getAlterationConfidence: There is no alteration with name " + alterationName + " inside test " + fileName);
        }
        return -1;
    }

    /**
     * Get an alteration associated bitmap
     * @param alterationName name of an existing alteration inside this test
     * @return bitmap associated with the test if it has been set, null if the bitmap hasn't been set or if there isn't any alteration named with the given param
     * @author Luca Moroldo - g3
     */
    public Bitmap getAlterationBitmap(String alterationName) {
        if(alterationsBitmaps.containsKey(alterationName))
            return alterationsBitmaps.get(alterationName);
        else
            Log.i(TAG, "getAlterationBitmap: No bitmap set for alteration " + alterationName + " in test " + fileName);
        return null;
    }

    /**
     * @param alterationName alterationName name of an existing alteration inside this test
     * @return notes text, null if there isn't any alteration named with the given param
     * @author Luca Moroldo - g3
     */
    public String getAlterationNotes(String alterationName) {
        JSONObject jsonAlterations = null;
        try {
            jsonAlterations = jsonObject.getJSONObject("alterations");
        } catch (JSONException e) {
            Log.i(TAG, "getAlterationNotes: No alteration found in " + fileName + " with name " + alterationName);
            return null;
        }

        try {

            JSONObject jsonAlteration = jsonAlterations.getJSONObject(alterationName);

            try {
                return jsonAlteration.getString("notes");
            } catch (JSONException e) {
                Log.i(TAG, "getAlterationNotes: There is no notes set in altaration " + alterationName + " inside test " + fileName);
            }

        } catch (JSONException e) {
            Log.i(TAG, "getAlterationNotes: There is no alteration with name " + alterationName + " inside test " + fileName);
        }

        return null;
    }

    /**
     * @param alterationName alterationName name of an existing alteration inside this test
     * @return tags array, null  if there isn't any alteration named with the given param
     * @author Luca Moroldo - g3
     */
    public String[] getAlterationTags(String alterationName) {

        JSONObject jsonAlterations = null;
        try {
            jsonAlterations = jsonObject.getJSONObject("alterations");
        } catch (JSONException e) {
            Log.i(TAG, "getAlterationTags: No alteration found in " + fileName + " with name " + alterationName);
            return null;
        }

        try {

            JSONObject jsonAlteration = jsonAlterations.getJSONObject(alterationName);

            try {
                return Utils.getStringArrayFromJSON(jsonAlteration, "tags");
            } catch (JSONException e) {
                Log.i(TAG, "getAlterationTags: There are no tags set in altaration " + alterationName + " inside test " + fileName);
            }

        } catch (JSONException e) {
            Log.i(TAG, "getAlterationTags: There is no alteration with name " + alterationName + " inside test " + fileName);
        }

        return null;
    }

    /**
     * @return JSONObject associated to this test
     */
    public JSONObject getJsonObject() { return jsonObject; }

    /**
     * @param confidence Float that will be associated to this test with key 'confidence'
     * @modify jsonObject of this TestElement
     */
    public void setConfidence(float confidence) {
        try {
            jsonObject.put("confidence", Float.toString(confidence));
        } catch (JSONException e) {
            Log.i(TAG, "setConfidence: Failed to set confidence in test " + fileName);
        }
    }

    /**
     * @param text String that will be set in this test with key 'extracted_text'
     * @modify jsonObject of this TestElement
     * @author Luca Moroldo - g3
     */
    public void setRecognizedText(String text) {
        try {
            jsonObject.put("extracted_text", text);
        } catch (JSONException e) {
            Log.i(TAG, "setRecognizedText: Failed to set recognized text in test " + fileName);
        }
    }

    /**
     * associate a bitmap file to an alteration of this test
     * @param alterationName name of an existing alteration inside this test
     * @param bitmap image related to the test alteration
     * @modify jsonObject of this TestElement
     * @return true if bitmap was set correctly, false if alteration name doesn't exist
     * @author Luca Moroldo - g3
     */
    public boolean setAlterationBitmap(String alterationName, Bitmap bitmap) {
        if(alterationsBitmaps.containsKey(alterationName)) {
            alterationsBitmaps.put(alterationName, bitmap);
            return true;
        }
        Log.i(TAG, "setAlterationBitmap: No alteration found in " + fileName + " with name " + alterationName);
        return false;
    }

    /**
     * Associate a recognized text to the alteration inside this test, if present
     * @param alterationName alterationName name of an existing alteration inside this test
     * @param text recognized text of the alteration that will be set
     * @modify jsonObject of this TestElement
     * @author Luca Moroldo - g3
     */
    public void setAlterationRecognizedText(String alterationName, String text) {

        JSONObject jsonAlterations = null;
        try {
            jsonAlterations = jsonObject.getJSONObject("alterations");
        } catch (JSONException e) {
            Log.i(TAG, "setAlterationRecognizedText: No alteration found in " + fileName + " with name " + alterationName);
            return;
        }

        try {

            JSONObject jsonAlteration = jsonAlterations.getJSONObject(alterationName);
            jsonAlteration.put("extracted_text", text);

        } catch (JSONException e) {
            Log.i(TAG, "setAlterationRecognizedText: There is no alteration with name " + alterationName + " inside test " + fileName);
        }
    }

    /**
     * @param alterationName alterationName name of an existing alteration inside this test
     * @param alterationConfidence value of the confidence of the alteration that will be set
     * @modify jsonObject of this TestElement
     * @author Luca Moroldo - g3
     */
    public void setAlterationConfidence(String alterationName, float alterationConfidence) {

        JSONObject jsonAlterations = null;
        try {
            jsonAlterations = jsonObject.getJSONObject("alterations");
        } catch (JSONException e) {
            Log.i(TAG, "setAlterationConfidence: No alteration found in " + fileName + " with name " + alterationName);
            return;
        }

        try {

            JSONObject jsonAlteration = jsonAlterations.getJSONObject(alterationName);
            jsonAlteration.put("confidence", Float.toString(alterationConfidence));

        } catch (JSONException e) {
            Log.i(TAG, "setAlterationConfidence: There is no alteration with name " + alterationName + " inside test " + fileName);
        }
    }

    @Override
    public String toString() { return jsonObject.toString(); }


}
