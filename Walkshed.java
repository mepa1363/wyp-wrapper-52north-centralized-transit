package walkyourplace.transit.wps.centralized;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;
import org.n52.wps.server.AbstractSelfDescribingAlgorithm;

public class Walkshed extends AbstractSelfDescribingAlgorithm {

	@Override
	public List<String> getInputIdentifiers() {
		List<String> identifiers = new ArrayList<String>();
		identifiers.add("StartPoint");
		identifiers.add("WalkingPeriod");
		identifiers.add("WalkingSpeed");
		identifiers.add("WalkshedOutput");
		return identifiers;
	}

	@Override
	public List<String> getOutputIdentifiers() {
		List<String> identifiers = new ArrayList<String>();
		identifiers.add("WalkshedResult");
		return identifiers;
	}

	@Override
	public Class getInputDataType(String identifier) {
		if (identifier.equals("StartPoint")
				|| identifier.equals("WalkingPeriod")
				|| identifier.equals("WalkingSpeed")
				|| identifier.equals("WalkshedOutput")) {
			return LiteralStringBinding.class;
		}
		throw new RuntimeException("Error: Wrong identifier");
	}

	@Override
	public Class getOutputDataType(String identifier) {
		if (identifier.equals("WalkshedResult")) {
			return LiteralStringBinding.class;
		}
		throw new RuntimeException("Error: Wrong identifier");
	}

	@Override
	public Map<String, IData> run(Map<String, List<IData>> inputMap) {

		List<IData> pointList = inputMap.get("StartPoint");
		List<IData> timeList = inputMap.get("WalkingPeriod");
		List<IData> speedList = inputMap.get("WalkingSpeed");
		List<IData> outputList = inputMap.get("WalkshedOutput");

		if (pointList.size() == 0 || timeList.size() == 0
				|| speedList.size() == 0 || outputList.size() == 0) {
			throw new RuntimeException("Invalid Input Parameters");
		}

		IData point = pointList.get(0);
		IData time = timeList.get(0);
		IData speed = speedList.get(0);
		IData walkshedoutput = outputList.get(0);

		String fromPlace = (String) point.getPayload();
		String walkTime = (String) time.getPayload();
		String walkSpeed = (String) speed.getPayload();
		String output = (String) walkshedoutput.getPayload();

		IData result = new LiteralStringBinding(CallOTP(fromPlace, walkTime,
				walkSpeed, output));

		Map<String, IData> resultMap = new HashMap<String, IData>();

		resultMap.put("WalkshedResult", result);
		return resultMap;
	}

	public static String CallOTP(String fromPalce, String walkTime,
			String walkSpeed, String output) {
		URL url;
		String line;
		HttpURLConnection connection;
		StringBuilder sb = new StringBuilder();

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date date = new Date();
		String time = dateFormat.format(date);

		try {
			url = new URL(
					"http://gisciencegroup.ucalgary.ca:8080/opentripplanner-api-webapp/ws/iso?layers=traveltime&styles=mask&batch=true&fromPlace="
							+ fromPalce
							+ "&toPlace=51.09098935,-113.95179705&time="
							+ time
							+ "&mode=WALK&maxWalkDistance=10000&walkTime="
							+ walkTime
							+ "&walkSpeed="
							+ walkSpeed
							+ "&output="
							+ output);
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
