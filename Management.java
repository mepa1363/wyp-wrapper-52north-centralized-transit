package walkyourplace.transit.wps.centralized;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;
import org.n52.wps.server.AbstractSelfDescribingAlgorithm;

import es.unex.sextante.hydrology.strahlerOrder.StrahlerOrderAlgorithm;

public class Management extends AbstractSelfDescribingAlgorithm {

	@Override
	public List<String> getInputIdentifiers() {
		List<String> identifiers = new ArrayList<String>();
		identifiers.add("StartPoint");
		identifiers.add("StartTime");
		identifiers.add("WalkingTimePeriod");
		identifiers.add("WalkingSpeed");
		identifiers.add("BusWaitingTime");
		identifiers.add("BusRideTime");
		identifiers.add("DistanceDecayFunction");
		return identifiers;
	}

	@Override
	public List<String> getOutputIdentifiers() {
		List<String> identifiers = new ArrayList<String>();
		identifiers.add("AccessibilityScore");
		return identifiers;
	}

	@Override
	public Class getInputDataType(String identifier) {
		if (identifier.equals("StartPoint") || identifier.equals("StartTime")
				|| identifier.equals("WalkingTimePeriod")
				|| identifier.equals("WalkingSpeed")
				|| identifier.equals("BusWaitingTime")
				|| identifier.equals("BusRideTime")
				|| identifier.equals("DistanceDecayFunction")) {
			return LiteralStringBinding.class;
		}
		throw new RuntimeException("Error: Wrong identifier");
	}

	@Override
	public Class getOutputDataType(String identifier) {
		if (identifier.equals("AccessibilityScore")) {
			return LiteralStringBinding.class;
		}
		throw new RuntimeException("Error: Wrong identifier");
	}

	@Override
	public Map<String, IData> run(Map<String, List<IData>> inputMap) {

		List<IData> startpointList = inputMap.get("StartPoint");
		List<IData> starttimeList = inputMap.get("StartTime");
		List<IData> walkingtimeperiodList = inputMap.get("WalkingTimePeriod");
		List<IData> walkingspeedList = inputMap.get("WalkingSpeed");
		List<IData> buswaitingtimeList = inputMap.get("BusWaitingTime");
		List<IData> busridetimeList = inputMap.get("BusRideTime");
		List<IData> distancedecayfunctionList = inputMap
				.get("DistanceDecayFunction");

		if (startpointList.size() == 0 || starttimeList.size() == 0
				|| walkingtimeperiodList.size() == 0
				|| walkingspeedList.size() == 0
				|| buswaitingtimeList.size() == 0
				|| busridetimeList.size() == 0
				|| distancedecayfunctionList.size() == 0) {
			throw new RuntimeException("Invalid Input Parameters");
		}

		IData startpoint_data = startpointList.get(0);
		IData starttime_data = starttimeList.get(0);
		IData walkingtimeperiod_data = walkingtimeperiodList.get(0);
		IData walkingspeed_data = walkingspeedList.get(0);
		IData buswaitingtime_data = buswaitingtimeList.get(0);
		IData busridetime_data = busridetimeList.get(0);
		IData distancedecayfunction_data = distancedecayfunctionList.get(0);

		String start_point = (String) startpoint_data.getPayload();
		String start_time = (String) starttime_data.getPayload();
		String walking_time_period = (String) walkingtimeperiod_data
				.getPayload();
		String walking_speed = (String) walkingspeed_data.getPayload();
		String bus_waiting_time = (String) buswaitingtime_data.getPayload();
		String bus_ride_time = (String) busridetime_data.getPayload();
		String distance_decay_function = (String) distancedecayfunction_data
				.getPayload();

		IData result = new LiteralStringBinding(CallManagementService(
				start_point, start_time, walking_time_period, walking_speed,
				bus_waiting_time, bus_ride_time, distance_decay_function));

		Map<String, IData> resultMap = new HashMap<String, IData>();

		resultMap.put("AccessibilityScore", result);
		return resultMap;
	}

	public static String CallManagementService(String start_point,
			String start_time, String walking_time_period,
			String walking_speed, String bus_waiting_time,
			String bus_ride_time, String distance_decay_function) {
		URL url;
		String line;
		HttpURLConnection connection;
		StringBuilder sb = new StringBuilder();
		String url_string;

		try {
			
			url_string = "http://127.0.0.1:9363/management?start_point="
					+ start_point + "&start_time=" + start_time
					+ "&walking_time_period=" + walking_time_period
					+ "&walking_speed=" + walking_speed + "&bus_waiting_time="
					+ bus_waiting_time + "&bus_ride_time=" + bus_ride_time
					+ "&distance_decay_function=" + distance_decay_function;

			url = new URL(url_string);
			connection = (HttpURLConnection) url.openConnection();

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			rd.close();

			connection.disconnect();

		} catch (Exception e) {
			System.out.println("Errors...");
		}

		return sb.toString();
	}

}
