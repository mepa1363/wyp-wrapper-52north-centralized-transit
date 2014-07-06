package walkyourplace.transit.wps.centralized;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.util.URIUtil;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;
import org.n52.wps.server.AbstractSelfDescribingAlgorithm;

public class POI extends AbstractSelfDescribingAlgorithm {

	@Override
	public List<String> getInputIdentifiers() {
		List<String> identifiers = new ArrayList<String>();
		identifiers.add("Walkshed");
		return identifiers;
	}

	@Override
	public List<String> getOutputIdentifiers() {
		List<String> identifiers = new ArrayList<String>();
		identifiers.add("POIResult");
		return identifiers;
	}

	@Override
	public Class getInputDataType(String identifier) {
		if (identifier.equals("Walkshed")) {
			return LiteralStringBinding.class;
		}
		throw new RuntimeException("Error: Wrong identifier");
	}

	@Override
	public Class getOutputDataType(String identifier) {
		if (identifier.equals("POIResult")) {
			return LiteralStringBinding.class;
		}
		throw new RuntimeException("Error: Wrong identifier");
	}

	@Override
	public Map<String, IData> run(Map<String, List<IData>> inputMap) {

		List<IData> walkshedList = inputMap.get("Walkshed");

		IData walkshed_data = walkshedList.get(0);

		String walkshed = (String) walkshed_data.getPayload();

		IData poi_result = new LiteralStringBinding(CallPOIService(walkshed));

		Map<String, IData> resultMap = new HashMap<String, IData>();

		resultMap.put("POIResult", poi_result);
		return resultMap;
	}

	public static String CallPOIService(String walkshed) {
		URL url;
		String line;
		HttpURLConnection connection;
		StringBuilder sb = new StringBuilder();
		String url_string;

		try {
			url_string = "http://127.0.0.1:9365/poi?walkshed=" + walkshed;
			url_string = URIUtil.encodeQuery(url_string);
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
