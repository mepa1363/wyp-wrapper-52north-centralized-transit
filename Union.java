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

public class Union extends AbstractSelfDescribingAlgorithm {

	@Override
	public List<String> getInputIdentifiers() {
		List<String> identifiers = new ArrayList<String>();
		identifiers.add("WalkshedCollection");
		return identifiers;
	}

	@Override
	public List<String> getOutputIdentifiers() {
		List<String> identifiers = new ArrayList<String>();
		identifiers.add("UnionResult");
		return identifiers;
	}

	@Override
	public Class getInputDataType(String identifier) {
		if (identifier.equals("WalkshedCollection")) {
			return LiteralStringBinding.class;
		}
		throw new RuntimeException("Error: Wrong identifier");
	}

	@Override
	public Class getOutputDataType(String identifier) {
		if (identifier.equals("UnionResult")) {
			return LiteralStringBinding.class;
		}
		throw new RuntimeException("Error: Wrong identifier");
	}

	@Override
	public Map<String, IData> run(Map<String, List<IData>> inputMap) {

		List<IData> walkshedList = inputMap.get("WalkshedCollection");

		IData walkshed_data = walkshedList.get(0);

		String walkshed = (String) walkshed_data.getPayload();

		IData union_result = new LiteralStringBinding(
				CallUnionService(walkshed));

		Map<String, IData> resultMap = new HashMap<String, IData>();

		resultMap.put("UnionResult", union_result);
		return resultMap;
	}

	public static String CallUnionService(String walkshed_collection) {
		URL url;
		String line;
		HttpURLConnection connection;
		StringBuilder sb = new StringBuilder();
		String url_string;

		String url_parameters;
		String USER_AGENT = "Mozilla/5.0";
		DataOutputStream wr;

		try {
			url_string = "http://127.0.0.1:9368/union";
			url_parameters = "walkshed_collection=" + walkshed_collection;

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
