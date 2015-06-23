package be.arndep.camel.account.core.domain;

import lombok.EqualsAndHashCode;

/**
 * Created by arnaud on 23/06/15.
 */
@EqualsAndHashCode(of = "id")
public abstract class Identity<K> {
	private K id;

	public Identity() {
	}

	protected void setId(K id) {
		this.id = id;
	}

	public K getId() {
		return id;
	}
}
