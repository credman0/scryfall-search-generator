package com.searchmachine.querys;
import com.searchmachine.core.Card;

public abstract class SearchQuery {
	
	
	public abstract boolean matchesQuery(Card card);
}
