package com.dsatab.data.filter;

import android.text.TextUtils;

import com.dsatab.data.enums.ItemType;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemSpecification;
import com.gandulf.guilib.data.OpenArrayAdapter;
import com.gandulf.guilib.data.OpenFilter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

public class ItemListFilter extends OpenFilter<Item> {

	private Collection<ItemType> types;

	private String category;

	public ItemListFilter(OpenArrayAdapter<Item> list) {
		super(list);
	}

	public Collection<ItemType> getTypes() {
		return types;
	}

	public void setTypes(Collection<ItemType> type) {
		this.types = type;
	}

	public void setType(ItemType type) {
		if (type != null)
			this.types = Arrays.asList(type);
		else
			this.types = null;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public boolean isFilterSet() {
		return constraint != null || types != null || category != null;
	}

	@Override
	public boolean filter(Item m) {
		boolean valid = true;
		if (types != null) {
			boolean found = false;

			for (ItemSpecification spec : m.getSpecifications()) {
				if (types.contains(spec.getType())) {
					found = true;
					break;
				}
			}

			valid &= found;
		}

		if (!TextUtils.isEmpty(category)) {
			valid &= category.equals(m.getCategory());
		}

		if (constraint != null) {
			valid &= m.getName().toLowerCase(Locale.GERMAN).startsWith(constraint);
		}

		return valid;
	}

}
