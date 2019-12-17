package com.searchmachine.core;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.searchmachine.querys.IdentityQuery;
import com.searchmachine.querys.SearchQuery;

public class JSONHandler {
	protected QueryParser queryParser = new QueryParser();
	protected Map<String, Card> cards = new HashMap<String, Card>();

	public JSONHandler (String cardsFileName){
		initilizeCardMap(cardsFileName);
	}

	public void initilizeCardMap(String cardsFileName) {
		cards = loadCardsFromJSON(cardsFileName);
	}

	public Set<String> getCardNames(){
		return cards.keySet();
	}

	protected Map<String, Card> loadCardsFromJSON(String cardsFileName) {
		InputStream cardsFile = getClass().getResourceAsStream(cardsFileName);
		Map<String, Card> cards = new HashMap<String, Card>();
		JSONObject cardJson = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(cardsFile));
			StringBuilder jsonBuilder = new StringBuilder();
			String jsonString = br.readLine();
			jsonBuilder.append(jsonString);
			while (br.ready()) {
				jsonString = br.readLine();
				jsonBuilder.append(jsonString);
			}
			cardJson = new JSONObject(jsonBuilder.toString());
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (String cardName : JSONObject.getNames(cardJson)) {
			JSONObject cardObject = cardJson.getJSONObject(cardName);
			JSONArray printingsJSON = null;
			String[] printings = null;
			if (cardObject.has("printings")) {
				printingsJSON = cardObject.getJSONArray("printings");
				printings = new String[printingsJSON.length()];
				for (int i = 0; i < printingsJSON.length(); i++) {
					printings[i] = printingsJSON.getString(i).toLowerCase();
				}
			}

			Character[] colorIdentity;
			if (cardObject.has("colorIdentity")) {
				JSONArray identityJson = cardObject.getJSONArray("colorIdentity");
				colorIdentity = new Character[identityJson.length()];
				for (int i = 0; i < colorIdentity.length; i++) {
					colorIdentity[i] = identityJson.getString(i).toLowerCase().charAt(0);
				}
				Arrays.sort(colorIdentity, IdentityQuery.ORDER_COMPARATOR);
			} else {
				colorIdentity = new Character[] { 'c' };
			}

			String cardType;
			if (cardObject.has("type")) {
				cardType = cardObject.getString("type").toLowerCase();
			} else {
				cardType = null;
			}

			String cardText;
			if (cardObject.has("text")) {
				cardText = cardObject.getString("text").toLowerCase();
			} else {
				cardText = "";
			}

			String power;
			if (cardObject.has("power")) {
				power = cardObject.getString("power").toLowerCase();
			} else {
				power = "-1";
			}

			String toughness;
			if (cardObject.has("toughness")) {
				toughness = cardObject.getString("toughness").toLowerCase();
			} else {
				toughness = "-1";
			}

			String manaCost;
			if (cardObject.has("manaCost")) {
				manaCost = cardObject.getString("manaCost").toLowerCase().replaceAll("\\{|\\}", "");
				
				// we do all this so we can sort with a custom comparator
				Character[] costArray = new Character[manaCost.length()];
				for (int i  = 0; i < manaCost.length(); i++) {
					costArray[i] = manaCost.charAt(i);
				}
				Arrays.sort(costArray, IdentityQuery.ORDER_COMPARATOR);
				char[] cCostArray = new char[costArray.length];
				for (int i  = 0; i < costArray.length; i++) {
					cCostArray[i] = costArray[i];
				}
				manaCost = new String(cCostArray);
			} else {
				manaCost = "";
			}

			JSONObject legalitiesJSON = null;
			Map<String, Boolean> legalities = null;
			if (cardObject.has("legalities")) {
				legalitiesJSON = cardObject.getJSONObject("legalities");

				legalities = new HashMap<String, Boolean>();
				for (String legality:legalitiesJSON.keySet()) {
					legalities.put(legality.toLowerCase(),
							legalitiesJSON.getString(legality).equals("Legal"));
				}
			}

			Card card = new Card(cardName.toLowerCase(), cardObject.getInt("convertedManaCost"), power, toughness, cardType, cardText, printings,
					colorIdentity, manaCost, legalities);
			cards.put(cardName, card);
		}

		return cards;
	}

	public Card getCard(String cardName) {
		return cards.get(cardName);
	}

	public String getCardText(String cardName) {
		return cards.get(cardName).getText();
	}

	public String getCardMana(String cardName) {
		return cards.get(cardName).getManaCost();
	}

	public Vector<String> getSearchResultList(String query) {
		String regex = "(\\S+)\"([^\"]*)\"|\"([^\"]*)\"|(\\S+)";
		Matcher queryMatcher = Pattern.compile(regex).matcher(query);

		ArrayList<SearchQuery> queryList = new ArrayList<SearchQuery>();

		// iterates through all the query tokens in the command from the user
		while (queryMatcher.find()) {
			SearchQuery searchQuery = queryParser.evaluate(queryMatcher.group());
			queryList.add(searchQuery);
		}

		Vector<String> resultList = new Vector<String>();

		for (String cardName : cards.keySet()) {
			Card card = cards.get(cardName);
			boolean queryTestFailed = false;
			for (SearchQuery searchQuery : queryList) {
				if (searchQuery != null)
					if (!searchQuery.matchesQuery(card)) {
						queryTestFailed = true;
						break;
					}
			}

			if (!queryTestFailed) {
				resultList.add(cardName);
			}

		}
		return resultList;
	}

}
