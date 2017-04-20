package MDRevealer;

import java.util.ResourceBundle;
import gnu.gettext.GettextResource;

/* Util - Provides GNU gettext support for MDRevealer
 * Copyright (C) 2012-2013 Robin Clifford and Robert Clifford
 * This program is part of MDRevealer.
 * MDRevealer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MDRevealer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class Util {

	private static ResourceBundle catalog;
	static int success = 0;

	public static String _(String s) {
		/* Hack: Avoid missing resource errors */
		try {
			catalog = ResourceBundle.getBundle("MDRevealer");
			success++;
		} catch (Exception e) {
		}
		if (success == 0) {
			return s;
		} else {

			return GettextResource.gettext(catalog, s);
		}
	}
}
