package walkyourplace.transit.wps.centralized;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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

public class Aggregation extends AbstractSelfDescribingAlgorithm {

	@Override
	public List<String> getInputIdentifiers() {
		List<String> identifiers = new ArrayList<String>();
		identifiers.add("POI");
		identifiers.add("Crime");
		identifiers.add("Transit");
		identifiers.add("StartPoint");
		identifiers.add("WalkshedCollection");
		identifiers.add("WalkshedUnion");
		identifiers.add("DistanceDecayFunction");
		identifiers.add("WalkingTimePeriod");
		return identifiers;
	}

	@Override
	public List<String> getOutputIdentifiers() {
		List<String> identifiers = new ArrayList<String>();
		identifiers.add("AggregationResult");
		return identifiers;
	}

	@Override
	public Class getInputDataType(String identifier) {
		if (identifier.equals("POI") || identifier.equals("Crime")
				|| identifier.equals("Transit")
				|| identifier.equals("StartPoint")
				|| identifier.equals("WalkshedCollection")
				|| identifier.equals("WalkshedUnion")
				|| identifier.equals("DistanceDecayFunction")
				|| identifier.equals("WalkingTimePeriod")) {
			return LiteralStringBinding.class;
		}
		throw new RuntimeException("Error: Wrong identifier");
	}

	@Override
	public Class getOutputDataType(String identifier) {
		if (identifier.equals("AggregationResult")) {
			return LiteralStringBinding.class;
		}
		throw new RuntimeException("Error: Wrong identifier");
	}

	@Override
	public Map<String, IData> run(Map<String, List<IData>> inputMap) {

		List<IData> poiList = inputMap.get("POI");
		List<IData> crimeList = inputMap.get("Crime");
		List<IData> transitList = inputMap.get("Transit");
		List<IData> startpointList = inputMap.get("StartPoint");
		List<IData> walkshedcollectionList = inputMap.get("WalkshedCollection");
		List<IData> walkshedunionList = inputMap.get("WalkshedUnion");
		List<IData> distancedecayList = inputMap.get("DistanceDecayFunction");
		List<IData> walkingtimeperiodList = inputMap.get("WalkingTimePeriod");

		if (poiList.size() == 0 || crimeList.size() == 0
				|| transitList.size() == 0 || startpointList.size() == 0
				|| walkshedcollectionList.size() == 0
				|| walkshedunionList.size() == 0
				|| distancedecayList.size() == 0
				|| walkingtimeperiodList.size() == 0) {
			throw new RuntimeException("Invalid Input Parameters");
		}

		IData poi_data = poiList.get(0);
		IData crime_data = crimeList.get(0);
		IData transit_data = transitList.get(0);
		IData startpoint_data = startpointList.get(0);
		IData walkshedcollection__data = walkshedcollectionList.get(0);
		IData walkshedunion_data = walkshedunionList.get(0);
		IData distancedecay_data = distancedecayList.get(0);
		IData walkingtimeperiod_data = walkingtimeperiodList.get(0);

		String poi = (String) poi_data.getPayload();
		String crime = (String) crime_data.getPayload();
		String transit = (String) transit_data.getPayload();
		String startpoint = (String) startpoint_data.getPayload();
		String walkshedcollection = (String) walkshedcollection__data
				.getPayload();
		String walkshedunion = (String) walkshedunion_data.getPayload();
		String distancedecay = (String) distancedecay_data.getPayload();
		String walkingtimeperiod = (String) walkingtimeperiod_data.getPayload();

		IData result = new LiteralStringBinding(CallAggreagtionService(poi,
				crime, transit, startpoint, walkshedcollection, walkshedunion,
				distancedecay, walkingtimeperiod));

		Map<String, IData> resultMap = new HashMap<String, IData>();

		resultMap.put("AggregationResult", result);
		return resultMap;
	}

	public static String CallAggreagtionService(String poi, String crime,
			String transit, String start_point, String walkshed_collection,
			String walkshed_union, String distance_decay_function,
			String walking_time_period) {
		URL url;
		String line;
		HttpURLConnection connection;
		StringBuilder sb = new StringBuilder();
		String url_string;
		String url_parameters;
		String USER_AGENT = "Mozilla/5.0";
		DataOutputStream wr;

		try {

			url_string = "http://127.0.0.1:9364/aggregation";
			url_parameters = "start_point=" + start_point
					+ "&walkshed_collection=" + walkshed_collection
					+ "&walkshed_union=" + walkshed_union + "&poi=" + poi
					+ "&crime=" + crime + "&transit=" + transit
					+ "&walking_time_period=" + walking_time_period
					+ "&distance_decay_function=" + distance_decay_function;

			url = new URL(url_string);

			connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("User-Agent", USER_AGENT);
			connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			connection.setDoOutput(true);
			wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(url_parameters);
			wr.flush();
			wr.close();

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
