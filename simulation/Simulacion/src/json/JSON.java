package json;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import simulation.Navigation;
import simulation.Step;

public class JSON {
	
	private URL url;
	private String data;
	
	private JSONObject obj;
	
	private Navigation nav;
	
	/**
	 * This contructor creates a JSON object taking it from an URL.
	 * 
	 * @param url The URL or IP direction (String) where the JSON is.
	 * @param obj The URL object (coulb be null)
	 */
	public JSON(String url, URL obj) {
		try {
			this.url = new URL(url);
			this.data = URLToString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This constructor creates a JSON object from a JSON in a string.
	 * 
	 * @param data The JSON in a String.
	 */
	public JSON(String data) {
		this.data = data;
	}
	
	/**
	 * This methods parses the JSON to "Navigation" and "Step" objects. To do that, takes the JSON
	 * and extracts the general information of the JSON into a "Navigation" object. Then, takes the
	 * "steps" array from the JSON and creates "Step" objects with the elements on the array.
	 * Finally, adds the "Step" object to the ArrayList of "Step" at the created "Navigation" object.
	 * 
	 * @return Integer result code (ERROR: -1 | SUCCESS: 0)
	 */
	public int parseNav() {
		try {
			obj = new JSONObject(data);
			if (obj.getString("status").equals("OK")) {
				nav = new Navigation();
				JSONObject legs = obj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0);
				nav.setDistance(legs.getJSONObject("distance").getString("text"));
				nav.setDistance_v(legs.getJSONObject("distance").getInt("value"));
				nav.setDuration(legs.getJSONObject("duration").getString("text"));
				nav.setDuration_v(legs.getJSONObject("duration").getInt("value"));
				nav.setEnd_lat(legs.getJSONObject("end_location").getDouble("lat"));
				nav.setEnd_lng(legs.getJSONObject("end_location").getDouble("lng"));
				nav.setStart_lat(legs.getJSONObject("start_location").getDouble("lat"));
				JSONArray steps = legs.getJSONArray("steps");
				for (int i = 0; i < steps.length(); i++) {
					JSONObject tempStep = steps.getJSONObject(i);
					Step step = new Step();
					step.setDistance(tempStep.getJSONObject("distance").getString("text"));
					step.setDistance_v(tempStep.getJSONObject("distance").getInt("value"));
					step.setDuration(tempStep.getJSONObject("duration").getString("text"));
					step.setDuration_v(tempStep.getJSONObject("duration").getInt("value"));
					step.setEnd_lat(tempStep.getJSONObject("end_location").getDouble("lat"));
					step.setEnd_lng(tempStep.getJSONObject("end_location").getDouble("lng"));
					step.setStart_lat(tempStep.getJSONObject("start_location").getDouble("lat"));
					step.setStart_lng(tempStep.getJSONObject("start_location").getDouble("lng"));
					step.setInstruction(tempStep.getString("html_instructions"));
					step.setTravelMode(tempStep.getString("travel_mode"));
					nav.addStep(step);
				}
			} else {
				System.out.println("JSON - ERROR: Estado de ruta no válido.");
			} return 0;
		} catch (JSONException e) {
			System.out.println("JSON - ERROR: JSON no válido.");
			return -1;
		}
	}
	
	/**
	 * This xtracts the JSON text from an URL. 
	 * 
	 * @return The string with the complete data of the JSON.
	 * @throws IOException
	 */
	private String URLToString() throws IOException {
		Scanner scan = new Scanner(url.openStream());
		String data = scan.nextLine();
		while (scan.hasNext()) {
			data += scan.nextLine();
		}
		scan.close();
		return data;
	}
	
	public Navigation getNav() {
		return nav;
	}
	
}
