package dk.schau.OSkoleMio;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class DanishStringSorter
{
	public List<String> sort(List<String> elements)
	{
		final Collator dkCollator = Collator.getInstance(new Locale("da", "DK"));
		
		if (dkCollator == null)
		{
			Collections.sort(elements);
		}
		else
		{
			Collections.sort(elements, new Comparator<String>()
			{
				@Override
				public int compare(String one, String another)
				{
					return dkCollator.compare(one, another);
				}
			});
		}
		
		return elements;
	}
}
